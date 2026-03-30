import { query } from "../db/client.js";
import { logger } from "../logging/logger.js";
import { contratosFiscais } from "./endpoints/index.js";
import type { EndpointDefinition } from "./endpoints/types.js";

const BATCH_SIZE = 1000;
const BATCH_PAUSE_MS = 100;

function resolveValue(mapping: string | ((item: any) => any), item: any): any {
  if (typeof mapping === "function") return mapping(item);
  return item[mapping] ?? null;
}

export async function insertBatch(
  endpoint: EndpointDefinition,
  items: Record<string, any>[]
): Promise<number> {
  if (!items.length) return 0;

  const startTime = Date.now();
  const tableName = endpoint.tableName;
  logger.logOperationStart("DATABASE", "INSERT_BATCH", "TABLE", tableName, "COUNT", items.length);

  try {
    let processedTotal = 0;
    for (let i = 0; i < items.length; i += BATCH_SIZE) {
      const batch = items.slice(i, i + BATCH_SIZE);
      const batchStart = Date.now();
      await processBatch(endpoint, batch);
      processedTotal += batch.length;
      logger.logDatabaseOperation("INSERT_BATCH_PROGRESS", tableName, batch.length, Date.now() - batchStart);

      if (i + BATCH_SIZE < items.length) {
        await new Promise((r) => setTimeout(r, BATCH_PAUSE_MS));
      }
    }

    const executionTime = Date.now() - startTime;
    logger.logOperationSuccess("DATABASE", "INSERT_BATCH", executionTime, processedTotal, "TABLE", tableName);
    logger.logDatabaseOperation("INSERT", tableName, processedTotal, executionTime);
    return processedTotal;
  } catch (e) {
    const executionTime = Date.now() - startTime;
    logger.logOperationError("DATABASE", "INSERT_BATCH", executionTime, e, "TABLE", tableName);
    throw e;
  }
}

async function processBatch(endpoint: EndpointDefinition, batch: Record<string, any>[]) {
  if (!batch.length) return;

  const columns = Object.keys(endpoint.responseMapping);

  const rows: any[][] = [];
  for (const item of batch) {
    const row: any[] = [];
    for (const col of columns) {
      const mapping = endpoint.responseMapping[col];
      row.push(resolveValue(mapping, item));
    }
    rows.push(row);
  }

  const sql = buildSql(endpoint.tableName, columns, endpoint.uniqueConstraintColumns);

  const flatValues: any[] = [];
  const valueSets: string[] = [];
  let paramIdx = 1;

  for (const row of rows) {
    const placeholders: string[] = [];
    for (const val of row) {
      placeholders.push(`$${paramIdx++}`);
      flatValues.push(val);
    }
    valueSets.push(`(${placeholders.join(", ")})`);
  }

  const fullSql = sql.replace("__VALUES__", valueSets.join(", "));
  await query(fullSql, flatValues);
}

function buildSql(tableName: string, columns: string[], uniqueCols: string[] | null): string {
  const colList = columns.join(", ");

  if (uniqueCols && uniqueCols.length > 0) {
    const conflictCols = uniqueCols.join(", ");
    const updateSet = columns
      .filter((c) => !uniqueCols.includes(c))
      .map((c) => `${c} = EXCLUDED.${c}`)
      .join(", ");
    return `INSERT INTO ${tableName} (${colList}) VALUES __VALUES__ ON CONFLICT (${conflictCols}) DO UPDATE SET ${updateSet}`;
  }

  return `INSERT INTO ${tableName} (${colList}) VALUES __VALUES__`;
}

export async function deleteByMesVigente(endpoint: EndpointDefinition, filters?: Record<string, any>): Promise<number> {
  const tableName = endpoint.tableName;
  const startTime = Date.now();

  try {
    if (tableName === "consumer_sefaz.despesa_detalhada" || tableName === "consumer_sefaz.previsao_realizacao_receita") {
      const nuMes = filters?.nuMes ?? filters?.nuMesFiltro;
      const dtAno = filters?.dtAno ?? filters?.dtAnoExercicioCTBFiltro ?? new Date().getFullYear();

      if (nuMes) {
        const result = await query(`DELETE FROM ${tableName} WHERE dt_ano_exercicio_ctb = $1 AND nu_mes = $2`, [dtAno, nuMes]);
        const count = result.rowCount ?? 0;
        logger.logDatabaseOperation("DELETE", tableName, count, Date.now() - startTime);
        return count;
      }
      const result = await query(`DELETE FROM ${tableName} WHERE dt_ano_exercicio_ctb = $1`, [dtAno]);
      const count = result.rowCount ?? 0;
      logger.logDatabaseOperation("DELETE", tableName, count, Date.now() - startTime);
      return count;
    }

    const skipDeleteTables = [
      "consumer_sefaz.empenho", "consumer_sefaz.pagamento", "consumer_sefaz.liquidacao",
      "consumer_sefaz.restos_a_pagar", "consumer_sefaz.contrato_empenho",
      "consumer_sefaz.ordem_fornecimento", "consumer_sefaz.receita",
    ];
    if (skipDeleteTables.includes(tableName)) return 0;

    if (!endpoint.nomeDataInicialPadraoFiltro || !endpoint.nomeDataFinalPadraoFiltro) return 0;

    const sql = `DELETE FROM ${tableName} WHERE EXTRACT(YEAR FROM ${endpoint.nomeDataInicialPadraoFiltro}) = EXTRACT(YEAR FROM CURRENT_DATE) AND EXTRACT(MONTH FROM ${endpoint.nomeDataFinalPadraoFiltro}) = EXTRACT(MONTH FROM CURRENT_DATE)`;
    const result = await query(sql);
    const count = result.rowCount ?? 0;
    logger.logDatabaseOperation("DELETE", tableName, count, Date.now() - startTime);
    return count;
  } catch (e) {
    logger.logOperationError("DATABASE", "DELETE_BY_MONTH", Date.now() - startTime, e, "TABLE", tableName);
    throw e;
  }
}

export async function persist(
  endpoint: EndpointDefinition,
  items: Record<string, any>[],
  filters?: Record<string, any>
): Promise<number> {
  if (!items.length) return 0;
  await deleteByMesVigente(endpoint, filters);
  return insertBatch(endpoint, items);
}

export async function persistContratosFiscais(
  items: Record<string, any>[],
  opts?: { deleteExisting?: boolean }
): Promise<number> {
  if (!items.length) return 0;

  if (opts?.deleteExisting !== false) {
    await query(
      `DELETE FROM consumer_sefaz.contratos_fiscais WHERE EXTRACT(YEAR FROM dt_inicio_vigencia_contrato) = EXTRACT(YEAR FROM CURRENT_DATE) AND EXTRACT(MONTH FROM dt_inicio_vigencia_contrato) = EXTRACT(MONTH FROM CURRENT_DATE)`
    );
  }

  return insertBatch(contratosFiscais, items);
}
