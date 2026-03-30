import { getToken } from "../auth/token-service.js";
import { logger } from "../logging/logger.js";
import { saveErrorLog } from "../logging/error-log-service.js";
import * as validationUtil from "../db/validation-util.js";
import { insertBatch, persistContratosFiscais } from "./repository.js";
import type { EndpointDefinition } from "./endpoints/types.js";

const sleep = (ms: number) => new Promise((r) => setTimeout(r, ms));

async function fetchFromSefaz(apiUrl: string, opts?: { logToConsole?: boolean }): Promise<any | null> {
  const startTime = Date.now();
  try {
    const token = await getToken();
    logger.apiEndpoint("GET", apiUrl);
    if (opts?.logToConsole) {
      logger.consumptionHttpGet(apiUrl);
    }

    const response = await fetch(apiUrl, {
      method: "GET",
      headers: {
        Authorization: `Bearer ${token}`,
        Accept: "application/json",
        "User-Agent": "SEFAZ-Consumer/1.0",
      },
    });

    const responseTime = Date.now() - startTime;
    const body = await response.text();
    const requestSize = apiUrl.length;
    const responseSize = body.length;

    logger.logApiCall(apiUrl, "GET", response.status, responseTime, requestSize, responseSize);

    if (!response.ok) {
      const err = new Error(`HTTP ${response.status} para ${apiUrl}`);
      logger.logOperationError("API_CLIENT", "API_CALL", responseTime, err, "URL", apiUrl, "STATUS", response.status);
      saveErrorLog("API_CLIENT", `API ERRO GET ${apiUrl} | STATUS: ${response.status}`, err, "API_CALL").catch(() => {});
      return null;
    }

    try {
      return JSON.parse(body);
    } catch {
      return null;
    }
  } catch (e) {
    const responseTime = Date.now() - startTime;
    logger.logOperationError("API_CLIENT", "API_CALL", responseTime, e, "URL", apiUrl);
    return null;
  }
}

function buildUrl(baseUrl: string, params: Record<string, any>): string {
  const url = new URL(baseUrl);
  for (const [key, value] of Object.entries(params)) {
    if (value != null) url.searchParams.set(key, String(value));
  }
  return url.toString();
}

function processResponse(data: any, _endpoint: EndpointDefinition): Record<string, any>[] {
  let items: any[] = [];

  if (Array.isArray(data)) {
    items = data;
  } else if (data && typeof data === "object") {
    if (Array.isArray(data.content)) {
      items = data.content;
    } else if (data.result?.dados?.colecao && Array.isArray(data.result.dados.colecao)) {
      items = data.result.dados.colecao;
    } else {
      items = [data];
    }
  }

  return items;
}

interface ConsumeOptions {
  ugFilter?: string[];
  quietBanners?: boolean;
  /** Com `quietBanners: false`, evita o bloco grande de fim e usa só linha resumida (ex.: scheduler). */
  quietEndBanner?: boolean;
  /** Imprime cada GET no console (útil para acompanhar requisições no agendamento). */
  verboseRequestLog?: boolean;
  /** Não repete o bloco Modo/Período (ex.: loop mensal em consumirAnoInteiro) */
  skipContextBanner?: boolean;
  filters?: Record<string, any>;
}

const CONSUME_PERSIST_BATCH_SIZE = 1000;

const MONTHLY_ENDPOINTS = new Set([
  "Pagamento",
  "Liquidacao",
  "Empenho",
  "EmpenhoMensal",
  "ConsultaGerencial",
  "OrdemFornecimento",
  "DespesaDetalhada",
  "PrevisaoRealizacaoReceita",
  "ContratosFiscais",
  "DespesaConvenio",
  "Receita",
]);

interface PeriodRef {
  ano: number;
  mes: number;
}

function hasExplicitYearOrMonthFilter(filters: Record<string, any>): boolean {
  const yearKeys = [
    "dtAnoExercicioCTBFiltro",
    "dtAnoExercicioFiltro",
    "dtAnoExercicioEmpFiltro",
    "nuAnoLancamentoFiltro",
    "nuAnoCelebracaoFiltro",
  ];
  const monthKeys = [
    "nuMesFiltro",
    "nuMesRecebimentoFiltro",
    "nuMesLancamentoFiltro",
    "nuMesCelebracaoFiltro",
  ];
  return [...yearKeys, ...monthKeys].some((key) => filters[key] != null);
}

async function resolveIncrementalPeriods(
  endpoint: EndpointDefinition,
  anoAtual: number,
  mesAtual: number,
  filters: Record<string, any>
): Promise<PeriodRef[]> {
  // Quando usuário informa período explicitamente, respeita exatamente o filtro.
  if (hasExplicitYearOrMonthFilter(filters)) {
    return [{ ano: anoAtual, mes: mesAtual }];
  }

  if (!MONTHLY_ENDPOINTS.has(endpoint.name)) {
    return [{ ano: anoAtual, mes: mesAtual }];
  }

  // Incremental padrão: últimos 2 meses (com ano/mês corretos, inclusive virada de ano)
  const ultimos2Meses = await validationUtil.getUltimos2Meses();
  return ultimos2Meses.map(([ano, mes]) => ({ ano, mes }));
}

function formatExplicitPeriod(filters: Record<string, any>): string {
  const y =
    filters.dtAnoExercicioCTBFiltro ??
    filters.dtAnoExercicioFiltro ??
    filters.dtAnoExercicioEmpFiltro ??
    filters.nuAnoLancamentoFiltro ??
    filters.nuAnoCelebracaoFiltro;
  const m =
    filters.nuMesFiltro ??
    filters.nuMesRecebimentoFiltro ??
    filters.nuMesLancamentoFiltro ??
    filters.nuMesCelebracaoFiltro;
  if (y != null && m != null) return `${String(m).padStart(2, "0")}/${y}`;
  if (y != null) return `ano exercício ${y}`;
  if (m != null) return `mês ${m}`;
  return "filtros de período definidos";
}

function formatPeriodosLista(periodos: PeriodRef[]): string {
  return periodos.map((p) => `${String(p.mes).padStart(2, "0")}/${p.ano}`).join(", ");
}

async function buildConsumptionContext(
  endpoint: EndpointDefinition,
  options: ConsumeOptions,
  anoAtual: number,
  mesAtual: number,
  isIndependente: boolean,
  hasBancoData: boolean,
  isLoadingAll: boolean,
  ugs: string[],
  filters: Record<string, any>
): Promise<{ entity: string; modo: string; periodo: string; escopo: string }> {
  const entity = endpoint.name;
  const escopo = options.ugFilter?.length
    ? `UG(s): ${options.ugFilter.join(", ")}`
    : `todas as UGs (${ugs.length})`;

  if (isIndependente) {
    return {
      entity,
      modo: "INDEPENDENTE (API sem iteração por UG/período)",
      periodo: "—",
      escopo: "requisição única",
    };
  }

  if (isLoadingAll) {
    const anoMin = anoAtual - 5;
    let periodo = `Exercício ${anoMin} a ${anoAtual} (6 anos)`;
    if (endpoint.requerIteracaoCdGestao) {
      periodo += "; 12 meses/ano × cd_gestão";
    } else if (endpoint.requerIteracaoEmpenhos) {
      periodo += "; por empenho cadastrado localmente";
    } else {
      periodo += "; 1 requisição por UG e ano";
    }
    return {
      entity,
      modo: "CARGA COMPLETA (sem dados na tabela — primeira carga)",
      periodo,
      escopo,
    };
  }

  if (hasExplicitYearOrMonthFilter(filters)) {
    return {
      entity,
      modo: "PERÍODO EXPLICITO (rota manual / por-ano / por-mês)",
      periodo: formatExplicitPeriod(filters),
      escopo,
    };
  }

  const periodos = await resolveIncrementalPeriods(endpoint, anoAtual, mesAtual, filters);
  const periodoStr = formatPeriodosLista(periodos);
  const modo = MONTHLY_ENDPOINTS.has(endpoint.name)
    ? "INCREMENTAL (últimos 2 meses — base já possui dados)"
    : "INCREMENTAL (exercício atual — base já possui dados)";

  return {
    entity,
    modo,
    periodo: periodoStr,
    escopo,
  };
}

export async function consumirPersistir(
  endpoint: EndpointDefinition,
  options: ConsumeOptions = {}
): Promise<number> {
  const { ugFilter, quietBanners = false, quietEndBanner = false, verboseRequestLog = false, skipContextBanner = false, filters = {} } = options;
  logger.generateCorrelationId();
  const logRequests = verboseRequestLog === true;
  logger.setContext("CONSUMER", `CONSUME_${endpoint.name.toUpperCase()}`);

  const startTime = Date.now();
  const pendingItems: Record<string, any>[] = [];
  let totalProcessed = 0;
  let contratosFiscaisDeleted = false;

  const flushPendingItems = async (force = false) => {
    if (!force && pendingItems.length < CONSUME_PERSIST_BATCH_SIZE) return;
    if (!pendingItems.length) return;

    const batchToPersist = pendingItems.splice(0, pendingItems.length);
    if (endpoint.tableName === "consumer_sefaz.contratos_fiscais") {
      await persistContratosFiscais(batchToPersist, { deleteExisting: !contratosFiscaisDeleted });
      contratosFiscaisDeleted = true;
    } else {
      await insertBatch(endpoint, batchToPersist);
    }
    totalProcessed += batchToPersist.length;
  };

  const sinkItems = async (items: Record<string, any>[]) => {
    if (!items.length) return;
    pendingItems.push(...items);
    if (pendingItems.length >= CONSUME_PERSIST_BATCH_SIZE) {
      await flushPendingItems(true);
    }
  };

  try {
    const anoAtual = await validationUtil.getAnoAtual();
    const mesAtual = await validationUtil.getMesAtual();
    const isIndependente = !endpoint.parametrosRequeridos &&
      Object.keys(endpoint.getParametrosAtual(null, anoAtual, mesAtual, filters)).length === 0;

    const ugs = isIndependente ? [] : (ugFilter && ugFilter.length > 0 ? ugFilter : await validationUtil.getUgs());
    let hasBancoData = true;
    let isLoadingAll = false;
    if (!isIndependente) {
      hasBancoData = await validationUtil.isPresenteBanco(endpoint.tableName, endpoint.dtAnoPadrao);
      isLoadingAll = !hasBancoData && !filters.dtAnoExercicioCTBFiltro && !filters.dtAnoExercicioFiltro && !filters.nuMesFiltro;
    }

    if (!skipContextBanner) {
      const ctx = await buildConsumptionContext(
        endpoint,
        options,
        anoAtual,
        mesAtual,
        isIndependente,
        hasBancoData,
        isLoadingAll,
        ugs,
        filters
      );
      logger.consumptionSessionOpen(
        {
          entity: ctx.entity,
          modo: ctx.modo,
          periodo: ctx.periodo,
          escopo: ctx.escopo,
          description: `Consumindo ${endpoint.name}`,
        },
        { quiet: quietBanners }
      );
    } else if (!quietBanners) {
      logger.consumptionStart(endpoint.name, `Consumindo ${endpoint.name}`);
    } else {
      logger.consumptionStartQuiet(endpoint.name, `Consumindo ${endpoint.name}`);
    }

    if (isIndependente) {
      const data = await fetchFromSefaz(endpoint.url, { logToConsole: logRequests });
      if (data) {
        const items = processResponse(data, endpoint);
        await sinkItems(items);
      }
    } else {
      if (isLoadingAll) {
        await fetchAllYears(endpoint, ugs, anoAtual, mesAtual, filters, quietBanners, sinkItems, logRequests);
      } else {
        await fetchCurrentPeriod(endpoint, ugs, anoAtual, mesAtual, filters, quietBanners, sinkItems, logRequests);
      }
    }

    await flushPendingItems(true);

    if (totalProcessed === 0) {
      logger.warning("CONSUMER", `Nenhum dado encontrado para ${endpoint.name}`);
      if (!quietBanners) {
        if (quietEndBanner) {
          logger.consumptionEndQuiet(endpoint.name, "0 registros", Date.now() - startTime, { silent: false });
        } else {
          logger.consumptionEnd(endpoint.name, "0 registros", Date.now() - startTime);
        }
      } else {
        logger.consumptionEndQuiet(endpoint.name, "0 registros", Date.now() - startTime, { silent: skipContextBanner });
      }
      return 0;
    }

    logger.logSavingStart();
    const duration = Date.now() - startTime;
    logger.logDataSaved(totalProcessed);
    if (!quietBanners) {
      if (quietEndBanner) {
        logger.consumptionEndQuiet(endpoint.name, `${totalProcessed} registros`, duration, { silent: false });
      } else {
        logger.consumptionEnd(endpoint.name, `${totalProcessed} registros processados`, duration);
      }
    } else {
      logger.consumptionEndQuiet(endpoint.name, `${totalProcessed} registros`, duration, { silent: skipContextBanner });
    }

    const section = logger.markdownSection(`Consumo ${endpoint.name}`);
    section.success(`${totalProcessed} registros processados`, duration);
    section.log();

    return totalProcessed;
  } catch (e) {
    const duration = Date.now() - startTime;
    logger.error("CONSUMER", `Erro ao consumir ${endpoint.name}`, e);
    if (!quietBanners) {
      if (quietEndBanner) {
        logger.consumptionEndQuiet(endpoint.name, "ERRO", duration, { silent: false });
      } else {
        logger.consumptionEnd(endpoint.name, "ERRO", duration);
      }
    }
    throw e;
  } finally {
    logger.clearContext();
  }
}

async function fetchAllYears(
  endpoint: EndpointDefinition,
  ugs: string[],
  anoAtual: number,
  mesAtual: number,
  filters: Record<string, any>,
  quiet: boolean,
  sinkItems: (items: Record<string, any>[]) => Promise<void>,
  logRequests: boolean
) {
  const years = Array.from({ length: 6 }, (_, i) => anoAtual - i);
  const totalSteps = ugs.length * years.length;
  let step = 0;

  for (const ugCd of ugs) {
    for (const ano of years) {
      step++;
      if (!quiet) {
        logger.consumptionProgress(endpoint.name, "Processando UGs/Anos", step, totalSteps, `UG: ${ugCd} Ano: ${ano}`);
      }

      if (endpoint.requerIteracaoCdGestao) {
        await fetchWithCdGestao(endpoint, ugCd, ano, mesAtual, filters, true, sinkItems, logRequests);
      } else if (endpoint.requerIteracaoEmpenhos) {
        await fetchWithEmpenhos(endpoint, ugCd, ano, filters, sinkItems, logRequests);
      } else {
        const params = endpoint.getParametrosTodosAnos(ugCd, ano, filters);
        const url = buildUrl(endpoint.url, params);
        const data = await fetchFromSefaz(url, { logToConsole: logRequests });
        if (data) {
          const items = processResponse(data, endpoint);
          enrichItems(items, endpoint, ugCd, ano, null);
          await sinkItems(items);
        }
      }
      await sleep(200);
    }
  }
}

async function fetchCurrentPeriod(
  endpoint: EndpointDefinition,
  ugs: string[],
  anoAtual: number,
  mesAtual: number,
  filters: Record<string, any>,
  quiet: boolean,
  sinkItems: (items: Record<string, any>[]) => Promise<void>,
  logRequests: boolean
) {
  const periodos = await resolveIncrementalPeriods(endpoint, anoAtual, mesAtual, filters);

  for (let i = 0; i < ugs.length; i++) {
    const ugCd = ugs[i];
    if (!quiet) {
      logger.consumptionProgress(endpoint.name, "Processando UGs", i + 1, ugs.length, `UG: ${ugCd}`);
    }

    if (endpoint.requerIteracaoCdGestao) {
      await fetchWithCdGestao(endpoint, ugCd, anoAtual, mesAtual, filters, false, sinkItems, logRequests);
    } else if (endpoint.requerIteracaoEmpenhos) {
      await fetchWithEmpenhos(endpoint, ugCd, anoAtual, filters, sinkItems, logRequests);
    } else {
      for (const periodo of periodos) {
        const params = endpoint.getParametrosAtual(ugCd, periodo.ano, periodo.mes, filters);
        const url = buildUrl(endpoint.url, params);
        const data = await fetchFromSefaz(url, { logToConsole: logRequests });
        if (data) {
          const items = processResponse(data, endpoint);
          enrichItems(items, endpoint, ugCd, periodo.ano, periodo.mes);
          await sinkItems(items);
        }
      }
    }
    await sleep(200);
  }
}

async function fetchWithCdGestao(
  endpoint: EndpointDefinition,
  ugCd: string,
  ano: number,
  mesAtual: number,
  filters: Record<string, any>,
  allYears: boolean,
  sinkItems: (items: Record<string, any>[]) => Promise<void>,
  logRequests: boolean
) {
  const cdGestaoList = endpoint.name === "Empenho" || endpoint.name === "EmpenhoMensal"
    ? await validationUtil.cdGestaoSeed()
    : await validationUtil.cdGestaoPorUgAno(ugCd, ano);

  if (cdGestaoList.length === 0) {
    logger.apiSkipUg(ugCd, "Sem cd_gestao encontrado");
    return;
  }

  const periodos: PeriodRef[] = allYears
    ? Array.from({ length: 12 }, (_, i) => ({ ano, mes: i + 1 }))
    : (await validationUtil.getUltimos2Meses()).map(([anoRef, mesRef]) => ({ ano: anoRef, mes: mesRef }));

  for (const cdGestao of cdGestaoList) {
    for (const periodo of periodos) {
      const params = allYears
        ? { ...endpoint.getParametrosTodosAnos(ugCd, periodo.ano, filters), cdGestao, nuMes: periodo.mes }
        : { ...endpoint.getParametrosAtual(ugCd, periodo.ano, periodo.mes, filters), cdGestao };
      const url = buildUrl(endpoint.url, params);
      const data = await fetchFromSefaz(url, { logToConsole: logRequests });
      if (data) {
        const items = processResponse(data, endpoint);
        enrichItems(items, endpoint, ugCd, periodo.ano, periodo.mes);
        await sinkItems(items);
      }
      await sleep(100);
    }
  }
}

async function fetchWithEmpenhos(
  endpoint: EndpointDefinition,
  ugCd: string,
  ano: number,
  filters: Record<string, any>,
  sinkItems: (items: Record<string, any>[]) => Promise<void>,
  logRequests: boolean
) {
  try {
    const { query: dbQuery } = await import("../db/client.js");
    const result = await dbQuery(
      `SELECT cd_gestao, sq_empenho FROM consumer_sefaz.empenho WHERE cd_unidade_gestora = $1 AND dt_ano_exercicio_ctb = $2 AND cd_gestao IS NOT NULL`,
      [ugCd, ano]
    );
    for (const row of result.rows) {
      const params = {
        ...endpoint.getParametrosTodosAnos(ugCd, ano, filters),
        cdGestao: row.cd_gestao,
        sqEmpenho: row.sq_empenho,
      };
      const url = buildUrl(endpoint.url, params);
      const data = await fetchFromSefaz(url, { logToConsole: logRequests });
      if (data) {
        const items = processResponse(data, endpoint);
        await sinkItems(items);
      }
      await sleep(50);
    }
  } catch (e) {
    logger.error("CONSUMER", `Erro ao iterar empenhos para ${endpoint.name} UG: ${ugCd}`, e);
  }
}

function enrichItems(items: any[], endpoint: EndpointDefinition, ugCd: string | null, ano: number | null, mes: number | null) {
  for (const item of items) {
    if (ugCd && !item.cdUnidadeGestora && !item._cdUnidadeGestora) {
      item._cdUnidadeGestora = ugCd;
    }
    if (ano && !item.dtAnoExercicioCTB && !item._dtAnoExercicioCTB) {
      item._dtAnoExercicioCTB = ano;
    }
    if (mes && !item.nuMes && !item._nuMes) {
      item._nuMes = mes;
    }
  }
}
