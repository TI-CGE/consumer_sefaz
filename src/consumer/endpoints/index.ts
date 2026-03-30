import type { EndpointDefinition } from "./types.js";

function parseLocalDate(val: any): string | null {
  if (!val) return null;
  if (typeof val === "string" && val.length >= 10) {
    try { return val.substring(0, 10); } catch { return null; }
  }
  return null;
}

function parseLocalDateTime(val: any): string | null {
  if (!val) return null;
  if (typeof val === "string") {
    try { return val.substring(0, 19).replace("T", " "); } catch { return null; }
  }
  return null;
}

function nowTimestamp() {
  return new Date().toISOString().replace("T", " ").substring(0, 23);
}

/** Valores monetários que vêm como string na API (legado Java usava BigDecimal.parse) */
function parseDecimalFlexible(val: any): number | null {
  if (val == null || val === "") return null;
  if (typeof val === "number" && !Number.isNaN(val)) return val;
  const s = String(val).trim().replace(",", ".");
  const n = parseFloat(s);
  return Number.isNaN(n) ? null : n;
}

// ================================================================
// 1. UnidadeGestora
// ================================================================
export const unidadeGestora: EndpointDefinition = {
  name: "UnidadeGestora",
  tableName: "consumer_sefaz.unidade_gestora",
  url: "https://api-transparencia.apps.sefaz.se.gov.br/gfu/v2/unidade-gestora?sgTipoUnidadeGestora=E",
  nomeDataInicialPadraoFiltro: null,
  nomeDataFinalPadraoFiltro: null,
  dtAnoPadrao: null,
  parametrosRequeridos: false,
  requerIteracaoCdGestao: false,
  requerIteracaoEmpenhos: false,
  uniqueConstraintColumns: null,
  responseMapping: {
    nm_unidade_gestora: "nmUnidadeGestora",
    sg_unidade_gestora: "sgUnidadeGestora",
    cd_unidade_gestora: "cdUnidadeGestora",
    sg_tipo_unidade_gestora: "sgTipoUnidadeGestora",
  },
  getParametrosTodosAnos: () => ({}),
  getParametrosAtual: () => ({}),
};

// ================================================================
// 2. Contrato
// ================================================================
export const contrato: EndpointDefinition = {
  name: "Contrato",
  tableName: "consumer_sefaz.contrato",
  url: "https://api-transparencia.apps.sefaz.se.gov.br/gbp/v1/contrato",
  nomeDataInicialPadraoFiltro: null,
  nomeDataFinalPadraoFiltro: null,
  dtAnoPadrao: "dt_ano_exercicio",
  parametrosRequeridos: false,
  requerIteracaoCdGestao: false,
  requerIteracaoEmpenhos: false,
  uniqueConstraintColumns: null,
  responseMapping: {
    sg_unidade_gestora: "sgUnidadeGestora",
    cd_unidade_gestora: "cdUnidadeGestora",
    dt_ano_exercicio: "dtAnoExercicio",
    cd_contrato: "cdContrato",
    cd_aditivo: "cdAditivo",
    dt_inicio_vigencia: (item: any) => parseLocalDate(item.dtInicioVigencia),
    dt_fim_vigencia: (item: any) => parseLocalDate(item.dtFimVigencia),
    nm_categoria: "nmCategoria",
    nm_fornecedor: "nmFornecedor",
    nu_documento: "nuDocumento",
    ds_objeto_contrato: "dsObjetoContrato",
    vl_contrato: "vlContrato",
    tp_contrato: "tpContrato",
  },
  getParametrosTodosAnos: (ugCd, ano, filters) => {
    const p: Record<string, any> = {};
    if (ugCd) p.cdUnidadeGestora = ugCd;
    if (ano) p.dtAnoExercicio = filters?.dtAnoExercicioFiltro ?? ano;
    return p;
  },
  getParametrosAtual: (ugCd, anoAtual, _mesAtual, filters) => {
    const p: Record<string, any> = {};
    if (ugCd) p.cdUnidadeGestora = ugCd;
    p.dtAnoExercicio = filters?.dtAnoExercicioFiltro ?? anoAtual;
    return p;
  },
};

// ================================================================
// 3. Empenho
// ================================================================
export const empenho: EndpointDefinition = {
  name: "Empenho",
  tableName: "consumer_sefaz.empenho",
  url: "https://api-transparencia.apps.sefaz.se.gov.br/gfu/v1/empenho",
  nomeDataInicialPadraoFiltro: "dt_geracao_empenho",
  nomeDataFinalPadraoFiltro: "dt_geracao_empenho",
  dtAnoPadrao: "dt_ano_exercicio_ctb",
  parametrosRequeridos: false,
  requerIteracaoCdGestao: true,
  requerIteracaoEmpenhos: false,
  uniqueConstraintColumns: ["dt_ano_exercicio_ctb", "cd_unidade_gestora", "cd_gestao", "sq_empenho"],
  responseMapping: {
    dt_ano_exercicio_ctb: "dtAnoExercicioCTB", cd_unidade_gestora: "cdUnidadeGestora",
    cd_gestao: "cdGestao", sg_unidade_gestora: "sgUnidadeGestora",
    id_orgao: "idOrgao", sg_orgao: "sgOrgao",
    id_orgao_supervisor: "idOrgaoSupervisor", sg_orgao_supervisor: "sgOrgaoSupervisor",
    sq_solic_empenho: "sqSolicEmpenho", sq_empenho: "sqEmpenho",
    cd_natureza_despesa_completa: "cdNaturezaDespesaCompleta",
    dt_geracao_empenho: "dtGeracaoEmpenho", dt_emissao_empenho: "dtEmissaoEmpenho",
    dt_lancamento_empenho: "dtLancamentoEmpenho",
    nm_razao_social_pessoa: "nmRazaoSocialPessoa", nu_documento: "nuDocumento",
    vl_solic_empenho: "vlSolicEmpenho", vl_original_empenho: "vlOriginalEmpenho",
    vl_total_reforcado_empenho: "vlTotalReforcadoEmpenho", vl_total_anulado_empenho: "vlTotalAnuladoEmpenho",
    vl_total_liquidado_empenho: "vlTotalLiquidadoEmpenho", vl_total_estorno_liqd_empenho: "vlTotalEstornoLiqdEmpenho",
    vl_total_pago_empenho: "vlTotalPagoEmpenho", vl_total_executado: "vlTotalExecutado",
    vl_total_devolvido_ob_empenho: "vlTotalDevolvidoObEmpenho", vl_total_devolvido_gr_empenho: "vlTotalDevolvidoGrEmpenho",
    cd_funcao: "cdFuncao", nm_funcao: "nmFuncao",
    cd_sub_funcao: "cdSubFuncao", nm_sub_funcao: "nmSubFuncao",
    cd_elemento_despesa: "cdElementoDespesa", cd_fonte_recurso: "cdFonteRecurso",
    cd_licitacao: "cdLicitacao", ds_objeto_licitacao: "dsObjetoLicitacao",
    nu_processo_licitacao: "nuProcessoLicitacao", nm_modalidade_licitacao: "nmModalidadeLicitacao",
    cd_funcao_plo: "cdFuncaoPLO", nm_funcao_plo: "nmFuncaoPLO",
    cd_programa_governo: "cdProgramaGoverno", nm_programa_governo: "nmProgramaGoverno",
    cd_categoria_economica: "cdCategoriaEconomica", nm_categoria_economica: "nmCategoriaEconomica",
    cd_modalidade_aplicacao: "cdModalidadeAplicacao", nm_modalidade_aplicacao: "nmModalidadeAplicacao",
    nm_fonte_recurso: "nmFonteRecurso", cd_grupo_despesa: "cdGrupoDespesa", nm_grupo_despesa: "nmGrupoDespesa",
  },
  getParametrosTodosAnos: (ugCd, ano, filters) => {
    const p: Record<string, any> = {};
    if (ugCd) p.cdUnidadeGestora = ugCd;
    p.dtAnoExercicioCTB = filters?.dtAnoExercicioCTBFiltro ?? ano;
    if (filters?.nuMesFiltro) p.nuMes = filters.nuMesFiltro;
    return p;
  },
  getParametrosAtual: (ugCd, anoAtual, mesAtual, filters) => {
    const p: Record<string, any> = {};
    if (ugCd) p.cdUnidadeGestora = ugCd;
    p.dtAnoExercicioCTB = filters?.dtAnoExercicioCTBFiltro ?? anoAtual;
    if (filters?.nuMesFiltro) p.nuMes = filters.nuMesFiltro;
    else p.nuMes = mesAtual;
    return p;
  },
};

// ================================================================
// 4. Pagamento
// ================================================================
export const pagamento: EndpointDefinition = {
  name: "Pagamento",
  tableName: "consumer_sefaz.pagamento",
  url: "https://api-transparencia.apps.sefaz.se.gov.br/gfu/v1/pagamento",
  nomeDataInicialPadraoFiltro: "dt_lancamento_ob",
  nomeDataFinalPadraoFiltro: "dt_lancamento_ob",
  dtAnoPadrao: "dt_ano_exercicio_ctb",
  parametrosRequeridos: false,
  requerIteracaoCdGestao: true,
  requerIteracaoEmpenhos: false,
  uniqueConstraintColumns: ["dt_ano_exercicio_ctb", "cd_unidade_gestora", "cd_gestao", "sq_empenho", "sq_ob"],
  responseMapping: {
    dt_ano_exercicio_ctb: "dtAnoExercicioCTB", cd_unidade_gestora: "cdUnidadeGestora",
    cd_gestao: "cdGestao", sg_unidade_gestora: "sgUnidadeGestora",
    id_orgao: "idOrgao", sg_orgao: "sgOrgao",
    id_orgao_supervisor: "idOrgaoSupervisor", sg_orgao_supervisor: "sgOrgaoSupervisor",
    sq_previsao_desembolso: "sqPrevisaoDesembolso", sq_empenho: "sqEmpenho",
    sq_ob: "sqOrdemBancaria", cd_natureza_despesa_completa: "cdNaturezaDespesa",
    nu_documento: "nuDocumento", nm_razao_social_pessoa: "nmRazaoSocialPessoa",
    vl_liquido_pd: "vlLiquidoPD", vl_anulacao_pd: "vlAnulacaoPD",
    vl_liquido_ob: "vlLiquidoOB", vl_anulacao_ob: "vlAnulacaoOB",
    dt_previsao_desembolso: "dtPrevisaoDesembolso", dt_lancamento_ob: "dtLancamentoOB",
    sq_liquidacao: "sqLiquidacao", si_ob: "siOB", si_pd: "siPD",
    cd_funcao: "cdFuncaoPLO", nm_funcao: "nmFuncaoPLO",
    cd_sub_funcao: "cdSubFuncao", nm_sub_funcao: "nmSubFuncao",
    cd_elemento_despesa: "cdElementoDespesa", cd_fonte_recurso: "cdFonteRecurso",
    cd_licitacao: "cdLicitacao", ds_objeto_licitacao: "dsObjetoLicitacao",
    nu_processo_licitacao: "nuProcessoLicitacao", nm_modalidade_licitacao: "nmModalidadeLicitacao",
    nu_conta_bancaria_recebedora: "nuContaBancariaRecebedora",
    nm_banco_recebedora: "nmBancoRecebedora",
    cd_ponto_atendimento_recebedora: "cdPontoAtendimentoRecebedora",
  },
  getParametrosTodosAnos: (ugCd, ano, filters) => {
    const p: Record<string, any> = {};
    if (ugCd) p.cdUnidadeGestora = ugCd;
    const anoUsar = filters?.dtAnoExercicioCTBFiltro ?? ano;
    p.dtAnoExercicioCTB = anoUsar;
    p.nuAnoLancamento = anoUsar;
    if (filters?.nuMesFiltro) p.nuMes = filters.nuMesFiltro;
    return p;
  },
  getParametrosAtual: (ugCd, anoAtual, mesAtual, filters) => {
    const p: Record<string, any> = {};
    if (ugCd) p.cdUnidadeGestora = ugCd;
    const anoUsar = filters?.dtAnoExercicioCTBFiltro ?? anoAtual;
    p.dtAnoExercicioCTB = anoUsar;
    p.nuAnoLancamento = anoUsar;
    if (filters?.nuMesFiltro) p.nuMes = filters.nuMesFiltro;
    else p.nuMes = mesAtual;
    return p;
  },
};

// ================================================================
// 5. Liquidacao
// ================================================================
export const liquidacao: EndpointDefinition = {
  name: "Liquidacao",
  tableName: "consumer_sefaz.liquidacao",
  url: "https://api-transparencia.apps.sefaz.se.gov.br/gfu/v1/liquidacao",
  nomeDataInicialPadraoFiltro: "dt_liquidacao",
  nomeDataFinalPadraoFiltro: "dt_liquidacao",
  dtAnoPadrao: "dt_ano_exercicio_ctb",
  parametrosRequeridos: false,
  requerIteracaoCdGestao: true,
  requerIteracaoEmpenhos: false,
  uniqueConstraintColumns: ["dt_ano_exercicio_ctb", "cd_unidade_gestora", "cd_gestao", "sq_empenho", "sq_liquidacao"],
  responseMapping: {
    sq_empenho: "sqEmpenho", sq_liquidacao: "sqLiquidacao",
    dt_liquidacao: "dtLiquidacao", cd_unidade_gestora: "cdUnidadeGestora",
    sg_unidade_gestora: "sgUnidadeGestora", id_orgao: "idOrgao", sg_orgao: "sgOrgao",
    id_orgao_supervisor: "idOrgaoSupervisor", sg_orgao_supervisor: "sgOrgaoSupervisor",
    vl_bruto_liquidacao: "vlBrutoLiquidacao", nu_documento: "nuDocumento",
    tp_documento: "tpDocumento", dt_ano_exercicio_ctb: "dtAnoExercicioCTB",
    cd_gestao: "cdGestao", nm_razao_social_pessoa: "nmRazaoSocialPessoa",
    cd_natureza_despesa_completa: "cdNaturezaDespesa", vl_estornado_liquidacao: "vlEstornadoLiquidacao",
    cd_funcao: "cdFuncao", nm_funcao: "nmFuncao",
    cd_sub_funcao: "cdSubFuncao", nm_sub_funcao: "nmSubFuncao",
    cd_elemento_despesa: "cdElementoDespesa", cd_fonte_recurso: "cdFonteRecurso",
    cd_licitacao: "cdLicitacao", ds_objeto_licitacao: "dsObjetoLicitacao",
    nu_processo_licitacao: "nuProcessoLicitacao", nm_modalidade_licitacao: "nmModalidadeLicitacao",
  },
  getParametrosTodosAnos: (ugCd, ano, filters) => {
    const p: Record<string, any> = {};
    if (ugCd) p.cdUnidadeGestora = ugCd;
    const anoUsar = filters?.dtAnoExercicioCTBFiltro ?? ano;
    p.dtAnoExercicioCTB = anoUsar;
    p.nuAnoLancamento = anoUsar;
    if (filters?.nuMesFiltro) p.nuMes = filters.nuMesFiltro;
    return p;
  },
  getParametrosAtual: (ugCd, anoAtual, mesAtual, filters) => {
    const p: Record<string, any> = {};
    if (ugCd) p.cdUnidadeGestora = ugCd;
    const anoUsar = filters?.dtAnoExercicioCTBFiltro ?? anoAtual;
    p.dtAnoExercicioCTB = anoUsar;
    p.nuAnoLancamento = anoUsar;
    if (filters?.nuMesFiltro) p.nuMes = filters.nuMesFiltro;
    else p.nuMes = mesAtual;
    return p;
  },
};

// ================================================================
// 6. Receita (Convenio)
// ================================================================
export const receita: EndpointDefinition = {
  name: "Receita",
  tableName: "consumer_sefaz.receita",
  url: "https://api-transparencia.apps.sefaz.se.gov.br/aco/v1/convenio/receita",
  nomeDataInicialPadraoFiltro: "dt_celebracao_convenio",
  nomeDataFinalPadraoFiltro: "dt_celebracao_convenio",
  dtAnoPadrao: null,
  parametrosRequeridos: false,
  requerIteracaoCdGestao: false,
  requerIteracaoEmpenhos: false,
  uniqueConstraintColumns: ["cd_convenio"],
  responseMapping: {
    dt_fim_vigencia_convenio: (i: any) => parseLocalDate(i.dtFimVigenciaConvenio),
    cd_convenio: "cdConvenio", cd_unidade_gestora: "cdUnidadeGestora",
    nm_concedente: "nmConcedente", in_convenio_ficha_ingresso: "inConvenioFichaIngresso",
    dt_publicacao_convenio: (i: any) => parseLocalDate(i.dtPublicacaoConvenio),
    ds_objeto_convenio: "dsObjetoConvenio",
    vl_concedente_convenio: "vlConcedenteConvenio", cd_gestao: "cdGestao",
    tx_ident_original_convenio: "txIdentOriginalConvenio",
    cd_concedente_pessoa: "cdConcedentePessoa", cd_efetivacao_usuario: "cdEfetivacaoUsuario",
    tx_observacao_convenio: "txObservacaoConvenio",
    cd_beneficiario_pessoa: "cdBeneficiarioPessoa", cd_convenio_situacao: "cdConvenioSituacao",
    cd_area_atuacao: "cdAreaAtuacao",
    dt_lancamento_convenio: (i: any) => parseLocalDate(i.dtLancamentoConvenio),
    dt_prazo_prest_contas_convenio: (i: any) => parseLocalDate(i.dtPrazoPrestContasConvenio),
    nm_beneficiario: "nmBeneficiario",
    dt_celebracao_convenio: (i: any) => parseLocalDate(i.dtCelebracaoConvenio),
    dt_inicio_vigencia_convenio: (i: any) => parseLocalDate(i.dtInicioVigenciaConvenio),
    sg_unidade_gestora: "sgUnidadeGestora", nm_convenio: "nmConvenio",
    in_local_publicacao_convenio: "inLocalPublicacaoConvenio",
    vl_contrapartida_convenio: "vlContrapartidaConvenio",
    in_convenio_empenho_ingresso: "inConvenioEmpenhoIngresso",
    sq_unidade_gestora_gestao: "sqUnidadeGestoraGestao",
  },
  getParametrosTodosAnos: (ugCd, ano) => {
    const p: Record<string, any> = {};
    if (ugCd) p.cdUnidadeGestora = ugCd;
    p.nuAnoCelebracao = ano;
    return p;
  },
  getParametrosAtual: (ugCd, anoAtual, mesAtual, filters) => {
    const p: Record<string, any> = {};
    if (ugCd) p.cdUnidadeGestora = ugCd;
    p.nuAnoCelebracao = filters?.nuAnoCelebracaoFiltro ?? anoAtual;
    p.nuMesCelebracao = filters?.nuMesCelebracaoFiltro ?? mesAtual;
    p.inVigente = "S";
    return p;
  },
};

// ================================================================
// 7. RestosAPagar
// ================================================================
export const restosAPagar: EndpointDefinition = {
  name: "RestosAPagar",
  tableName: "consumer_sefaz.restos_a_pagar",
  url: "https://api-transparencia.apps.sefaz.se.gov.br/gfu/v1/restos-a-pagar",
  nomeDataInicialPadraoFiltro: "dt_ano_exercicio_ctb",
  nomeDataFinalPadraoFiltro: "dt_ano_exercicio_ctb",
  dtAnoPadrao: "dt_ano_exercicio_ctb",
  parametrosRequeridos: false,
  requerIteracaoCdGestao: true,
  requerIteracaoEmpenhos: false,
  uniqueConstraintColumns: ["dt_ano_exercicio_ctb", "cd_unidade_gestora", "cd_gestao", "sq_empenho"],
  responseMapping: {
    vl_total_transferido_npp: "vlTotalTransferidoNpp", vl_empenho_inscrito_np: "vlEmpenhoInscritoNp",
    vl_empenho_inscrito_p: "vlEmpenhoInscritoP",
    vl_empenho_canc_rpnp_nao_exec: "vlEmpenhoCancRpnpNaoExec",
    vl_empenho_cancelado_rp: "vlEmpenhoCanceladoRp",
    vl_empenho_canc_divd_rpnp_exec: "vlEmpenhoCancDivdRpnpExec",
    vl_empenho_total_pago_np: "vlEmpenhoTotalPagoNp",
    sq_empenho: "sqEmpenho",
    vl_empenho_cancelado_divida_rp: "vlEmpenhoCanceladoDividaRp",
    cd_unidade_gestora: "cdUnidadeGestora", vl_empenho_total_pago_p: "vlEmpenhoTotalPagoP",
    vl_executado: "vlExecutado", dt_ano_exercicio_ctb: "dtAnoExercicioCTB",
    vl_empenho_canc_rpnp_executado: "vlEmpenhoCancRpnpExecutado",
    sq_solicitacao_empenho: "sqSolicitacaoEmpenho", cd_gestao: "cdGestao",
  },
  getParametrosTodosAnos: (ugCd, ano, filters) => {
    const p: Record<string, any> = {};
    if (ugCd) p.cdUnidadeGestora = ugCd;
    p.dtAnoExercicioCTB = filters?.dtAnoExercicioCTBFiltro ?? ano;
    return p;
  },
  getParametrosAtual: (ugCd, anoAtual, _mesAtual, filters) => {
    const p: Record<string, any> = {};
    if (ugCd) p.cdUnidadeGestora = ugCd;
    p.dtAnoExercicioCTB = filters?.dtAnoExercicioCTBFiltro ?? anoAtual;
    return p;
  },
};

// ================================================================
// 8. ContratoEmpenho
// ================================================================
export const contratoEmpenho: EndpointDefinition = {
  name: "ContratoEmpenho",
  tableName: "consumer_sefaz.contrato_empenho",
  url: "https://api-transparencia.apps.sefaz.se.gov.br/gbp/v1/contrato-empenho",
  nomeDataInicialPadraoFiltro: null,
  nomeDataFinalPadraoFiltro: null,
  dtAnoPadrao: "dt_ano_exercicio",
  parametrosRequeridos: false,
  requerIteracaoCdGestao: false,
  requerIteracaoEmpenhos: false,
  uniqueConstraintColumns: ["cd_solicitacao_compra", "ug_cd", "dt_ano_exercicio"],
  responseMapping: {
    cd_solicitacao_compra: "cdSolicitacaoCompra",
    ug_cd: (i: any) => i.ugCd || i.cdUnidadeGestoraNE || i.cdUnidadeGestoraSE || null,
    ds_resumida_solicitacao_compra: "dsResumidaSolicitacaoCompra",
    cd_licitacao: "cdLicitacao", dt_ano_exercicio: "dtAnoExercicio",
    situacao: "siLicitacao", nm_modalidade_licitacao: "nmModalidadeLicitacao",
    natureza_objeto_licitacao: "tpNaturezaObjetoLicitacao",
    ug_se: "cdUnidadeGestoraSE", doc_se: "docSe", valor_se: "vlSE",
    ug_ne: "cdUnidadeGestoraNE", doc_ne: "docNe",
    doc_credor_ne: "nuDocumentoCredor", nm_credor_ne: "nmCredor",
    crit_julg_licitacao: "tpCritJulgLicitacao", tipo_empenho: "tpEmpenho",
    vl_original_ne: "vlOriginalNE",
    vltotal_reforco_ne: "vlTotalReforcoNE",
    vltotal_anulado_ne: "vlTotalAnuladoNE",
    vltotal_liquidado_ne: "vlTotalLiquidadoNE",
    vl_total_pago_ne: "vlTotalPagoNE", cd_contrato: "cdContrato",
    tipo_contrato: "tpContrato", vl_contrato: "vlContrato",
    dt_inicio_vigencia_contrato: (i: any) => parseLocalDate(i.dtInicioVigenciaContrato),
    dt_fim_vigencia_contrato: (i: any) => parseLocalDate(i.dtFimVigenciaContrato),
    created_at: () => nowTimestamp(),
    updated_at: () => nowTimestamp(),
  },
  getParametrosTodosAnos: (ugCd, ano) => {
    const p: Record<string, any> = {};
    if (ugCd) p.cdUnidadeGestoraContrato = ugCd;
    if (ano) p.dtAnoExercicio = ano;
    return p;
  },
  getParametrosAtual: (ugCd, anoAtual, _mesAtual, filters) => {
    const p: Record<string, any> = {};
    if (ugCd) p.cdUnidadeGestoraContrato = ugCd;
    p.dtAnoExercicio = filters?.dtAnoExercicioFiltro ?? anoAtual;
    return p;
  },
};

// ================================================================
// 9. OrdemFornecimento
// ================================================================
export const ordemFornecimento: EndpointDefinition = {
  name: "OrdemFornecimento",
  tableName: "consumer_sefaz.ordem_fornecimento",
  url: "https://api-transparencia.apps.sefaz.se.gov.br/gbp/v1/ordem-fornecimento",
  nomeDataInicialPadraoFiltro: "dt_recebimento",
  nomeDataFinalPadraoFiltro: "dt_recebimento",
  dtAnoPadrao: "dt_ano_exercicio_emp",
  parametrosRequeridos: false,
  requerIteracaoCdGestao: false,
  requerIteracaoEmpenhos: false,
  uniqueConstraintColumns: ["cd_unidade_gestora", "cd_gestao", "dt_ano_exercicio_emp", "sq_empenho", "sq_ordem_fornecimento"],
  responseMapping: {
    cd_unidade_gestora: "cdUnidadeGestora", sg_unidade_gestora: "sgUnidadeGestora",
    cd_unidade_administrativa: "cdUnidadeAdministrativa", sg_unidade_administrativa: "sgUnidadeAdministrativa",
    cd_gestao: "cdGestao", dt_ano_exercicio_emp: "dtAnoExercicioEmp",
    dt_ano_exercicio_of: "dtAnoExercicioOF", sq_empenho: "sqEmpenho",
    sq_ordem_fornecimento: "sqOrdemFornecimento",
    dt_recebimento: "dtRecebimento", vl_ordem_fornecimento: "vlOrdemFornecimento",
    nu_danfe: "nuDanfe", dt_emissao: "dtEmissao",
    nu_documento_destinatario: "nuDocumentoDestinatario", nm_destinatario: "nmDestinatario",
    nu_documento_remetente: "nuDocumentoRemetente", nm_remetente: "nmRemetente",
    vl_total_nfe: "vlTotalNfe", vl_icms: "vlIcms", vl_ipi: "vlIpi",
  },
  getParametrosTodosAnos: (ugCd, ano, filters) => {
    const p: Record<string, any> = {};
    if (ugCd) p.cdUnidadeGestora = ugCd;
    p.dtAnoExercicioEmp = filters?.dtAnoExercicioEmpFiltro ?? ano;
    if (filters?.nuMesRecebimentoFiltro) p.nuMesRecebimento = filters.nuMesRecebimentoFiltro;
    return p;
  },
  getParametrosAtual: (ugCd, anoAtual, mesAtual, filters) => {
    const p: Record<string, any> = {};
    if (ugCd) p.cdUnidadeGestora = ugCd;
    p.dtAnoExercicioEmp = filters?.dtAnoExercicioEmpFiltro ?? anoAtual;
    p.nuMesRecebimento = filters?.nuMesRecebimentoFiltro ?? mesAtual;
    return p;
  },
};

// ================================================================
// 10. DespesaDetalhada
// ================================================================
export const despesaDetalhada: EndpointDefinition = {
  name: "DespesaDetalhada",
  tableName: "consumer_sefaz.despesa_detalhada",
  url: "https://api-transparencia.apps.sefaz.se.gov.br/ctb/v1/despesa-detalhada",
  nomeDataInicialPadraoFiltro: null,
  nomeDataFinalPadraoFiltro: null,
  dtAnoPadrao: "dt_ano_exercicio_ctb",
  parametrosRequeridos: false,
  requerIteracaoCdGestao: false,
  requerIteracaoEmpenhos: false,
  requerMultiplasRequisicoes: true,
  multiplasRequisicoesCount: 12,
  multiplasRequisicoesPauseMs: 500,
  uniqueConstraintColumns: ["dt_ano_exercicio_ctb", "nu_mes", "cd_orgao", "cd_unid_orc", "cd_natureza_despesa", "cd_ppa_acao", "cd_sub_acao"],
  responseMapping: {
    cd_unidade_gestora: (i: any) => i.cdUnidadeGestora || i._cdUnidadeGestora || null,
    dt_ano_exercicio_ctb: (i: any) => i.dtAnoExercicioCTB || i._dtAnoExercicioCTB || null,
    nu_mes: (i: any) => i.nuMes || i._nuMes || null,
    cd_orgao: "cdOrgao", nm_orgao: "nmOrgao",
    cd_unid_orc: "cdUnidOrc", nm_unid_orc: "nmUnidOrc",
    cd_funcao: "cdFuncao", nm_funcao: "nmFuncao",
    cd_sub_funcao: "cdSubFuncao", nm_sub_funcao: "nmSubFuncao",
    cd_programa_governo: "cdProgramaGoverno", nm_programa_governo: "nmProgramaGoverno",
    cd_ppa_acao: "cdPPAAcao", nm_ppa_acao: "nmPPAAcao",
    cd_sub_acao: "cdSubAcao", nm_subacao: "nmSubacao",
    cd_categoria_economica: "cdCategoriaEconomica", nm_categoria_economica: "nmCategoriaEconomica",
    cd_natureza_despesa: "cdNaturezaDespesa", nm_natureza_despesa: "nmNaturezaDespesa",
    vl_dotacao_inicial: "vlDotacaoInicial", vl_credito_adicional: "vlCreditoAdicional",
    vl_dotacao_atualizada: "vlDotacaoAtualizada",
    vl_total_empenhado: "vlTotalEmpenhado", vl_total_liquidado: "vlTotalLiquidado",
    vl_total_pago: "vlTotalPago",
    created_at: () => nowTimestamp(),
    updated_at: () => nowTimestamp(),
  },
  getParametrosTodosAnos: (ugCd, ano, filters) => {
    const p: Record<string, any> = {};
    if (ugCd) p.cdUnidadeGestora = ugCd;
    p.dtAnoExercicio = ano;
    p.nuMes = filters?.nuMesFiltro ?? 12;
    return p;
  },
  getParametrosAtual: (ugCd, anoAtual, mesAtual, filters) => {
    const p: Record<string, any> = {};
    if (ugCd) p.cdUnidadeGestora = ugCd;
    p.dtAnoExercicio = filters?.dtAnoExercicioCTBFiltro ?? anoAtual;
    p.nuMes = filters?.nuMesFiltro ?? mesAtual ?? 12;
    return p;
  },
};

// ================================================================
// 11. PrevisaoRealizacaoReceita
// ================================================================
export const previsaoRealizacaoReceita: EndpointDefinition = {
  name: "PrevisaoRealizacaoReceita",
  tableName: "consumer_sefaz.previsao_realizacao_receita",
  url: "https://api-transparencia.apps.sefaz.se.gov.br/ctb/v1/previsao-realizacao-receita",
  nomeDataInicialPadraoFiltro: null,
  nomeDataFinalPadraoFiltro: null,
  dtAnoPadrao: "dt_ano_exercicio_ctb",
  parametrosRequeridos: false,
  requerIteracaoCdGestao: false,
  requerIteracaoEmpenhos: false,
  requerMultiplasRequisicoes: true,
  multiplasRequisicoesCount: 12,
  uniqueConstraintColumns: ["cd_unidade_gestora", "dt_ano_exercicio_ctb", "nu_mes", "cd_categoria_economica", "cd_origem", "cd_especie", "cd_desdobramento", "cd_tipo"],
  responseMapping: {
    cd_unidade_gestora: "cdUnidadeGestora",
    dt_ano_exercicio_ctb: (i: any) => {
      const v = i.dtAnoExercicioCTB ?? i._dtAnoExercicioCTB ?? (i as any).dtAnoExercicioCTBString;
      if (v == null || v === "") return null;
      const n = typeof v === "number" ? v : parseInt(String(v), 10);
      return Number.isNaN(n) ? null : n;
    },
    nu_mes: (i: any) => i.nuMes || i._nuMes || null,
    cd_categoria_economica: (i: any) => i.cdCategoriaEconomica ?? "",
    nm_categoria_economica: (i: any) => i.nmCategoriaEconomica ?? "",
    cd_origem: (i: any) => i.cdOrigem ?? "",
    nm_origem: (i: any) => i.nmOrigem ?? "",
    cd_especie: (i: any) => i.cdEspecie ?? "",
    nm_especie: (i: any) => i.nmEspecie ?? "",
    cd_desdobramento: (i: any) => i.cdDesdobramento ?? "",
    nm_desdobramento: (i: any) => i.nmDesdobramento ?? "",
    cd_tipo: (i: any) => i.cdTipo ?? "",
    nm_tipo: (i: any) => i.nmTipo ?? "",
    vl_previsto: "vlPrevisto", vl_atualizado: "vlAtualizado", vl_realizado: "vlRealizado",
    created_at: () => nowTimestamp(),
    updated_at: () => nowTimestamp(),
  },
  getParametrosTodosAnos: (ugCd, ano, filters) => {
    const p: Record<string, any> = {};
    if (ugCd) p.cdUnidadeGestora = ugCd;
    p.dtAnoExercicioCTB = ano;
    p.nuMes = filters?.nuMesFiltro ?? 12;
    return p;
  },
  getParametrosAtual: (ugCd, anoAtual, mesAtual, filters) => {
    const p: Record<string, any> = {};
    if (ugCd) p.cdUnidadeGestora = ugCd;
    p.dtAnoExercicioCTB = filters?.dtAnoExercicioCTBFiltro ?? anoAtual;
    p.nuMes = filters?.nuMesFiltro ?? mesAtual ?? 12;
    return p;
  },
};

// ================================================================
// 12. ConsultaGerencial
// ================================================================
export const consultaGerencial: EndpointDefinition = {
  name: "ConsultaGerencial",
  tableName: "consumer_sefaz.consulta_gerencial",
  url: "https://api-transparencia.apps.sefaz.se.gov.br/gfu/v1/diarias/consulta-gerencial",
  nomeDataInicialPadraoFiltro: null,
  nomeDataFinalPadraoFiltro: null,
  dtAnoPadrao: "dt_ano_exercicio_ctb",
  parametrosRequeridos: false,
  requerIteracaoCdGestao: true,
  requerIteracaoEmpenhos: false,
  uniqueConstraintColumns: null,
  responseMapping: {
    cd_unidade_gestora: "cdUnidadeGestora", sg_unidade_gestora: "sgUnidadeGestora",
    dt_ano_exercicio_ctb: "dtAnoExercicioCTB", cd_gestao: "cdGestao",
    tx_motivo_solicitacao: "txMotivoSolicitacao",
    dt_saida_solicitacao_diaria: (i: any) => parseLocalDate(i.dtSaidaSolicitacaoDiariaStr ?? i.dtSaidaSolicitacaoDiaria),
    dt_retorno_solicitacao_diaria: (i: any) => parseLocalDate(i.dtRetornoSolicitacaoDiariaStr ?? i.dtRetornoSolicitacaoDiaria),
    qtd_diaria_solicitacao_diaria: "qtdDiariaSolicitacaoDiaria",
    vl_total_solicitacao_diaria: (i: any) => parseDecimalFlexible(i.vlTotalSolicitacaoDiariaStr ?? i.vlTotalSolicitacaoDiaria),
    vl_desconto_solicitacao_diaria: (i: any) => parseDecimalFlexible(i.vlDescontoSolicitacaoDiariaStr ?? i.vlDescontoSolicitacaoDiaria),
    vl_valor_moeda: (i: any) => parseDecimalFlexible(i.vlValorMoedaStr ?? i.vlValorMoeda),
    vl_total_valor_pago_atualizado: (i: any) => parseDecimalFlexible(i.vlTotalValorPagoAtualizadoStr ?? i.vlTotalValorPagoAtualizado),
    sq_solic_empenho: "sqSolicEmpenho", sq_empenho: "sqEmpenho",
    sq_solicitacao_diaria: "sqSolicitacaoDiaria", sq_ob: "sqOB",
    sq_previsao_desembolso: "sqPrevisaoDesembolso",
    tp_documento: "tpDocumento", nu_documento: "nuDocumento",
    nm_razao_social_pessoa: "nmRazaoSocialPessoa", ds_qualificacao_vinculo: "dsQualificacaoVinculo",
    destino_viagem_pais_solicitacao_diaria: "destinoViagemPaisSolicitacaoDiaria",
    destino_viagem_uf_solicitacao_diaria: "destinoViagemUFSolicitacaoDiaria",
    destino_viagem_municipio_solicitacao_diaria: "destinoViagemMunicipioSolicitacaoDiaria",
    tp_transporte_viagem_solicitacao_diaria: "tpTransporteViagemSolicitacaoDiaria",
    tp_viagem_solicitacao_diaria: "tpViagemSolicitacaoDiaria",
    nm_cargo: "nmCargo", cd_situacao_solicitacao: "cdSituacaoSolicitacao",
    created_at: () => nowTimestamp(),
    updated_at: () => nowTimestamp(),
  },
  getParametrosTodosAnos: (ugCd, ano, filters) => {
    const p: Record<string, any> = {};
    if (ugCd) p.cdUnidadeGestora = ugCd;
    p.dtAnoExercicioCTB = ano;
    if (filters?.nuMesFiltro) p.nuMes = filters.nuMesFiltro;
    return p;
  },
  getParametrosAtual: (ugCd, anoAtual, mesAtual, filters) => {
    const p: Record<string, any> = {};
    if (ugCd) p.cdUnidadeGestora = ugCd;
    p.dtAnoExercicioCTB = filters?.dtAnoExercicioCTBFiltro ?? anoAtual;
    p.nuMes = filters?.nuMesFiltro ?? mesAtual;
    return p;
  },
};

// ================================================================
// 13. ContratosFiscais
// ================================================================
export const contratosFiscais: EndpointDefinition = {
  name: "ContratosFiscais",
  tableName: "consumer_sefaz.contratos_fiscais",
  url: "https://api-transparencia.apps.sefaz.se.gov.br/gbp/v1/contrato-fiscais",
  nomeDataInicialPadraoFiltro: "dt_inicio_vigencia_contrato",
  nomeDataFinalPadraoFiltro: "dt_fim_vigencia_contrato",
  dtAnoPadrao: "dt_ano_exercicio",
  parametrosRequeridos: false,
  requerIteracaoCdGestao: false,
  requerIteracaoEmpenhos: false,
  uniqueConstraintColumns: null,
  responseMapping: {
    dt_fim_vigencia_contrato: (i: any) => parseLocalDate(i.dtFimVigenciaContrato),
    sg_unidade_gestora: "sgUnidadeGestora", cd_licitacao: "cdLicitacao",
    cd_unidade_gestora: "cdUnidadeGestora", nm_fiscal: "nmFiscal",
    nu_documento_contratado: "nuDocumentoContratado", nm_contratado: "nmContratado",
    dt_ano_exercicio: "dtAnoExercicio", cd_contrato: "cdContrato",
    dt_inicio_vigencia_contrato: (i: any) => parseLocalDate(i.dtInicioVigenciaContrato),
    ds_qualificador: "dsQualificador",
  },
  getParametrosTodosAnos: (ugCd, ano, filters) => {
    const p: Record<string, any> = {};
    if (ugCd) p.cdUnidadeGestora = ugCd;
    p.dtAnoExercicio = filters?.dtAnoExercicioFiltro ?? ano;
    if (filters?.nuMesFiltro) p.nuMes = filters.nuMesFiltro;
    p.cdQualificador = 133;
    return p;
  },
  getParametrosAtual: (ugCd, anoAtual, mesAtual, filters) => {
    const p: Record<string, any> = {};
    if (ugCd) p.cdUnidadeGestora = ugCd;
    p.dtAnoExercicio = filters?.dtAnoExercicioFiltro ?? anoAtual;
    p.nuMes = filters?.nuMesFiltro ?? mesAtual;
    p.cdQualificador = 133;
    return p;
  },
};

// ================================================================
// 14. BaseDespesaCredor
// ================================================================
export const baseDespesaCredor: EndpointDefinition = {
  name: "BaseDespesaCredor",
  tableName: "consumer_sefaz.base_despesa_credor",
  url: "https://api-transparencia.apps.sefaz.se.gov.br/gbp/v1/base-despesa-credor",
  nomeDataInicialPadraoFiltro: null,
  nomeDataFinalPadraoFiltro: null,
  dtAnoPadrao: "dt_ano_exercicio",
  parametrosRequeridos: true,
  requerIteracaoCdGestao: false,
  requerIteracaoEmpenhos: false,
  uniqueConstraintColumns: null,
  responseMapping: {
    cd_gestao: "cdGestao", ug_cd: "cdUnidadeGestora",
    dt_ano_exercicio: "dtAnoExercicio", sq_empenho: "sqEmpenho",
    nu_documento_pessoa: "nuDocumentoPessoa", cd_tipo_documento: "cdTipoDocumento",
    nm_razao_social_pessoa: "nmRazaoSocialPessoa",
    dh_lancamento_empenho: (i: any) => parseLocalDateTime(i.dhLancamentoEmpenho),
    dt_geracao_empenho: "dtGeracaoEmpenho",
    vl_original_empenho: "vlOriginalEmpenho", vl_total_liquidado_empenho: "vlTotalLiquidadoEmpenho",
    vl_total_pago_empenho: "vlTotalPagoEmpenho",
    nm_modalidade_licitacao: "nmModalidadeLicitacao", cd_licitacao: "cdLicitacao",
    nm_item_material_servico: "nmItemMaterialServico",
    qt_item_solicitacao_empenho: "qtItemSolicitacaoEmpenho",
    vl_unitario_item_solicitacao_empenho: "vlUnitarioItemSolicitacaoEmpenho",
    created_at: () => nowTimestamp(),
    updated_at: () => nowTimestamp(),
  },
  getParametrosTodosAnos: (ugCd, ano) => {
    const p: Record<string, any> = {};
    if (ugCd) p.cdUnidadeGestora = ugCd;
    if (ano) p.dtAnoExercicio = ano;
    p.nuFaixaPaginacao = 1;
    return p;
  },
  getParametrosAtual: (ugCd, anoAtual, _mesAtual, filters) => {
    const p: Record<string, any> = {};
    if (ugCd) p.cdUnidadeGestora = ugCd;
    p.dtAnoExercicio = filters?.dtAnoExercicioFiltro ?? anoAtual;
    p.nuFaixaPaginacao = filters?.nuFaixaPaginacaoFiltro ?? 1;
    return p;
  },
};

// ================================================================
// 15. BaseDespesaLicitacao
// ================================================================
export const baseDespesaLicitacao: EndpointDefinition = {
  name: "BaseDespesaLicitacao",
  tableName: "consumer_sefaz.base_despesa_licitacao",
  url: "https://api-transparencia.apps.sefaz.se.gov.br/gbp/v1/base-despesa-licitacao",
  nomeDataInicialPadraoFiltro: null,
  nomeDataFinalPadraoFiltro: null,
  dtAnoPadrao: null,
  parametrosRequeridos: false,
  requerIteracaoCdGestao: false,
  requerIteracaoEmpenhos: false,
  uniqueConstraintColumns: null,
  responseMapping: {
    nu_processo: "nuProcesso", si_licitacao: "siLicitacao", nu_documento: "nuDocumento",
    nm_razao_social_ug: "nmRazaoSocialUg", nm_razao_social_fornecedor: "nmRazaoSocialFornecedor",
    cd_licitacao: "cdLicitacao", vl_licitacao: "vlLicitacao",
    sg_unidade_gestora: "sgUnidadeGestora", cd_unidade_gestora: "cdUnidadeGestora",
    nm_modalidade: "nmModalidade",
    dt_homologacao: (i: any) => parseLocalDate(i.dtHomologacao),
    vl_estimado: "vlEstimado", ds_objeto: "dsObjeto",
    created_at: () => nowTimestamp(),
    updated_at: () => nowTimestamp(),
  },
  getParametrosTodosAnos: (ugCd, ano) => {
    const p: Record<string, any> = {};
    if (ugCd) p.cdUnidadeGestora = ugCd;
    if (ano) p.dtAnoExercicio = ano;
    return p;
  },
  getParametrosAtual: (ugCd, anoAtual, _mesAtual, filters) => {
    const p: Record<string, any> = {};
    if (ugCd) p.cdUnidadeGestora = ugCd;
    p.dtAnoExercicio = filters?.dtAnoExercicioFiltro ?? anoAtual;
    return p;
  },
};

// ================================================================
// 16. TotalizadoresExecucao
// ================================================================
export const totalizadoresExecucao: EndpointDefinition = {
  name: "TotalizadoresExecucao",
  tableName: "consumer_sefaz.totalizadores_execucao",
  url: "https://api-transparencia.apps.sefaz.se.gov.br/gfu/v1/totalizadores-execucao",
  nomeDataInicialPadraoFiltro: null,
  nomeDataFinalPadraoFiltro: null,
  dtAnoPadrao: "dt_ano_exercicio_ctb",
  parametrosRequeridos: false,
  requerIteracaoCdGestao: false,
  requerIteracaoEmpenhos: false,
  uniqueConstraintColumns: null,
  responseMapping: {
    cd_programa_governo: "cdProgramaGoverno",
    cd_natureza_despesa_elemento_despesa: "cdNaturezaDespesaElementoDespesa",
    nm_natureza_despesa_elemento_despesa: "nmNaturezaDespesaElementoDespesa",
    nm_programa_governo: "nmProgramaGoverno",
    vl_total_pago: "vlTotalPago", vl_total_liquidado: "vlTotalLiquidado",
    cd_unidade_orcamentaria: "cdUnidadeOrcamentaria",
    vl_total_empenhado: "vlTotalEmpenhado", nm_fonte_recurso: "nmFonteRecurso",
    nm_complemento_exec_orc: "nmComplementoExecOrc",
    vl_total_dotacao_atualizada: "vlTotalDotacaoAtualizada",
    cd_fonte_recurso_reduzida: "cdFonteRecursoReduzida",
    cd_sub_acao: "cdSubAcao", nm_sub_acao: "nmSubAcao",
    sg_unidade_gestora: "sgUnidadeGestora", cd_unidade_gestora: "cdUnidadeGestora",
    dh_ultima_alteracao: (i: any) => parseLocalDateTime(i.dhUltimaAlteracao),
    nm_acao: "nmAcao", dt_ano_exercicio_ctb: "dtAnoExercicioCTB",
    cd_acao: "cdAcao", cd_gestao: "cdGestao",
    cd_complemento_exec_orc: "cdComplementoExecOrc",
    created_at: () => nowTimestamp(),
    updated_at: () => nowTimestamp(),
  },
  getParametrosTodosAnos: (ugCd, ano) => {
    const p: Record<string, any> = {};
    if (ugCd) p.cdUnidadeGestora = ugCd;
    if (ano) p.dtAnoExercicioCTB = ano;
    return p;
  },
  getParametrosAtual: (ugCd, anoAtual, _mesAtual, filters) => {
    const p: Record<string, any> = {};
    if (ugCd) p.cdUnidadeGestora = ugCd;
    p.dtAnoExercicioCTB = filters?.dtAnoExercicioCTBFiltro ?? anoAtual;
    return p;
  },
};

// ================================================================
// 17. DadosOrcamentarios
// ================================================================
export const dadosOrcamentarios: EndpointDefinition = {
  name: "DadosOrcamentarios",
  tableName: "consumer_sefaz.dados_orcamentarios",
  url: "https://api-transparencia.apps.sefaz.se.gov.br/gfu/v1/empenho/dados-orcamentarios",
  nomeDataInicialPadraoFiltro: null,
  nomeDataFinalPadraoFiltro: null,
  dtAnoPadrao: null,
  parametrosRequeridos: true,
  requerIteracaoCdGestao: false,
  requerIteracaoEmpenhos: true,
  uniqueConstraintColumns: null,
  responseMapping: {
    cd_funcao_plo: "cdFuncao", nm_funcao_plo: "nmFuncao",
    cd_sub_funcao: "cdSubFuncao", nm_sub_funcao: "nmSubFuncao",
    cd_programa_governo: "cdProgramaGoverno", nm_programa_governo: "nmProgramaGoverno",
    cd_categoria_economica: "cdCategoriaEconomica", nm_categoria_economica: "nmCategoriaEconomica",
    cd_modalidade_aplicacao: "cdModalidadeAplicacao", nm_modalidade_aplicacao: "nmModalidadeAplicacao",
    cd_fonte_recurso: "cdFonteRecurso", nm_fonte_recurso: "nmFonteRecurso",
    cd_grupo_despesa: "cdGrupoDespesa", nm_grupo_despesa: "nmGrupoDespesa",
    cd_licitacao: "cdLicitacao", ds_objeto_licitacao: "dsObjetoLicitacao",
    nu_processo_licitacao: "nuProcessoLicitacao",
    cd_elemento_despesa: "cdElementoDespesa", nm_elemento_despesa: "nmElementoDespesa",
    cd_tipo_despesa: "cdTipoDespesa", ds_tipo_despesa: "dsTipoDespesa",
  },
  getParametrosTodosAnos: (ugCd, ano) => {
    const p: Record<string, any> = {};
    if (ugCd) p.cdUnidadeGestora = ugCd;
    p.dtAnoExercicioCTB = ano;
    return p;
  },
  getParametrosAtual: (ugCd, anoAtual) => {
    const p: Record<string, any> = {};
    if (ugCd) p.cdUnidadeGestora = ugCd;
    p.dtAnoExercicioCTB = anoAtual;
    return p;
  },
};

// ================================================================
// 18. DespesaConvenio
// ================================================================
export const despesaConvenio: EndpointDefinition = {
  name: "DespesaConvenio",
  tableName: "consumer_sefaz.convenio_despesa",
  url: "https://api-transparencia.apps.sefaz.se.gov.br/aco/v1/convenio/despesa",
  nomeDataInicialPadraoFiltro: "dt_lancamento_convenio",
  nomeDataFinalPadraoFiltro: "dt_lancamento_convenio",
  dtAnoPadrao: null,
  parametrosRequeridos: false,
  requerIteracaoCdGestao: false,
  requerIteracaoEmpenhos: false,
  uniqueConstraintColumns: null,
  responseMapping: {
    dt_fim_vigencia_convenio: (i: any) => parseLocalDate(i.dtFimVigenciaConvenio),
    cd_convenio: "cdConvenio", cd_unidade_gestora: "cdUnidadeGestora",
    nm_concedente: "nmConcedente", in_convenio_ficha_ingresso: "inConvenioFichaIngresso",
    dt_publicacao_convenio: (i: any) => parseLocalDate(i.dtPublicacaoConvenio),
    ds_objeto_convenio: "dsObjetoConvenio",
    vl_concedente_convenio: "vlConcedenteConvenio", cd_gestao: "cdGestao",
    tx_ident_original_convenio: "txIdentOriginalConvenio",
    cd_concedente_pessoa: "cdConcedentePessoa", cd_efetivacao_usuario: "cdEfetivacaoUsuario",
    tx_observacao_convenio: "txObservacaoConvenio",
    cd_beneficiario_pessoa: "cdBeneficiarioPessoa", cd_convenio_situacao: "cdConvenioSituacao",
    cd_area_atuacao: "cdAreaAtuacao",
    dt_lancamento_convenio: (i: any) => parseLocalDate(i.dtLancamentoConvenio),
    dt_prazo_prest_contas_convenio: (i: any) => parseLocalDate(i.dtPrazoPrestContasConvenio),
    nm_beneficiario: "nmBeneficiario",
    dt_celebracao_convenio: (i: any) => parseLocalDate(i.dtCelebracaoConvenio),
    dt_inicio_vigencia_convenio: (i: any) => parseLocalDate(i.dtInicioVigenciaConvenio),
    sg_unidade_gestora: "sgUnidadeGestora", nm_convenio: "nmConvenio",
    in_local_publicacao_convenio: "inLocalPublicacaoConvenio",
    vl_contrapartida_convenio: "vlContrapartidaConvenio",
    in_convenio_empenho_ingresso: "inConvenioEmpenhoIngresso",
    sq_unidade_gestora_gestao: "sqUnidadeGestoraGestao",
    created_at: () => nowTimestamp(),
    updated_at: () => nowTimestamp(),
  },
  getParametrosTodosAnos: (ugCd, ano) => {
    const p: Record<string, any> = {};
    if (ugCd) p.cdUnidadeGestora = ugCd;
    p.nuAnoLancamento = ano;
    return p;
  },
  getParametrosAtual: (ugCd, anoAtual, mesAtual, filters) => {
    const p: Record<string, any> = {};
    if (ugCd) p.cdUnidadeGestora = ugCd;
    p.nuAnoLancamento = filters?.nuAnoLancamentoFiltro ?? anoAtual;
    p.nuMesLancamento = filters?.nuMesLancamentoFiltro ?? mesAtual;
    p.inVigente = "S";
    return p;
  },
};

// ================================================================
// 19. Termo
// ================================================================
export const termo: EndpointDefinition = {
  name: "Termo",
  tableName: "consumer_sefaz.termo",
  url: "https://api-transparencia.apps.sefaz.se.gov.br/aco/v1/convenio/termo",
  nomeDataInicialPadraoFiltro: "dt_inicio_vigencia_conv",
  nomeDataFinalPadraoFiltro: "dt_fim_vigencia_conv",
  dtAnoPadrao: "dt_inicio_vigencia_conv",
  parametrosRequeridos: false,
  requerIteracaoCdGestao: false,
  requerIteracaoEmpenhos: false,
  uniqueConstraintColumns: null,
  responseMapping: {
    cd_convenio: "cdConvenio", cd_unidade_gestora: "cdUnidadeGestora",
    cd_gestao: "cdGestao", sg_unidade_gestora: "sgUnidadeGestora",
    nm_convenio: "nmConvenio", ds_objeto_convenio: "dsObjetoConvenio",
    dt_celebracao_convenio: (i: any) => parseLocalDate(i.dtCelebracaoConvenio),
    dt_inicio_vigencia_conv: (i: any) => parseLocalDate(i.dtInicioVigenciaConvenio),
    dt_fim_vigencia_conv: (i: any) => parseLocalDate(i.dtFimVigenciaConvenio),
    dt_publicacao_convenio: (i: any) => parseLocalDate(i.dtPublicacaoConvenio),
    nu_doc_oficial_convenio: "nuDocOficialConvenio",
    tx_ident_original_conv: "txIdentOriginalConvenio",
    tx_observacao_convenio: "txObservacaoConvenio",
    cd_efetivacao_usuario: "cdEfetivacaoUsuario",
    cd_convenio_situacao: "cdConvenioSituacao",
    cd_area_atuacao: "cdAreaAtuacao",
    in_local_publicacao_conv: "inLocalPublicacaoConvenio",
  },
  getParametrosTodosAnos: (ugCd) => {
    const p: Record<string, any> = {};
    if (ugCd) p.cdUnidadeGestora = ugCd;
    return p;
  },
  getParametrosAtual: (ugCd) => {
    const p: Record<string, any> = {};
    if (ugCd) p.cdUnidadeGestora = ugCd;
    return p;
  },
};

// ================================================================
// 20. EmpenhoMensal (inherits from Empenho)
// ================================================================
export const empenhoMensal: EndpointDefinition = {
  ...empenho,
  name: "EmpenhoMensal",
  getParametrosTodosAnos: (ugCd, ano, filters) => {
    const p: Record<string, any> = {};
    if (ugCd) p.cdUnidadeGestora = ugCd;
    p.dtAnoExercicioCTB = filters?.dtAnoExercicioCTBFiltro ?? ano;
    if (filters?.nuMesFiltro) p.nuMes = filters.nuMesFiltro;
    return p;
  },
  getParametrosAtual: (ugCd, anoAtual, _mesAtual, filters) => {
    const p: Record<string, any> = {};
    if (ugCd) p.cdUnidadeGestora = ugCd;
    p.dtAnoExercicioCTB = filters?.dtAnoExercicioCTBFiltro ?? anoAtual;
    if (filters?.nuMesFiltro) p.nuMes = filters.nuMesFiltro;
    return p;
  },
};

export const ALL_ENDPOINTS: Record<string, EndpointDefinition> = {
  unidadeGestora, contrato, empenho, pagamento, liquidacao, receita,
  restosAPagar, contratoEmpenho, ordemFornecimento, despesaDetalhada,
  previsaoRealizacaoReceita, consultaGerencial, contratosFiscais,
  baseDespesaCredor, baseDespesaLicitacao, totalizadoresExecucao,
  dadosOrcamentarios, despesaConvenio, termo, empenhoMensal,
};

export type { EndpointDefinition };
