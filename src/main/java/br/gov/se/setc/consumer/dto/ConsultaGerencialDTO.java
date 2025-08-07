package br.gov.se.setc.consumer.dto;

import br.gov.se.setc.consumer.contracts.EndpontSefaz;
import br.gov.se.setc.util.ValidacaoUtil;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * DTO para consumo da API de Consulta Gerencial (Diárias) do SEFAZ
 * Endpoint: https://api-transparencia.apps.sefaz.se.gov.br/gfu/v1/diarias/consulta-gerencial
 */
public class ConsultaGerencialDTO extends EndpontSefaz {

    @JsonProperty("cdUnidadeGestora")
    private String cdUnidadeGestora;

    @JsonProperty("sgUnidadeGestora")
    private String sgUnidadeGestora;

    @JsonProperty("dtAnoExercicioCTB")
    private Integer dtAnoExercicioCTB;

    @JsonProperty("cdGestao")
    private String cdGestao;

    @JsonProperty("txMotivoSolicitacao")
    private String txMotivoSolicitacao;

    @JsonProperty("dtSaidaSolicitacaoDiaria")
    private String dtSaidaSolicitacaoDiariaStr;

    @JsonProperty("dtRetornoSolicitacaoDiaria")
    private String dtRetornoSolicitacaoDiariaStr;

    @JsonProperty("qtdDiariaSolicitacaoDiaria")
    private Integer qtdDiariaSolicitacaoDiaria;

    @JsonProperty("vlTotalSolicitacaoDiaria")
    private String vlTotalSolicitacaoDiariaStr;

    @JsonProperty("vlDescontoSolicitacaoDiaria")
    private String vlDescontoSolicitacaoDiariaStr;

    @JsonProperty("vlValorMoeda")
    private String vlValorMoedaStr;

    @JsonProperty("sqSolicEmpenho")
    private Long sqSolicEmpenho;

    @JsonProperty("sqEmpenho")
    private Long sqEmpenho;

    @JsonProperty("sqSolicitacaoDiaria")
    private Long sqSolicitacaoDiaria;

    @JsonProperty("sqOB")
    private Long sqOB;

    @JsonProperty("sqPrevisaoDesembolso")
    private Long sqPrevisaoDesembolso;

    @JsonProperty("tpDocumento")
    private Integer tpDocumento;

    @JsonProperty("nuDocumento")
    private String nuDocumento;

    @JsonProperty("nmRazaoSocialPessoa")
    private String nmRazaoSocialPessoa;

    @JsonProperty("dsQualificacaoVinculo")
    private String dsQualificacaoVinculo;

    @JsonProperty("destinoViagemPaisSolicitacaoDiaria")
    private String destinoViagemPaisSolicitacaoDiaria;

    @JsonProperty("destinoViagemUFSolicitacaoDiaria")
    private String destinoViagemUFSolicitacaoDiaria;

    @JsonProperty("destinoViagemMunicipioSolicitacaoDiaria")
    private String destinoViagemMunicipioSolicitacaoDiaria;

    @JsonProperty("tpTransporteViagemSolicitacaoDiaria")
    private String tpTransporteViagemSolicitacaoDiaria;

    @JsonProperty("tpViagemSolicitacaoDiaria")
    private String tpViagemSolicitacaoDiaria;

    @JsonProperty("nmCargo")
    private String nmCargo;

    // Campos convertidos para tipos apropriados
    private LocalDate dtSaidaSolicitacaoDiaria;
    private LocalDate dtRetornoSolicitacaoDiaria;
    private BigDecimal vlTotalSolicitacaoDiaria;
    private BigDecimal vlDescontoSolicitacaoDiaria;
    private BigDecimal vlValorMoeda;

    // Campos para parâmetros de filtro
    private String cdUnidadeGestoraFiltro;
    private Integer dtAnoExercicioCTBFiltro;

    public ConsultaGerencialDTO() {
        inicializarDadosEndpoint();
        mapearCamposResposta();
        mapearParametros();
    }

    @Override
    protected void inicializarDadosEndpoint() {
        tabelaBanco = "consumer_sefaz.consulta_gerencial";
        url = "https://api-transparencia.apps.sefaz.se.gov.br/gfu/v1/diarias/consulta-gerencial";
        nomeDataInicialPadraoFiltro = null; // Não usa filtros de data
        nomeDataFinalPadraoFiltro = null;   // Não usa filtros de data
        dtAnoPadrao = "dt_ano_exercicio_ctb";
    }

    @Override
    public void mapearCamposResposta() {
        // Mapear campos de resposta para persistência no banco
        camposResposta.put("cd_unidade_gestora", cdUnidadeGestora);
        camposResposta.put("sg_unidade_gestora", sgUnidadeGestora);
        camposResposta.put("dt_ano_exercicio_ctb", dtAnoExercicioCTB);
        camposResposta.put("cd_gestao", cdGestao);
        camposResposta.put("tx_motivo_solicitacao", txMotivoSolicitacao);
        camposResposta.put("dt_saida_solicitacao_diaria", dtSaidaSolicitacaoDiaria);
        camposResposta.put("dt_retorno_solicitacao_diaria", dtRetornoSolicitacaoDiaria);
        camposResposta.put("qtd_diaria_solicitacao_diaria", qtdDiariaSolicitacaoDiaria);
        camposResposta.put("vl_total_solicitacao_diaria", vlTotalSolicitacaoDiaria);
        camposResposta.put("vl_desconto_solicitacao_diaria", vlDescontoSolicitacaoDiaria);
        camposResposta.put("vl_valor_moeda", vlValorMoeda);
        camposResposta.put("sq_solic_empenho", sqSolicEmpenho);
        camposResposta.put("sq_empenho", sqEmpenho);
        camposResposta.put("sq_solicitacao_diaria", sqSolicitacaoDiaria);
        camposResposta.put("sq_ob", sqOB);
        camposResposta.put("sq_previsao_desembolso", sqPrevisaoDesembolso);
        camposResposta.put("tp_documento", tpDocumento);
        camposResposta.put("nu_documento", nuDocumento);
        camposResposta.put("nm_razao_social_pessoa", nmRazaoSocialPessoa);
        camposResposta.put("ds_qualificacao_vinculo", dsQualificacaoVinculo);
        camposResposta.put("destino_viagem_pais_solicitacao_diaria", destinoViagemPaisSolicitacaoDiaria);
        camposResposta.put("destino_viagem_uf_solicitacao_diaria", destinoViagemUFSolicitacaoDiaria);
        camposResposta.put("destino_viagem_municipio_solicitacao_diaria", destinoViagemMunicipioSolicitacaoDiaria);
        camposResposta.put("tp_transporte_viagem_solicitacao_diaria", tpTransporteViagemSolicitacaoDiaria);
        camposResposta.put("tp_viagem_solicitacao_diaria", tpViagemSolicitacaoDiaria);
        camposResposta.put("nm_cargo", nmCargo);
    }

    @Override
    protected void mapearParametros() {
        // Mapear parâmetros de filtro para a API
        if (cdUnidadeGestoraFiltro != null) {
            camposParametros.put("cdUnidadeGestora", cdUnidadeGestoraFiltro);
        }
        if (dtAnoExercicioCTBFiltro != null) {
            camposParametros.put("dtAnoExercicioCTB", dtAnoExercicioCTBFiltro);
        }
    }

    @Override
    public Map<String, Object> getCamposParametrosTodosOsAnos(String ugCd, Short ano) {
        // Para Consulta Gerencial, usar os parâmetros básicos
        camposParametros.clear();
        if (ugCd != null) {
            camposParametros.put("cdUnidadeGestora", ugCd);
        }
        if (ano != null) {
            camposParametros.put("dtAnoExercicioCTB", ano.intValue());
        }
        return camposParametros;
    }

    @Override
    public Map<String, Object> getCamposParametrosAtual(String ugCd, ValidacaoUtil<?> utilsService) {
        // Para Consulta Gerencial, usar ano atual
        camposParametros.clear();
        if (ugCd != null) {
            camposParametros.put("cdUnidadeGestora", ugCd);
        }
        // Usar ano atual (2025)
        camposParametros.put("dtAnoExercicioCTB", 2025);
        return camposParametros;
    }

    // Getters and Setters
    public String getCdUnidadeGestora() {
        return cdUnidadeGestora;
    }

    public void setCdUnidadeGestora(String cdUnidadeGestora) {
        this.cdUnidadeGestora = cdUnidadeGestora;
    }

    public String getSgUnidadeGestora() {
        return sgUnidadeGestora;
    }

    public void setSgUnidadeGestora(String sgUnidadeGestora) {
        this.sgUnidadeGestora = sgUnidadeGestora;
    }

    public Integer getDtAnoExercicioCTB() {
        return dtAnoExercicioCTB;
    }

    public void setDtAnoExercicioCTB(Integer dtAnoExercicioCTB) {
        this.dtAnoExercicioCTB = dtAnoExercicioCTB;
    }

    public String getCdGestao() {
        return cdGestao;
    }

    public void setCdGestao(String cdGestao) {
        this.cdGestao = cdGestao;
    }

    public String getTxMotivoSolicitacao() {
        return txMotivoSolicitacao;
    }

    public void setTxMotivoSolicitacao(String txMotivoSolicitacao) {
        this.txMotivoSolicitacao = txMotivoSolicitacao;
    }

    public String getDtSaidaSolicitacaoDiariaStr() {
        return dtSaidaSolicitacaoDiariaStr;
    }

    public void setDtSaidaSolicitacaoDiariaStr(String dtSaidaSolicitacaoDiariaStr) {
        this.dtSaidaSolicitacaoDiariaStr = dtSaidaSolicitacaoDiariaStr;
        // Converter string para LocalDate se necessário
        if (dtSaidaSolicitacaoDiariaStr != null && !dtSaidaSolicitacaoDiariaStr.isEmpty()) {
            try {
                this.dtSaidaSolicitacaoDiaria = LocalDate.parse(dtSaidaSolicitacaoDiariaStr);
            } catch (Exception e) {
                // Log error but don't fail
                this.dtSaidaSolicitacaoDiaria = null;
            }
        }
    }

    public String getDtRetornoSolicitacaoDiariaStr() {
        return dtRetornoSolicitacaoDiariaStr;
    }

    public void setDtRetornoSolicitacaoDiariaStr(String dtRetornoSolicitacaoDiariaStr) {
        this.dtRetornoSolicitacaoDiariaStr = dtRetornoSolicitacaoDiariaStr;
        // Converter string para LocalDate se necessário
        if (dtRetornoSolicitacaoDiariaStr != null && !dtRetornoSolicitacaoDiariaStr.isEmpty()) {
            try {
                this.dtRetornoSolicitacaoDiaria = LocalDate.parse(dtRetornoSolicitacaoDiariaStr);
            } catch (Exception e) {
                // Log error but don't fail
                this.dtRetornoSolicitacaoDiaria = null;
            }
        }
    }

    public Integer getQtdDiariaSolicitacaoDiaria() {
        return qtdDiariaSolicitacaoDiaria;
    }

    public void setQtdDiariaSolicitacaoDiaria(Integer qtdDiariaSolicitacaoDiaria) {
        this.qtdDiariaSolicitacaoDiaria = qtdDiariaSolicitacaoDiaria;
    }

    public String getVlTotalSolicitacaoDiariaStr() {
        return vlTotalSolicitacaoDiariaStr;
    }

    public void setVlTotalSolicitacaoDiariaStr(String vlTotalSolicitacaoDiariaStr) {
        this.vlTotalSolicitacaoDiariaStr = vlTotalSolicitacaoDiariaStr;
        // Converter string para BigDecimal se necessário
        if (vlTotalSolicitacaoDiariaStr != null && !vlTotalSolicitacaoDiariaStr.isEmpty()) {
            try {
                this.vlTotalSolicitacaoDiaria = new BigDecimal(vlTotalSolicitacaoDiariaStr);
            } catch (Exception e) {
                // Log error but don't fail
                this.vlTotalSolicitacaoDiaria = BigDecimal.ZERO;
            }
        }
    }

    public String getVlDescontoSolicitacaoDiariaStr() {
        return vlDescontoSolicitacaoDiariaStr;
    }

    public void setVlDescontoSolicitacaoDiariaStr(String vlDescontoSolicitacaoDiariaStr) {
        this.vlDescontoSolicitacaoDiariaStr = vlDescontoSolicitacaoDiariaStr;
        // Converter string para BigDecimal se necessário
        if (vlDescontoSolicitacaoDiariaStr != null && !vlDescontoSolicitacaoDiariaStr.isEmpty()) {
            try {
                this.vlDescontoSolicitacaoDiaria = new BigDecimal(vlDescontoSolicitacaoDiariaStr);
            } catch (Exception e) {
                // Log error but don't fail
                this.vlDescontoSolicitacaoDiaria = BigDecimal.ZERO;
            }
        }
    }

    public String getVlValorMoedaStr() {
        return vlValorMoedaStr;
    }

    public void setVlValorMoedaStr(String vlValorMoedaStr) {
        this.vlValorMoedaStr = vlValorMoedaStr;
        // Converter string para BigDecimal se necessário
        if (vlValorMoedaStr != null && !vlValorMoedaStr.isEmpty()) {
            try {
                this.vlValorMoeda = new BigDecimal(vlValorMoedaStr);
            } catch (Exception e) {
                // Log error but don't fail
                this.vlValorMoeda = BigDecimal.ZERO;
            }
        }
    }

    public Long getSqSolicEmpenho() {
        return sqSolicEmpenho;
    }

    public void setSqSolicEmpenho(Long sqSolicEmpenho) {
        this.sqSolicEmpenho = sqSolicEmpenho;
    }

    public Long getSqEmpenho() {
        return sqEmpenho;
    }

    public void setSqEmpenho(Long sqEmpenho) {
        this.sqEmpenho = sqEmpenho;
    }

    public Long getSqSolicitacaoDiaria() {
        return sqSolicitacaoDiaria;
    }

    public void setSqSolicitacaoDiaria(Long sqSolicitacaoDiaria) {
        this.sqSolicitacaoDiaria = sqSolicitacaoDiaria;
    }

    public Long getSqOB() {
        return sqOB;
    }

    public void setSqOB(Long sqOB) {
        this.sqOB = sqOB;
    }

    public Long getSqPrevisaoDesembolso() {
        return sqPrevisaoDesembolso;
    }

    public void setSqPrevisaoDesembolso(Long sqPrevisaoDesembolso) {
        this.sqPrevisaoDesembolso = sqPrevisaoDesembolso;
    }

    public Integer getTpDocumento() {
        return tpDocumento;
    }

    public void setTpDocumento(Integer tpDocumento) {
        this.tpDocumento = tpDocumento;
    }

    public String getNuDocumento() {
        return nuDocumento;
    }

    public void setNuDocumento(String nuDocumento) {
        this.nuDocumento = nuDocumento;
    }

    public String getNmRazaoSocialPessoa() {
        return nmRazaoSocialPessoa;
    }

    public void setNmRazaoSocialPessoa(String nmRazaoSocialPessoa) {
        this.nmRazaoSocialPessoa = nmRazaoSocialPessoa;
    }

    public String getDsQualificacaoVinculo() {
        return dsQualificacaoVinculo;
    }

    public void setDsQualificacaoVinculo(String dsQualificacaoVinculo) {
        this.dsQualificacaoVinculo = dsQualificacaoVinculo;
    }

    public String getDestinoViagemPaisSolicitacaoDiaria() {
        return destinoViagemPaisSolicitacaoDiaria;
    }

    public void setDestinoViagemPaisSolicitacaoDiaria(String destinoViagemPaisSolicitacaoDiaria) {
        this.destinoViagemPaisSolicitacaoDiaria = destinoViagemPaisSolicitacaoDiaria;
    }

    public String getDestinoViagemUFSolicitacaoDiaria() {
        return destinoViagemUFSolicitacaoDiaria;
    }

    public void setDestinoViagemUFSolicitacaoDiaria(String destinoViagemUFSolicitacaoDiaria) {
        this.destinoViagemUFSolicitacaoDiaria = destinoViagemUFSolicitacaoDiaria;
    }

    public String getDestinoViagemMunicipioSolicitacaoDiaria() {
        return destinoViagemMunicipioSolicitacaoDiaria;
    }

    public void setDestinoViagemMunicipioSolicitacaoDiaria(String destinoViagemMunicipioSolicitacaoDiaria) {
        this.destinoViagemMunicipioSolicitacaoDiaria = destinoViagemMunicipioSolicitacaoDiaria;
    }

    public String getTpTransporteViagemSolicitacaoDiaria() {
        return tpTransporteViagemSolicitacaoDiaria;
    }

    public void setTpTransporteViagemSolicitacaoDiaria(String tpTransporteViagemSolicitacaoDiaria) {
        this.tpTransporteViagemSolicitacaoDiaria = tpTransporteViagemSolicitacaoDiaria;
    }

    public String getTpViagemSolicitacaoDiaria() {
        return tpViagemSolicitacaoDiaria;
    }

    public void setTpViagemSolicitacaoDiaria(String tpViagemSolicitacaoDiaria) {
        this.tpViagemSolicitacaoDiaria = tpViagemSolicitacaoDiaria;
    }

    public String getNmCargo() {
        return nmCargo;
    }

    public void setNmCargo(String nmCargo) {
        this.nmCargo = nmCargo;
    }

    // Getters para campos convertidos
    public LocalDate getDtSaidaSolicitacaoDiaria() {
        return dtSaidaSolicitacaoDiaria;
    }

    public void setDtSaidaSolicitacaoDiaria(LocalDate dtSaidaSolicitacaoDiaria) {
        this.dtSaidaSolicitacaoDiaria = dtSaidaSolicitacaoDiaria;
    }

    public LocalDate getDtRetornoSolicitacaoDiaria() {
        return dtRetornoSolicitacaoDiaria;
    }

    public void setDtRetornoSolicitacaoDiaria(LocalDate dtRetornoSolicitacaoDiaria) {
        this.dtRetornoSolicitacaoDiaria = dtRetornoSolicitacaoDiaria;
    }

    public BigDecimal getVlTotalSolicitacaoDiaria() {
        return vlTotalSolicitacaoDiaria;
    }

    public void setVlTotalSolicitacaoDiaria(BigDecimal vlTotalSolicitacaoDiaria) {
        this.vlTotalSolicitacaoDiaria = vlTotalSolicitacaoDiaria;
    }

    public BigDecimal getVlDescontoSolicitacaoDiaria() {
        return vlDescontoSolicitacaoDiaria;
    }

    public void setVlDescontoSolicitacaoDiaria(BigDecimal vlDescontoSolicitacaoDiaria) {
        this.vlDescontoSolicitacaoDiaria = vlDescontoSolicitacaoDiaria;
    }

    public BigDecimal getVlValorMoeda() {
        return vlValorMoeda;
    }

    public void setVlValorMoeda(BigDecimal vlValorMoeda) {
        this.vlValorMoeda = vlValorMoeda;
    }

    // Getters e Setters para campos de filtro
    public String getCdUnidadeGestoraFiltro() {
        return cdUnidadeGestoraFiltro;
    }

    public void setCdUnidadeGestoraFiltro(String cdUnidadeGestoraFiltro) {
        this.cdUnidadeGestoraFiltro = cdUnidadeGestoraFiltro;
    }

    public Integer getDtAnoExercicioCTBFiltro() {
        return dtAnoExercicioCTBFiltro;
    }

    public void setDtAnoExercicioCTBFiltro(Integer dtAnoExercicioCTBFiltro) {
        this.dtAnoExercicioCTBFiltro = dtAnoExercicioCTBFiltro;
    }
}
