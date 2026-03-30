import { consumirPersistir } from "./api-service.js";
import { logger } from "../logging/logger.js";
import type { EndpointDefinition } from "./endpoints/types.js";

const ANO_MIN = 2000;
const ANO_MAX = 2030;

function validateAno(ano: number) {
  if (ano < ANO_MIN || ano > ANO_MAX) throw new Error(`Ano deve estar entre ${ANO_MIN} e ${ANO_MAX}`);
}

function validateMes(mes: number) {
  if (mes < 1 || mes > 12) throw new Error("Mês deve estar entre 1 e 12");
}

export async function consumirAnoInteiro(
  endpoint: EndpointDefinition,
  ano: number,
  cdUnidadeGestora?: string,
  hasMonthlyIteration = true
): Promise<Record<string, any>> {
  validateAno(ano);
  const ugFilter = cdUnidadeGestora ? [cdUnidadeGestora.trim()] : undefined;
  const desc = `${endpoint.name} ano ${ano}${cdUnidadeGestora ? ` UG ${cdUnidadeGestora}` : ""}`;
  const startTime = Date.now();
  let totalRecords = 0;

  logger.consumptionSessionOpen({
    entity: endpoint.name,
    modo: hasMonthlyIteration ? "Por período: ano completo (12 meses)" : "Por período: exercício do ano (uma passagem)",
    periodo: hasMonthlyIteration ? `Ano ${ano}, meses 1–12` : `Ano ${ano}`,
    escopo: cdUnidadeGestora ? `UG ${cdUnidadeGestora}` : "todas as UGs",
    description: `Consumindo ${desc}`,
  });

  if (hasMonthlyIteration) {
    for (let mes = 1; mes <= 12; mes++) {
      logger.consumptionProgress(endpoint.name, `Processando meses ano ${ano}`, mes, 12, `Mês: ${mes}`);
      try {
        const filters: Record<string, any> = {};
        if (endpoint.name === "Pagamento" || endpoint.name === "Liquidacao" || endpoint.name === "Empenho" || endpoint.name === "ConsultaGerencial" || endpoint.name === "OrdemFornecimento") {
          filters.dtAnoExercicioCTBFiltro = ano;
          filters.nuMesFiltro = mes;
        } else if (endpoint.name === "DespesaDetalhada" || endpoint.name === "PrevisaoRealizacaoReceita") {
          filters.dtAnoExercicioCTBFiltro = ano;
          filters.nuMesFiltro = mes;
        } else if (endpoint.name === "ContratosFiscais") {
          filters.dtAnoExercicioFiltro = ano;
          filters.nuMesFiltro = mes;
        } else if (endpoint.name === "DespesaConvenio" || endpoint.name === "Receita") {
          filters.nuAnoLancamentoFiltro = ano;
          filters.nuMesLancamentoFiltro = mes;
        } else {
          filters.dtAnoExercicioFiltro = ano;
        }

        const processed = await consumirPersistir(endpoint, { ugFilter, quietBanners: true, skipContextBanner: true, filters });
        totalRecords += processed;

        if (mes < 12) await new Promise((r) => setTimeout(r, 400));
      } catch (e) {
        logger.error("CONSUMER", `Erro ao processar ano ${ano} mês ${mes}: ${e instanceof Error ? e.message : e}`);
        throw new Error(`Erro ao consumir ${endpoint.name} para ano ${ano} mês ${mes}: ${e instanceof Error ? e.message : e}`, {
          cause: e,
        });
      }
    }
  } else {
    const filters: Record<string, any> = {};
    if (endpoint.name.includes("Contrato") && !endpoint.name.includes("Fiscais")) {
      filters.dtAnoExercicioFiltro = ano;
    } else {
      filters.dtAnoExercicioCTBFiltro = ano;
    }
    const processed = await consumirPersistir(endpoint, { ugFilter, quietBanners: true, skipContextBanner: true, filters });
    totalRecords = processed;
  }

  const executionTimeMs = Date.now() - startTime;
  logger.consumptionEnd(endpoint.name, `${totalRecords} registros processados (${desc})`, executionTimeMs);

  const resumo: Record<string, any> = {
    status: "SUCCESS",
    recordsProcessed: totalRecords,
    ano,
    executionTimeMs,
    message: `Execução por ano inteiro concluída. ${totalRecords} registros processados.`,
  };
  if (cdUnidadeGestora) resumo.cdUnidadeGestora = cdUnidadeGestora;
  return resumo;
}

export async function consumirAnoEMes(
  endpoint: EndpointDefinition,
  ano: number,
  mes: number,
  cdUnidadeGestora?: string
): Promise<number> {
  validateAno(ano);
  validateMes(mes);
  const ugFilter = cdUnidadeGestora ? [cdUnidadeGestora.trim()] : undefined;

  const filters: Record<string, any> = {};
  if (endpoint.name === "Pagamento" || endpoint.name === "Liquidacao" || endpoint.name === "Empenho" || endpoint.name === "ConsultaGerencial" || endpoint.name === "OrdemFornecimento") {
    filters.dtAnoExercicioCTBFiltro = ano;
    filters.nuMesFiltro = mes;
  } else if (endpoint.name === "DespesaDetalhada" || endpoint.name === "PrevisaoRealizacaoReceita") {
    filters.dtAnoExercicioCTBFiltro = ano;
    filters.nuMesFiltro = mes;
  } else if (endpoint.name === "ContratosFiscais") {
    filters.dtAnoExercicioFiltro = ano;
    filters.nuMesFiltro = mes;
  } else if (endpoint.name === "DespesaConvenio" || endpoint.name === "Receita") {
    filters.nuAnoLancamentoFiltro = ano;
    filters.nuMesLancamentoFiltro = mes;
  } else {
    filters.dtAnoExercicioFiltro = ano;
  }

  return consumirPersistir(endpoint, { ugFilter, filters });
}

export function needsMonthlyIteration(endpointName: string): boolean {
  return [
    "Pagamento", "Liquidacao", "Empenho", "ConsultaGerencial",
    "OrdemFornecimento", "DespesaDetalhada", "PrevisaoRealizacaoReceita",
    "ContratosFiscais", "DespesaConvenio", "Receita"
  ].includes(endpointName);
}
