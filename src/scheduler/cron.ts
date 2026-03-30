import cron from "node-cron";
import { env } from "../env.js";
import { logger, type SchedulerEntityReportRow } from "../logging/logger.js";
import { consumirPersistir } from "../consumer/api-service.js";
import { tryAcquireReceitaLock, releaseReceitaLock } from "./lock.js";
import { SCHEDULER_CONSUME_CONSOLE } from "./consume-console-options.js";
import * as endpoints from "../consumer/endpoints/index.js";

const sleep = (ms: number) => new Promise((r) => setTimeout(r, ms));

let isFirstExecution = true;
let lastExecutionTime: Date | null = null;
let isRunning = false;

async function executeEntity(
  name: string,
  endpoint: endpoints.EndpointDefinition,
  results: Record<string, number>,
  reportRows: SchedulerEntityReportRow[]
) {
  const t0 = Date.now();
  try {
    const processed = await consumirPersistir(endpoint, { ...SCHEDULER_CONSUME_CONSOLE });
    results[name] = processed;
    reportRows.push({ name, records: processed, durationMs: Date.now() - t0, status: "ok" });
  } catch (e) {
    logger.error("SCHEDULER", `Erro ao executar ${name}`, e);
    results[name] = 0;
    reportRows.push({
      name,
      records: 0,
      durationMs: Date.now() - t0,
      status: "error",
      errorMessage: e instanceof Error ? e.message : String(e),
    });
  }
}

export async function executeAllEntities(): Promise<Record<string, number>> {
  if (isRunning) {
    logger.warning("SCHEDULER", "Execução já em andamento, pulando");
    return {};
  }

  isRunning = true;
  const correlationId = logger.generateCorrelationId();
  logger.setContext("SCHEDULER", "EXECUTE_ALL");
  const startTime = Date.now();
  const startedAtIso = new Date(startTime).toISOString();
  const results: Record<string, number> = {};
  const reportRows: SchedulerEntityReportRow[] = [];

  const section = logger.markdownSection("Execução Automática Completa");

  try {
    logger.logScheduledExecutionStart();

    // 1. UnidadeGestora
    await executeEntity("UnidadeGestora", endpoints.unidadeGestora, results, reportRows);
    section.success(`UnidadeGestora: ${results.UnidadeGestora ?? 0} registros`);
    await sleep(2000);

    // 2. ConsultaGerencial
    await executeEntity("ConsultaGerencial", endpoints.consultaGerencial, results, reportRows);
    section.success(`ConsultaGerencial: ${results.ConsultaGerencial ?? 0} registros`);
    await sleep(2000);

    // 3. ContratosFiscais
    await executeEntity("ContratosFiscais", endpoints.contratosFiscais, results, reportRows);
    section.success(`ContratosFiscais: ${results.ContratosFiscais ?? 0} registros`);
    await sleep(2000);

    // 4. Contrato
    await executeEntity("Contrato", endpoints.contrato, results, reportRows);
    section.success(`Contrato: ${results.Contrato ?? 0} registros`);
    await sleep(2000);

    // 5. ContratoEmpenho
    await executeEntity("ContratoEmpenho", endpoints.contratoEmpenho, results, reportRows);
    section.success(`ContratoEmpenho: ${results.ContratoEmpenho ?? 0} registros`);
    await sleep(2000);

    // 6. Receita (with lock)
    if (tryAcquireReceitaLock()) {
      try {
        await executeEntity("Receita", endpoints.receita, results, reportRows);
        section.success(`Receita: ${results.Receita ?? 0} registros`);
      } finally {
        releaseReceitaLock();
      }
    } else {
      section.warning("Receita: pulada (lock não adquirido)");
      reportRows.push({
        name: "Receita",
        records: 0,
        durationMs: 0,
        status: "skipped",
        errorMessage: "lock não adquirido",
      });
    }
    await sleep(2000);

    // 7. Pagamento
    await executeEntity("Pagamento", endpoints.pagamento, results, reportRows);
    section.success(`Pagamento: ${results.Pagamento ?? 0} registros`);
    await sleep(2000);

    // 8. Empenho
    await executeEntity("Empenho", endpoints.empenho, results, reportRows);
    section.success(`Empenho: ${results.Empenho ?? 0} registros`);
    await sleep(2000);

    // 9. Liquidacao
    await executeEntity("Liquidacao", endpoints.liquidacao, results, reportRows);
    section.success(`Liquidacao: ${results.Liquidacao ?? 0} registros`);
    await sleep(2000);

    // 10. RestosAPagar
    await executeEntity("RestosAPagar", endpoints.restosAPagar, results, reportRows);
    section.success(`RestosAPagar: ${results.RestosAPagar ?? 0} registros`);
    await sleep(2000);

    // 11. OrdemFornecimento
    await executeEntity("OrdemFornecimento", endpoints.ordemFornecimento, results, reportRows);
    section.success(`OrdemFornecimento: ${results.OrdemFornecimento ?? 0} registros`);
    await sleep(2000);

    // 12. DadosOrcamentarios
    await executeEntity("DadosOrcamentarios", endpoints.dadosOrcamentarios, results, reportRows);
    section.success(`DadosOrcamentarios: ${results.DadosOrcamentarios ?? 0} registros`);
    await sleep(2000);

    // 13. TotalizadoresExecucao
    await executeEntity("TotalizadoresExecucao", endpoints.totalizadoresExecucao, results, reportRows);
    section.success(`TotalizadoresExecucao: ${results.TotalizadoresExecucao ?? 0} registros`);
    await sleep(2000);

    // 14. BaseDespesaCredor
    await executeEntity("BaseDespesaCredor", endpoints.baseDespesaCredor, results, reportRows);
    section.success(`BaseDespesaCredor: ${results.BaseDespesaCredor ?? 0} registros`);
    await sleep(2000);

    // 15. BaseDespesaLicitacao
    await executeEntity("BaseDespesaLicitacao", endpoints.baseDespesaLicitacao, results, reportRows);
    section.success(`BaseDespesaLicitacao: ${results.BaseDespesaLicitacao ?? 0} registros`);
    await sleep(2000);

    // 16. DespesaConvenio
    await executeEntity("DespesaConvenio", endpoints.despesaConvenio, results, reportRows);
    section.success(`DespesaConvenio: ${results.DespesaConvenio ?? 0} registros`);
    await sleep(2000);

    // 17. Termo
    await executeEntity("Termo", endpoints.termo, results, reportRows);
    section.success(`Termo: ${results.Termo ?? 0} registros`);
    await sleep(2000);

    // 18. PrevisaoRealizacaoReceita
    await executeEntity("PrevisaoRealizacaoReceita", endpoints.previsaoRealizacaoReceita, results, reportRows);
    section.success(`PrevisaoRealizacaoReceita: ${results.PrevisaoRealizacaoReceita ?? 0} registros`);
    await sleep(2000);

    // 19. DespesaDetalhada (multi-mes)
    for (let mes = 1; mes <= 12; mes++) {
      const t0Mes = Date.now();
      try {
        const processed = await consumirPersistir(endpoints.despesaDetalhada, {
          ...SCHEDULER_CONSUME_CONSOLE,
          filters: { dtAnoExercicioCTBFiltro: new Date().getFullYear(), nuMesFiltro: mes },
        });
        results[`DespesaDetalhada_mes${mes}`] = processed;
        reportRows.push({
          name: `DespesaDetalhada (mês ${mes})`,
          records: processed,
          durationMs: Date.now() - t0Mes,
          status: "ok",
        });
        await sleep(500);
      } catch (e) {
        logger.error("SCHEDULER", `Erro DespesaDetalhada mês ${mes}`, e);
        results[`DespesaDetalhada_mes${mes}`] = 0;
        reportRows.push({
          name: `DespesaDetalhada (mês ${mes})`,
          records: 0,
          durationMs: Date.now() - t0Mes,
          status: "error",
          errorMessage: e instanceof Error ? e.message : String(e),
        });
      }
    }
    section.success(`DespesaDetalhada: ${Object.entries(results).filter(([k]) => k.startsWith("DespesaDetalhada")).reduce((s, [, v]) => s + v, 0)} registros`);

    const totalRecords = Object.values(results).reduce((s, v) => s + v, 0);
    const duration = Date.now() - startTime;
    logger.logScheduledExecutionReport(reportRows, totalRecords, duration, correlationId, startedAtIso);
    section.logWithSummary(totalRecords);

    lastExecutionTime = new Date();
    return results;
  } catch (e) {
    logger.error("SCHEDULER", "Erro grave na execução agendada", e);
    section.error(`Erro grave: ${e instanceof Error ? e.message : String(e)}`);
    section.log();
    throw e;
  } finally {
    isRunning = false;
    isFirstExecution = false;
    logger.clearContext();
  }
}

export async function executeManually(): Promise<Record<string, any>> {
  const correlationId = logger.generateCorrelationId();
  try {
    const results = await executeAllEntities();
    return { status: "SUCCESS", correlationId, results, timestamp: new Date().toISOString() };
  } catch (e) {
    return {
      status: "ERROR", correlationId,
      message: e instanceof Error ? e.message : String(e),
      timestamp: new Date().toISOString(),
    };
  }
}

export async function executeEntityManually(endpoint: endpoints.EndpointDefinition): Promise<Record<string, any>> {
  const correlationId = logger.generateCorrelationId();
  try {
    const processed = await consumirPersistir(endpoint);
    return {
      status: "SUCCESS", correlationId,
      recordsProcessed: processed,
      entity: endpoint.name,
      timestamp: new Date().toISOString(),
    };
  } catch (e) {
    return {
      status: "ERROR", correlationId,
      message: e instanceof Error ? e.message : String(e),
      entity: endpoint.name,
      timestamp: new Date().toISOString(),
    };
  }
}

export function getSchedulerStatus(): Record<string, any> {
  return {
    enabled: env.SCHEDULER_ENABLED,
    productionEnabled: env.SCHEDULER_PRODUCTION_ENABLED,
    isRunning,
    isFirstExecution,
    lastExecutionTime: lastExecutionTime?.toISOString() ?? null,
    cron: "0 45 2 * * *",
    timestamp: new Date().toISOString(),
  };
}

export function startScheduler() {
  if (!env.SCHEDULER_ENABLED) {
    logger.info("SCHEDULER", "Scheduler desabilitado por configuração");
    return;
  }

  if (env.SCHEDULER_PRODUCTION_ENABLED) {
    cron.schedule("45 2 * * *", async () => {
      logger.info("SCHEDULER", "Executando agendamento diário às 02:45");
      try {
        await executeAllEntities();
      } catch (e) {
        logger.error("SCHEDULER", "Erro na execução agendada", e);
      }
    });
    logger.info("SCHEDULER", "Agendamento diário configurado: 02:45 AM");
  }

  if (env.SCHEDULER_STARTUP_DELAY_SECONDS > 0 && env.SCHEDULER_TEST_ON_STARTUP) {
    setTimeout(async () => {
      logger.info("SCHEDULER", "Executando na inicialização após delay");
      try {
        await executeAllEntities();
      } catch (e) {
        logger.error("SCHEDULER", "Erro na execução de startup", e);
      }
    }, env.SCHEDULER_STARTUP_DELAY_SECONDS * 1000);
  }

  // Log cleanup daily at 2:00 AM
  cron.schedule("0 2 * * *", async () => {
    try {
      const { performCleanup } = await import("../logging/log-cleanup.js");
      performCleanup();
      logger.info("SCHEDULER", "Limpeza de logs executada");
    } catch (e) {
      logger.error("SCHEDULER", "Erro na limpeza de logs", e);
    }
  });
}
