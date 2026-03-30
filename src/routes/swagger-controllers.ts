import { Hono } from "hono";
import { query } from "../db/client.js";

export const swaggerRoutes = new Hono();

export interface QueryConfig {
  path: string;
  table: string;
  defaultOrderBy?: string;
  searchColumns?: string[];
}

export const ENTITY_CONFIGS: QueryConfig[] = [
  { path: "/contrato", table: "consumer_sefaz.contrato", defaultOrderBy: "dt_ano_exercicio DESC", searchColumns: ["nm_fornecedor", "cd_contrato", "ds_objeto_contrato"] },
  { path: "/empenho", table: "consumer_sefaz.empenho", defaultOrderBy: "dt_ano_exercicio_ctb DESC", searchColumns: ["nm_razao_social_pessoa", "nu_documento"] },
  { path: "/pagamento", table: "consumer_sefaz.pagamento", defaultOrderBy: "dt_ano_exercicio_ctb DESC", searchColumns: ["nm_razao_social_pessoa"] },
  { path: "/liquidacao", table: "consumer_sefaz.liquidacao", defaultOrderBy: "dt_ano_exercicio_ctb DESC", searchColumns: ["nm_razao_social_pessoa"] },
  { path: "/receita", table: "consumer_sefaz.receita", searchColumns: ["nm_convenio", "ds_objeto_convenio"] },
  { path: "/restos-a-pagar", table: "consumer_sefaz.restos_a_pagar", defaultOrderBy: "dt_ano_exercicio_ctb DESC" },
  { path: "/contrato-empenho", table: "consumer_sefaz.contrato_empenho", defaultOrderBy: "dt_ano_exercicio DESC" },
  { path: "/ordem-fornecimento", table: "consumer_sefaz.ordem_fornecimento", defaultOrderBy: "dt_ano_exercicio_emp DESC" },
  { path: "/despesa-detalhada", table: "consumer_sefaz.despesa_detalhada", defaultOrderBy: "dt_ano_exercicio_ctb DESC, nu_mes DESC" },
  { path: "/previsao-realizacao-receita", table: "consumer_sefaz.previsao_realizacao_receita", defaultOrderBy: "dt_ano_exercicio_ctb DESC, nu_mes DESC" },
  { path: "/consulta-gerencial", table: "consumer_sefaz.consulta_gerencial", defaultOrderBy: "dt_ano_exercicio_ctb DESC", searchColumns: ["nm_razao_social_pessoa", "ds_motivo_viagem"] },
  { path: "/contratos-fiscais", table: "consumer_sefaz.contratos_fiscais", defaultOrderBy: "dt_ano_exercicio DESC", searchColumns: ["nm_contratado", "nm_fiscal"] },
  { path: "/base-despesa-credor", table: "consumer_sefaz.base_despesa_credor", defaultOrderBy: "dt_ano_exercicio DESC", searchColumns: ["nm_razao_social_pessoa"] },
  { path: "/base-despesa-licitacao", table: "consumer_sefaz.base_despesa_licitacao", searchColumns: ["nm_razao_social_fornecedor", "ds_objeto"] },
  { path: "/totalizadores-execucao", table: "consumer_sefaz.totalizadores_execucao", defaultOrderBy: "dt_ano_exercicio_ctb DESC" },
  { path: "/dados-orcamentarios", table: "consumer_sefaz.dados_orcamentarios" },
  { path: "/unidade-gestora", table: "consumer_sefaz.unidade_gestora", defaultOrderBy: "cd_unidade_gestora" },
  { path: "/convenio/despesa", table: "consumer_sefaz.convenio_despesa", searchColumns: ["nm_concedente", "ds_objeto_convenio"] },
  { path: "/termo", table: "consumer_sefaz.termo", searchColumns: ["nm_convenio", "ds_objeto_convenio"] },
];

for (const config of ENTITY_CONFIGS) {
  swaggerRoutes.get(config.path, async (c) => {
    try {
      const page = Math.max(0, parseInt(c.req.query("page") || "0"));
      const size = Math.min(1000, Math.max(1, parseInt(c.req.query("size") || "20")));
      const offset = page * size;
      const search = c.req.query("search");
      const ano = c.req.query("ano");
      const ug = c.req.query("cdUnidadeGestora");

      const whereClauses: string[] = [];
      const params: any[] = [];
      let paramIdx = 1;

      if (ano) {
        const anoCol = config.table.includes("empenho") || config.table.includes("pagamento") || config.table.includes("liquidacao") || config.table.includes("restos_a_pagar") || config.table.includes("despesa_detalhada") || config.table.includes("previsao") || config.table.includes("consulta_gerencial") || config.table.includes("totalizadores")
          ? "dt_ano_exercicio_ctb" : "dt_ano_exercicio";
        whereClauses.push(`${anoCol} = $${paramIdx++}`);
        params.push(parseInt(ano));
      }

      if (ug) {
        whereClauses.push(`cd_unidade_gestora = $${paramIdx++}`);
        params.push(ug);
      }

      if (search && config.searchColumns?.length) {
        const searchClauses = config.searchColumns.map((col) => `${col} ILIKE $${paramIdx}`);
        whereClauses.push(`(${searchClauses.join(" OR ")})`);
        params.push(`%${search}%`);
        paramIdx++;
      }

      const whereStr = whereClauses.length > 0 ? `WHERE ${whereClauses.join(" AND ")}` : "";
      const orderBy = config.defaultOrderBy ? `ORDER BY ${config.defaultOrderBy}` : "";

      const countResult = await query(`SELECT COUNT(*) as total FROM ${config.table} ${whereStr}`, params);
      const total = parseInt(countResult.rows[0].total);

      const dataResult = await query(
        `SELECT * FROM ${config.table} ${whereStr} ${orderBy} LIMIT $${paramIdx++} OFFSET $${paramIdx++}`,
        [...params, size, offset]
      );

      return c.json({
        content: dataResult.rows,
        totalElements: total,
        totalPages: Math.ceil(total / size),
        number: page,
        size,
        first: page === 0,
        last: (page + 1) * size >= total,
      });
    } catch (e) {
      return c.json({ error: e instanceof Error ? e.message : String(e) }, 500);
    }
  });

  swaggerRoutes.get(`${config.path}/count`, async (c) => {
    try {
      const result = await query(`SELECT COUNT(*) as total FROM ${config.table}`);
      return c.json({ count: parseInt(result.rows[0].total), table: config.table });
    } catch (e) {
      return c.json({ error: e instanceof Error ? e.message : String(e) }, 500);
    }
  });
}
