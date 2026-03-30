import { Hono } from "hono";
import { logger } from "../logging/logger.js";
import { executeManually, executeEntityManually, getSchedulerStatus } from "../scheduler/cron.js";
import { consumirPersistir } from "../consumer/api-service.js";
import { consumirAnoInteiro, consumirAnoEMes, needsMonthlyIteration } from "../consumer/por-periodo-service.js";
import { tryAcquireReceitaLock, releaseReceitaLock, isReceitaRunning } from "../scheduler/lock.js";
import { SCHEDULER_CONSUME_CONSOLE } from "../scheduler/consume-console-options.js";
import * as ep from "../consumer/endpoints/index.js";

export const schedulerRoutes = new Hono();

const ENTITY_MAP: Record<string, ep.EndpointDefinition> = {
  pagamento: ep.pagamento,
  liquidacao: ep.liquidacao,
  empenho: ep.empenho,
  "restos-a-pagar": ep.restosAPagar,
  "ordem-fornecimento": ep.ordemFornecimento,
  "dados-orcamentarios": ep.dadosOrcamentarios,
  "totalizadores-execucao": ep.totalizadoresExecucao,
  "consulta-gerencial": ep.consultaGerencial,
  contrato: ep.contrato,
  "contratos-fiscais": ep.contratosFiscais,
  "contrato-empenho": ep.contratoEmpenho,
  "base-despesa-credor": ep.baseDespesaCredor,
  "base-despesa-licitacao": ep.baseDespesaLicitacao,
  "despesa-detalhada": ep.despesaDetalhada,
  "previsao-realizacao-receita": ep.previsaoRealizacaoReceita,
  termo: ep.termo,
};

function validateAno(ano: number): string | null {
  if (isNaN(ano) || ano < 2000 || ano > 2030) return "Ano deve estar entre 2000 e 2030";
  return null;
}

function validateMes(mes: number): string | null {
  if (isNaN(mes) || mes < 1 || mes > 12) return "Mês deve estar entre 1 e 12";
  return null;
}

// POST /scheduler/execute
schedulerRoutes.post("/execute", async (c) => {
  const correlationId = logger.generateCorrelationId();
  try {
    const result = await executeManually();
    return c.json({ ...result, correlationId });
  } catch (e) {
    return c.json({ status: "ERROR", correlationId, message: e instanceof Error ? e.message : String(e), timestamp: new Date().toISOString() }, 500);
  }
});

// GET /scheduler/status
schedulerRoutes.get("/status", (c) => {
  return c.json({ ...getSchedulerStatus(), timestamp: new Date().toISOString() });
});

// GET /scheduler/info
schedulerRoutes.get("/info", (c) => {
  return c.json({
    description: "SEFAZ Transparency Consumer - Scheduler",
    cron: "45 2 * * * (diariamente às 02:45)",
    entities: Object.keys(ENTITY_MAP),
    endpoints: {
      executeAll: "POST /scheduler/execute",
      executeEntity: "POST /scheduler/execute/:entity",
      executeByYear: "POST /scheduler/execute/:entity/por-ano/:ano",
      executeByPeriod: "POST /scheduler/execute/:entity/por-periodo?ano=&mes=",
      executeByYearUg: "POST /scheduler/execute/:entity/por-ano/:ano/ug/:cdUnidadeGestora",
      executeByPeriodUg: "POST /scheduler/execute/:entity/por-periodo/ug/:cdUnidadeGestora?ano=&mes=",
      convenioDespesaPorPeriodo: "POST /scheduler/execute/convenio/despesa/por-periodo?ano=&mes=",
      convenioDespesaPorAnoUg: "POST /scheduler/execute/convenio/despesa/por-ano/:ano/ug/:cdUnidadeGestora",
      convenioDespesaPorPeriodoUg: "POST /scheduler/execute/convenio/despesa/por-periodo/ug/:cdUnidadeGestora?ano=&mes=",
      convenioReceitaPorPeriodo: "POST /scheduler/execute/convenio/receita/por-periodo?ano=&mes=",
      convenioReceitaPorAnoUg: "POST /scheduler/execute/convenio/receita/por-ano/:ano/ug/:cdUnidadeGestora",
      convenioReceitaPorPeriodoUg: "POST /scheduler/execute/convenio/receita/por-periodo/ug/:cdUnidadeGestora?ano=&mes=",
      empenhoMensalPorPeriodo: "POST /empenho/mensal/execute-por-periodo?ano=&mes=",
    },
  });
});

// GET /scheduler/ping
schedulerRoutes.get("/ping", (c) => {
  return c.json({ status: "OK", message: "Scheduler respondendo", timestamp: new Date().toISOString() });
});

// POST /scheduler/execute/:entity
for (const [entityKey, endpoint] of Object.entries(ENTITY_MAP)) {
  schedulerRoutes.post(`/execute/${entityKey}`, async (c) => {
    const correlationId = logger.generateCorrelationId();
    try {
      const result = await executeEntityManually(endpoint);
      return c.json({ ...result, correlationId });
    } catch (e) {
      return c.json({ status: "ERROR", correlationId, message: e instanceof Error ? e.message : String(e), timestamp: new Date().toISOString() }, 500);
    }
  });

  // POST /scheduler/execute/:entity/por-ano/:ano
  schedulerRoutes.post(`/execute/${entityKey}/por-ano/:ano`, async (c) => {
    const correlationId = logger.generateCorrelationId();
    const ano = parseInt(c.req.param("ano"));
    const err = validateAno(ano);
    if (err) return c.json({ status: "ERROR", message: err, correlationId }, 400);
    try {
      const hasMonthly = needsMonthlyIteration(endpoint.name);
      const result = await consumirAnoInteiro(endpoint, ano, undefined, hasMonthly);
      return c.json({ ...result, correlationId });
    } catch (e) {
      return c.json({ status: "ERROR", correlationId, message: e instanceof Error ? e.message : String(e) }, 500);
    }
  });

  // POST /scheduler/execute/:entity/por-periodo
  schedulerRoutes.post(`/execute/${entityKey}/por-periodo`, async (c) => {
    const correlationId = logger.generateCorrelationId();
    const ano = parseInt(c.req.query("ano") || "0");
    const mes = parseInt(c.req.query("mes") || "0");
    let err = validateAno(ano);
    if (err) return c.json({ status: "ERROR", message: err, correlationId }, 400);
    err = validateMes(mes);
    if (err) return c.json({ status: "ERROR", message: err, correlationId }, 400);
    try {
      const processed = await consumirAnoEMes(endpoint, ano, mes);
      return c.json({
        status: "SUCCESS", correlationId,
        recordsProcessed: processed, ano, mes,
        message: `${processed} registros processados`,
        timestamp: new Date().toISOString(),
      });
    } catch (e) {
      return c.json({ status: "ERROR", correlationId, message: e instanceof Error ? e.message : String(e) }, 500);
    }
  });

  // POST /scheduler/execute/:entity/por-ano/:ano/ug/:cdUnidadeGestora
  schedulerRoutes.post(`/execute/${entityKey}/por-ano/:ano/ug/:cdUnidadeGestora`, async (c) => {
    const correlationId = logger.generateCorrelationId();
    const ano = parseInt(c.req.param("ano"));
    const cdUnidadeGestora = c.req.param("cdUnidadeGestora");
    const err = validateAno(ano);
    if (err) return c.json({ status: "ERROR", message: err, correlationId }, 400);
    if (!cdUnidadeGestora?.trim()) return c.json({ status: "ERROR", message: "cdUnidadeGestora é obrigatório", correlationId }, 400);
    try {
      const hasMonthly = needsMonthlyIteration(endpoint.name);
      const result = await consumirAnoInteiro(endpoint, ano, cdUnidadeGestora, hasMonthly);
      return c.json({ ...result, correlationId });
    } catch (e) {
      return c.json({ status: "ERROR", correlationId, message: e instanceof Error ? e.message : String(e) }, 500);
    }
  });

  // POST /scheduler/execute/:entity/por-periodo/ug/:cdUnidadeGestora
  schedulerRoutes.post(`/execute/${entityKey}/por-periodo/ug/:cdUnidadeGestora`, async (c) => {
    const correlationId = logger.generateCorrelationId();
    const ano = parseInt(c.req.query("ano") || "0");
    const mes = parseInt(c.req.query("mes") || "0");
    const cdUnidadeGestora = c.req.param("cdUnidadeGestora");
    let err = validateAno(ano);
    if (err) return c.json({ status: "ERROR", message: err, correlationId }, 400);
    err = validateMes(mes);
    if (err) return c.json({ status: "ERROR", message: err, correlationId }, 400);
    if (!cdUnidadeGestora?.trim()) return c.json({ status: "ERROR", message: "cdUnidadeGestora é obrigatório", correlationId }, 400);
    try {
      const processed = await consumirAnoEMes(endpoint, ano, mes, cdUnidadeGestora);
      return c.json({
        status: "SUCCESS", correlationId,
        recordsProcessed: processed, ano, mes, cdUnidadeGestora,
        message: `${processed} registros processados`,
        timestamp: new Date().toISOString(),
      });
    } catch (e) {
      return c.json({ status: "ERROR", correlationId, message: e instanceof Error ? e.message : String(e) }, 500);
    }
  });
}

// Receita (Convenio) - with lock support
schedulerRoutes.post("/execute/convenio/receita", async (c) => {
  const correlationId = logger.generateCorrelationId();
  if (isReceitaRunning()) {
    return c.json({ status: "ALREADY_RUNNING", message: "Receita já em execução", correlationId }, 409);
  }
  if (!tryAcquireReceitaLock()) {
    return c.json({ status: "ALREADY_RUNNING", correlationId }, 409);
  }
  try {
    const result = await executeEntityManually(ep.receita);
    return c.json({ ...result, correlationId });
  } finally {
    releaseReceitaLock();
  }
});

schedulerRoutes.post("/execute/convenio/receita/por-ano/:ano", async (c) => {
  const correlationId = logger.generateCorrelationId();
  if (isReceitaRunning()) return c.json({ status: "ALREADY_RUNNING", correlationId }, 409);
  const ano = parseInt(c.req.param("ano"));
  const err = validateAno(ano);
  if (err) return c.json({ status: "ERROR", message: err, correlationId }, 400);
  if (!tryAcquireReceitaLock()) return c.json({ status: "ALREADY_RUNNING", correlationId }, 409);
  try {
    const result = await consumirAnoInteiro(ep.receita, ano, undefined, true);
    return c.json({ ...result, correlationId });
  } catch (e) {
    if (e instanceof Error && e.message.includes("já em execução")) {
      return c.json({ status: "ALREADY_RUNNING", correlationId }, 409);
    }
    return c.json({ status: "ERROR", correlationId, message: e instanceof Error ? e.message : String(e) }, 500);
  } finally {
    releaseReceitaLock();
  }
});

schedulerRoutes.post("/execute/convenio/receita/por-periodo", async (c) => {
  const correlationId = logger.generateCorrelationId();
  if (isReceitaRunning()) return c.json({ status: "ALREADY_RUNNING", correlationId }, 409);
  const ano = parseInt(c.req.query("ano") || "0");
  const mes = parseInt(c.req.query("mes") || "0");
  let err = validateAno(ano);
  if (err) return c.json({ status: "ERROR", message: err, correlationId }, 400);
  err = validateMes(mes);
  if (err) return c.json({ status: "ERROR", message: err, correlationId }, 400);
  if (!tryAcquireReceitaLock()) return c.json({ status: "ALREADY_RUNNING", correlationId }, 409);
  try {
    const processed = await consumirAnoEMes(ep.receita, ano, mes);
    return c.json({
      status: "SUCCESS",
      correlationId,
      recordsProcessed: processed,
      ano,
      mes,
      message: `${processed} registros processados`,
      timestamp: new Date().toISOString(),
    });
  } catch (e) {
    return c.json({ status: "ERROR", correlationId, message: e instanceof Error ? e.message : String(e) }, 500);
  } finally {
    releaseReceitaLock();
  }
});

schedulerRoutes.post("/execute/convenio/receita/por-ano/:ano/ug/:cdUnidadeGestora", async (c) => {
  const correlationId = logger.generateCorrelationId();
  if (isReceitaRunning()) return c.json({ status: "ALREADY_RUNNING", correlationId }, 409);
  const ano = parseInt(c.req.param("ano"));
  const cdUnidadeGestora = c.req.param("cdUnidadeGestora");
  const err = validateAno(ano);
  if (err) return c.json({ status: "ERROR", message: err, correlationId }, 400);
  if (!cdUnidadeGestora?.trim()) return c.json({ status: "ERROR", message: "cdUnidadeGestora é obrigatório", correlationId }, 400);
  if (!tryAcquireReceitaLock()) return c.json({ status: "ALREADY_RUNNING", correlationId }, 409);
  try {
    const result = await consumirAnoInteiro(ep.receita, ano, cdUnidadeGestora, true);
    return c.json({ ...result, correlationId });
  } catch (e) {
    return c.json({ status: "ERROR", correlationId, message: e instanceof Error ? e.message : String(e) }, 500);
  } finally {
    releaseReceitaLock();
  }
});

schedulerRoutes.post("/execute/convenio/receita/por-periodo/ug/:cdUnidadeGestora", async (c) => {
  const correlationId = logger.generateCorrelationId();
  if (isReceitaRunning()) return c.json({ status: "ALREADY_RUNNING", correlationId }, 409);
  const ano = parseInt(c.req.query("ano") || "0");
  const mes = parseInt(c.req.query("mes") || "0");
  const cdUnidadeGestora = c.req.param("cdUnidadeGestora");
  let err = validateAno(ano);
  if (err) return c.json({ status: "ERROR", message: err, correlationId }, 400);
  err = validateMes(mes);
  if (err) return c.json({ status: "ERROR", message: err, correlationId }, 400);
  if (!cdUnidadeGestora?.trim()) return c.json({ status: "ERROR", message: "cdUnidadeGestora é obrigatório", correlationId }, 400);
  if (!tryAcquireReceitaLock()) return c.json({ status: "ALREADY_RUNNING", correlationId }, 409);
  try {
    const processed = await consumirAnoEMes(ep.receita, ano, mes, cdUnidadeGestora);
    return c.json({
      status: "SUCCESS",
      correlationId,
      recordsProcessed: processed,
      ano,
      mes,
      cdUnidadeGestora,
      message: `${processed} registros processados`,
      timestamp: new Date().toISOString(),
    });
  } catch (e) {
    return c.json({ status: "ERROR", correlationId, message: e instanceof Error ? e.message : String(e) }, 500);
  } finally {
    releaseReceitaLock();
  }
});

// Convenio despesa routes
schedulerRoutes.post("/execute/convenio/despesa", async (c) => {
  const correlationId = logger.generateCorrelationId();
  try {
    const result = await executeEntityManually(ep.despesaConvenio);
    return c.json({ ...result, correlationId });
  } catch (e) {
    return c.json({ status: "ERROR", correlationId, message: e instanceof Error ? e.message : String(e) }, 500);
  }
});

schedulerRoutes.post("/execute/convenio/despesa/por-ano/:ano", async (c) => {
  const correlationId = logger.generateCorrelationId();
  const ano = parseInt(c.req.param("ano"));
  const err = validateAno(ano);
  if (err) return c.json({ status: "ERROR", message: err, correlationId }, 400);
  try {
    const result = await consumirAnoInteiro(ep.despesaConvenio, ano, undefined, true);
    return c.json({ ...result, correlationId });
  } catch (e) {
    return c.json({ status: "ERROR", correlationId, message: e instanceof Error ? e.message : String(e) }, 500);
  }
});

schedulerRoutes.post("/execute/convenio/despesa/por-periodo", async (c) => {
  const correlationId = logger.generateCorrelationId();
  const ano = parseInt(c.req.query("ano") || "0");
  const mes = parseInt(c.req.query("mes") || "0");
  let err = validateAno(ano);
  if (err) return c.json({ status: "ERROR", message: err, correlationId }, 400);
  err = validateMes(mes);
  if (err) return c.json({ status: "ERROR", message: err, correlationId }, 400);
  try {
    const processed = await consumirAnoEMes(ep.despesaConvenio, ano, mes);
    return c.json({
      status: "SUCCESS",
      correlationId,
      recordsProcessed: processed,
      ano,
      mes,
      message: `${processed} registros processados`,
      timestamp: new Date().toISOString(),
    });
  } catch (e) {
    return c.json({ status: "ERROR", correlationId, message: e instanceof Error ? e.message : String(e) }, 500);
  }
});

schedulerRoutes.post("/execute/convenio/despesa/por-ano/:ano/ug/:cdUnidadeGestora", async (c) => {
  const correlationId = logger.generateCorrelationId();
  const ano = parseInt(c.req.param("ano"));
  const cdUnidadeGestora = c.req.param("cdUnidadeGestora");
  const err = validateAno(ano);
  if (err) return c.json({ status: "ERROR", message: err, correlationId }, 400);
  if (!cdUnidadeGestora?.trim()) return c.json({ status: "ERROR", message: "cdUnidadeGestora é obrigatório", correlationId }, 400);
  try {
    const result = await consumirAnoInteiro(ep.despesaConvenio, ano, cdUnidadeGestora, true);
    return c.json({ ...result, correlationId });
  } catch (e) {
    return c.json({ status: "ERROR", correlationId, message: e instanceof Error ? e.message : String(e) }, 500);
  }
});

schedulerRoutes.post("/execute/convenio/despesa/por-periodo/ug/:cdUnidadeGestora", async (c) => {
  const correlationId = logger.generateCorrelationId();
  const ano = parseInt(c.req.query("ano") || "0");
  const mes = parseInt(c.req.query("mes") || "0");
  const cdUnidadeGestora = c.req.param("cdUnidadeGestora");
  let err = validateAno(ano);
  if (err) return c.json({ status: "ERROR", message: err, correlationId }, 400);
  err = validateMes(mes);
  if (err) return c.json({ status: "ERROR", message: err, correlationId }, 400);
  if (!cdUnidadeGestora?.trim()) return c.json({ status: "ERROR", message: "cdUnidadeGestora é obrigatório", correlationId }, 400);
  try {
    const processed = await consumirAnoEMes(ep.despesaConvenio, ano, mes, cdUnidadeGestora);
    return c.json({
      status: "SUCCESS",
      correlationId,
      recordsProcessed: processed,
      ano,
      mes,
      cdUnidadeGestora,
      message: `${processed} registros processados`,
      timestamp: new Date().toISOString(),
    });
  } catch (e) {
    return c.json({ status: "ERROR", correlationId, message: e instanceof Error ? e.message : String(e) }, 500);
  }
});

// PrevisaoRealizacaoReceita multi-mes
schedulerRoutes.post("/execute/previsao-realizacao-receita-multi-mes", async (c) => {
  const correlationId = logger.generateCorrelationId();
  try {
    let totalRecords = 0;
    for (let mes = 1; mes <= 12; mes++) {
      const processed = await consumirPersistir(ep.previsaoRealizacaoReceita, {
        ...SCHEDULER_CONSUME_CONSOLE,
        filters: { nuMesFiltro: mes },
      });
      totalRecords += processed;
      await new Promise((r) => setTimeout(r, 500));
    }
    return c.json({
      status: "SUCCESS", correlationId,
      message: `Previsão Realização Receita multi-mês concluída. ${totalRecords} registros.`,
      recordsProcessed: totalRecords,
    });
  } catch (e) {
    return c.json({ status: "ERROR", correlationId, message: e instanceof Error ? e.message : String(e) }, 500);
  }
});

schedulerRoutes.post("/execute/previsao-realizacao-receita-multi-mes/mes/:mes", async (c) => {
  const correlationId = logger.generateCorrelationId();
  const mes = parseInt(c.req.param("mes"));
  const err = validateMes(mes);
  if (err) return c.json({ status: "ERROR", message: err, correlationId }, 400);
  try {
    const processed = await consumirPersistir(ep.previsaoRealizacaoReceita, {
      ...SCHEDULER_CONSUME_CONSOLE,
      filters: { nuMesFiltro: mes },
    });
    return c.json({
      status: "SUCCESS", correlationId,
      recordsProcessed: processed, month: mes,
      message: `Mês ${mes} processado: ${processed} registros`,
    });
  } catch (e) {
    return c.json({ status: "ERROR", correlationId, message: e instanceof Error ? e.message : String(e) }, 500);
  }
});
