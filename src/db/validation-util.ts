import { query } from "./client.js";

export async function getAnoAtual(): Promise<number> {
  const result = await query("SELECT EXTRACT(YEAR FROM CURRENT_DATE)::int AS ano_atual");
  return result.rows[0].ano_atual;
}

export async function getMesAtual(): Promise<number> {
  const result = await query("SELECT EXTRACT(MONTH FROM CURRENT_DATE)::int AS mes_atual");
  return result.rows[0].mes_atual;
}

export async function getUltimos2Meses(): Promise<Array<[number, number]>> {
  const ano = await getAnoAtual();
  const mes = await getMesAtual();
  if (mes >= 2) {
    return [[ano, mes - 1], [ano, mes]];
  }
  return [[ano - 1, 12], [ano, 1]];
}

export async function getUgs(): Promise<string[]> {
  try {
    const result = await query(
      "SELECT DISTINCT cd_unidade_gestora FROM consumer_sefaz.unidade_gestora WHERE sg_tipo_unidade_gestora = 'E'"
    );
    if (result.rows.length > 0) {
      return result.rows.map((r: any) => r.cd_unidade_gestora);
    }
    return ["001", "002"];
  } catch {
    return ["001", "002"];
  }
}

export async function cdGestao(): Promise<string[]> {
  const result = await query(
    "SELECT DISTINCT cd_gestao FROM consumer_sefaz.empenho WHERE cd_gestao IS NOT NULL AND TRIM(cd_gestao) != ''"
  );
  return result.rows
    .map((r: any) => r.cd_gestao?.trim())
    .filter((s: string) => s && s.length > 0);
}

export async function cdGestaoSeed(): Promise<string[]> {
  try {
    const result = await query(
      `SELECT DISTINCT vugl.cd_gestao
       FROM spt.vw_unidades_gestora_liq vugl
       WHERE vugl.cd_gestao IS NOT NULL AND vugl.cd_gestao != ''
       ORDER BY vugl.cd_gestao`
    );
    if (result.rows.length > 0) {
      return result.rows
        .map((r: any) => r.cd_gestao?.trim())
        .filter((s: string) => s && s.length > 0);
    }
  } catch {
    // fallback
  }
  return cdGestao();
}

export async function cdGestaoPorUgAno(cdUnidadeGestora: string, ano: number): Promise<string[]> {
  if (!cdUnidadeGestora || !ano) return [];
  try {
    const result = await query(
      `SELECT DISTINCT vugl.cd_gestao
       FROM spt.vw_unidades_gestora_liq vugl
       WHERE vugl.cd_unidade_gestora = $1
         AND vugl.cd_gestao IS NOT NULL AND vugl.cd_gestao != ''
       ORDER BY vugl.cd_gestao`,
      [cdUnidadeGestora]
    );
    if (result.rows.length > 0) {
      return result.rows
        .map((r: any) => r.cd_gestao?.trim())
        .filter((s: string) => s && s.length > 0);
    }
  } catch {
    // fallback
  }
  const result = await query(
    `SELECT DISTINCT cd_gestao FROM consumer_sefaz.empenho
     WHERE cd_unidade_gestora = $1 AND dt_ano_exercicio_ctb = $2
       AND cd_gestao IS NOT NULL AND TRIM(cd_gestao) != ''
     ORDER BY cd_gestao`,
    [cdUnidadeGestora, ano]
  );
  return result.rows
    .map((r: any) => r.cd_gestao?.trim())
    .filter((s: string) => s && s.length > 0);
}

export async function isPresenteBanco(tableName: string, dtAnoPadrao: string | null): Promise<boolean> {
  try {
    const col = dtAnoPadrao || "*";
    const sql = `SELECT (COALESCE(COUNT(${col}), 0) > 0) AS valor FROM ${tableName} LIMIT 1`;
    const result = await query(sql);
    return result.rows[0]?.valor === true;
  } catch {
    return false;
  }
}

export async function isEndpointIndependenteUGData(hasParams: boolean): Promise<boolean> {
  return !hasParams;
}
