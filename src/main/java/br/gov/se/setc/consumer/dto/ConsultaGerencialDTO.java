package br.gov.se.setc.consumer.dto;
import br.gov.se.setc.consumer.contracts.EndpontSefaz;
import br.gov.se.setc.util.ValidacaoUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;
/**
 * DTO para consumo da API de Consulta Gerencial (Diárias) do SEFAZ
 * Endpoint: https://api-transparencia.apps.sefaz.se.gov.br/gfu/v1/diarias/consulta-gerencial
 */
public class ConsultaGerencialDTO extends EndpontSefaz {
    private static final Logger logger = Logger.getLogger(ConsultaGerencialDTO.class.getName());
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
    @JsonProperty("vlTotalValorPagoAtualizado")
    private String vlTotalValorPagoAtualizadoStr;
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
    private LocalDate dtSaidaSolicitacaoDiaria;
    private LocalDate dtRetornoSolicitacaoDiaria;
    private BigDecimal vlTotalSolicitacaoDiaria;
    private BigDecimal vlDescontoSolicitacaoDiaria;
    private BigDecimal vlValorMoeda;
    private BigDecimal vlTotalValorPagoAtualizado;
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
        camposResposta.put("vl_total_valor_pago_atualizado", vlTotalValorPagoAtualizado);
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
        if (cdUnidadeGestoraFiltro != null) {
            camposParametros.put("cdUnidadeGestora", cdUnidadeGestoraFiltro);
        }
        if (dtAnoExercicioCTBFiltro != null) {
            camposParametros.put("dtAnoExercicioCTB", dtAnoExercicioCTBFiltro);
        }
    }
    @Override
    public Map<String, Object> getCamposParametrosTodosOsAnos(String ugCd, Short ano) {
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
        camposParametros.clear();
        if (ugCd != null) {
            camposParametros.put("cdUnidadeGestora", ugCd);
        }
        camposParametros.put("dtAnoExercicioCTB", 2025);
        return camposParametros;
    }
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
        if (dtSaidaSolicitacaoDiariaStr != null && !dtSaidaSolicitacaoDiariaStr.isEmpty()) {
            try {
                this.dtSaidaSolicitacaoDiaria = LocalDate.parse(dtSaidaSolicitacaoDiariaStr);
                logger.fine("Data de saída convertida com sucesso: " + dtSaidaSolicitacaoDiariaStr + " -> " + this.dtSaidaSolicitacaoDiaria);
            } catch (Exception e) {
                logger.warning("Erro ao converter data de saída '" + dtSaidaSolicitacaoDiariaStr + "': " + e.getMessage());
                this.dtSaidaSolicitacaoDiaria = null;
            }
        } else {
            logger.fine("Data de saída é null ou vazia: " + dtSaidaSolicitacaoDiariaStr);
            this.dtSaidaSolicitacaoDiaria = null;
        }
    }
    public String getDtRetornoSolicitacaoDiariaStr() {
        return dtRetornoSolicitacaoDiariaStr;
    }
    public void setDtRetornoSolicitacaoDiariaStr(String dtRetornoSolicitacaoDiariaStr) {
        this.dtRetornoSolicitacaoDiariaStr = dtRetornoSolicitacaoDiariaStr;
        if (dtRetornoSolicitacaoDiariaStr != null && !dtRetornoSolicitacaoDiariaStr.isEmpty()) {
            try {
                this.dtRetornoSolicitacaoDiaria = LocalDate.parse(dtRetornoSolicitacaoDiariaStr);
                logger.fine("Data de retorno convertida com sucesso: " + dtRetornoSolicitacaoDiariaStr + " -> " + this.dtRetornoSolicitacaoDiaria);
            } catch (Exception e) {
                logger.warning("Erro ao converter data de retorno '" + dtRetornoSolicitacaoDiariaStr + "': " + e.getMessage());
                this.dtRetornoSolicitacaoDiaria = null;
            }
        } else {
            logger.fine("Data de retorno é null ou vazia: " + dtRetornoSolicitacaoDiariaStr);
            this.dtRetornoSolicitacaoDiaria = null;
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
        if (vlTotalSolicitacaoDiariaStr != null && !vlTotalSolicitacaoDiariaStr.isEmpty()) {
            try {
                this.vlTotalSolicitacaoDiaria = new BigDecimal(vlTotalSolicitacaoDiariaStr);
                logger.fine("Valor total solicitação convertido com sucesso: " + vlTotalSolicitacaoDiariaStr + " -> " + this.vlTotalSolicitacaoDiaria);
            } catch (Exception e) {
                logger.warning("Erro ao converter valor total solicitação '" + vlTotalSolicitacaoDiariaStr + "': " + e.getMessage());
                this.vlTotalSolicitacaoDiaria = BigDecimal.ZERO;
            }
        } else {
            this.vlTotalSolicitacaoDiaria = BigDecimal.ZERO;
        }
    }
    public String getVlDescontoSolicitacaoDiariaStr() {
        return vlDescontoSolicitacaoDiariaStr;
    }
    public void setVlDescontoSolicitacaoDiariaStr(String vlDescontoSolicitacaoDiariaStr) {
        this.vlDescontoSolicitacaoDiariaStr = vlDescontoSolicitacaoDiariaStr;
        if (vlDescontoSolicitacaoDiariaStr != null && !vlDescontoSolicitacaoDiariaStr.isEmpty()) {
            try {
                this.vlDescontoSolicitacaoDiaria = new BigDecimal(vlDescontoSolicitacaoDiariaStr);
                logger.fine("Valor desconto convertido com sucesso: " + vlDescontoSolicitacaoDiariaStr + " -> " + this.vlDescontoSolicitacaoDiaria);
            } catch (Exception e) {
                logger.warning("Erro ao converter valor desconto '" + vlDescontoSolicitacaoDiariaStr + "': " + e.getMessage());
                this.vlDescontoSolicitacaoDiaria = BigDecimal.ZERO;
            }
        } else {
            this.vlDescontoSolicitacaoDiaria = BigDecimal.ZERO;
        }
    }
    public String getVlValorMoedaStr() {
        return vlValorMoedaStr;
    }
    public void setVlValorMoedaStr(String vlValorMoedaStr) {
        this.vlValorMoedaStr = vlValorMoedaStr;
        if (vlValorMoedaStr != null && !vlValorMoedaStr.isEmpty()) {
            try {
                this.vlValorMoeda = new BigDecimal(vlValorMoedaStr);
                logger.fine("Valor moeda convertido com sucesso: " + vlValorMoedaStr + " -> " + this.vlValorMoeda);
            } catch (Exception e) {
                logger.warning("Erro ao converter valor moeda '" + vlValorMoedaStr + "': " + e.getMessage());
                this.vlValorMoeda = BigDecimal.ZERO;
            }
        } else {
            this.vlValorMoeda = BigDecimal.ZERO;
        }
    }
    public String getVlTotalValorPagoAtualizadoStr() {
        return vlTotalValorPagoAtualizadoStr;
    }
    public void setVlTotalValorPagoAtualizadoStr(String vlTotalValorPagoAtualizadoStr) {
        this.vlTotalValorPagoAtualizadoStr = vlTotalValorPagoAtualizadoStr;
        if (vlTotalValorPagoAtualizadoStr != null && !vlTotalValorPagoAtualizadoStr.isEmpty()) {
            try {
                this.vlTotalValorPagoAtualizado = new BigDecimal(vlTotalValorPagoAtualizadoStr);
                logger.fine("Valor pago atualizado convertido com sucesso: " + vlTotalValorPagoAtualizadoStr + " -> " + this.vlTotalValorPagoAtualizado);
            } catch (Exception e) {
                logger.warning("Erro ao converter valor pago atualizado '" + vlTotalValorPagoAtualizadoStr + "': " + e.getMessage());
                this.vlTotalValorPagoAtualizado = BigDecimal.ZERO;
            }
        } else {
            this.vlTotalValorPagoAtualizado = BigDecimal.ZERO;
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
    @JsonIgnore
    public LocalDate getDtSaidaSolicitacaoDiaria() {
        return dtSaidaSolicitacaoDiaria;
    }
    public void setDtSaidaSolicitacaoDiaria(LocalDate dtSaidaSolicitacaoDiaria) {
        this.dtSaidaSolicitacaoDiaria = dtSaidaSolicitacaoDiaria;
    }
    @JsonIgnore
    public LocalDate getDtRetornoSolicitacaoDiaria() {
        return dtRetornoSolicitacaoDiaria;
    }
    public void setDtRetornoSolicitacaoDiaria(LocalDate dtRetornoSolicitacaoDiaria) {
        this.dtRetornoSolicitacaoDiaria = dtRetornoSolicitacaoDiaria;
    }
    @JsonIgnore
    public BigDecimal getVlTotalSolicitacaoDiaria() {
        return vlTotalSolicitacaoDiaria;
    }
    public void setVlTotalSolicitacaoDiaria(BigDecimal vlTotalSolicitacaoDiaria) {
        this.vlTotalSolicitacaoDiaria = vlTotalSolicitacaoDiaria;
    }
    @JsonIgnore
    public BigDecimal getVlDescontoSolicitacaoDiaria() {
        return vlDescontoSolicitacaoDiaria;
    }
    public void setVlDescontoSolicitacaoDiaria(BigDecimal vlDescontoSolicitacaoDiaria) {
        this.vlDescontoSolicitacaoDiaria = vlDescontoSolicitacaoDiaria;
    }
    @JsonIgnore
    public BigDecimal getVlValorMoeda() {
        return vlValorMoeda;
    }
    public void setVlValorMoeda(BigDecimal vlValorMoeda) {
        this.vlValorMoeda = vlValorMoeda;
    }
    @JsonIgnore
    public BigDecimal getVlTotalValorPagoAtualizado() {
        return vlTotalValorPagoAtualizado;
    }
    public void setVlTotalValorPagoAtualizado(BigDecimal vlTotalValorPagoAtualizado) {
        this.vlTotalValorPagoAtualizado = vlTotalValorPagoAtualizado;
    }
    public void setDtSaidaSolicitacaoDiaria(String dtSaidaSolicitacaoDiaria) {
        setDtSaidaSolicitacaoDiariaStr(dtSaidaSolicitacaoDiaria);
    }
    public void setDtRetornoSolicitacaoDiaria(String dtRetornoSolicitacaoDiaria) {
        setDtRetornoSolicitacaoDiariaStr(dtRetornoSolicitacaoDiaria);
    }
    public void setVlTotalSolicitacaoDiaria(String vlTotalSolicitacaoDiaria) {
        setVlTotalSolicitacaoDiariaStr(vlTotalSolicitacaoDiaria);
    }
    public void setVlTotalSolicitacaoDiaria(Integer vlTotalSolicitacaoDiaria) {
        if (vlTotalSolicitacaoDiaria != null) {
            setVlTotalSolicitacaoDiariaStr(vlTotalSolicitacaoDiaria.toString());
        }
    }
    public void setVlTotalValorPagoAtualizado(String vlTotalValorPagoAtualizado) {
        setVlTotalValorPagoAtualizadoStr(vlTotalValorPagoAtualizado);
    }
    public void setVlTotalValorPagoAtualizado(Integer vlTotalValorPagoAtualizado) {
        if (vlTotalValorPagoAtualizado != null) {
            setVlTotalValorPagoAtualizadoStr(vlTotalValorPagoAtualizado.toString());
        }
    }
    public void setVlDescontoSolicitacaoDiaria(String vlDescontoSolicitacaoDiaria) {
        setVlDescontoSolicitacaoDiariaStr(vlDescontoSolicitacaoDiaria);
    }
    public void setVlDescontoSolicitacaoDiaria(Integer vlDescontoSolicitacaoDiaria) {
        if (vlDescontoSolicitacaoDiaria != null) {
            setVlDescontoSolicitacaoDiariaStr(vlDescontoSolicitacaoDiaria.toString());
        }
    }
    public void setVlValorMoeda(String vlValorMoeda) {
        setVlValorMoedaStr(vlValorMoeda);
    }
    public void setVlValorMoeda(Integer vlValorMoeda) {
        if (vlValorMoeda != null) {
            setVlValorMoedaStr(vlValorMoeda.toString());
        }
    }
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
