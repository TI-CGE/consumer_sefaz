package br.gov.se.setc.consumer.dto;
import br.gov.se.setc.consumer.contracts.EndpontSefaz;
import br.gov.se.setc.util.ValidacaoUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
/**
 * DTO para consumo da API de Contratos do SEFAZ
 * Endpoint: https://api-transparencia.apps.sefaz.se.gov.br/gbp/v1/contrato
 */
public class ContratoDTO extends EndpontSefaz {
    @JsonProperty("sgUnidadeGestora")
    private String sgUnidadeGestora;
    @JsonProperty("cdUnidadeGestora")
    private String cdUnidadeGestora;
    @JsonProperty("dtAnoExercicio")
    private Integer dtAnoExercicio;
    @JsonProperty("cdContrato")
    private String cdContrato;
    @JsonProperty("cdAditivo")
    private String cdAditivo;
    @JsonProperty("dtInicioVigencia")
    private String dtInicioVigenciaStr;
    @JsonProperty("dtFimVigencia")
    private String dtFimVigenciaStr;
    @JsonProperty("nmCategoria")
    private String nmCategoria;
    @JsonProperty("nmFornecedor")
    private String nmFornecedor;
    @JsonProperty("nuDocumento")
    private String nuDocumento;
    @JsonProperty("dsObjetoContrato")
    private String dsObjetoContrato;
    @JsonProperty("vlContrato")
    private BigDecimal vlContrato;
    @JsonProperty("tpContrato")
    private String tpContrato;
    private LocalDate dtInicioVigencia;
    private LocalDate dtFimVigencia;
    private String cdUnidadeGestoraFiltro;
    private Integer dtAnoExercicioFiltro;
    public ContratoDTO() {
        inicializarDadosEndpoint();
        mapearCamposResposta();
        mapearParametros();
    }
    @Override
    protected void inicializarDadosEndpoint() {
        tabelaBanco = "consumer_sefaz.contrato";
        url = "https://api-transparencia.apps.sefaz.se.gov.br/gbp/v1/contrato";
        nomeDataInicialPadraoFiltro = null;
        nomeDataFinalPadraoFiltro = null;
        dtAnoPadrao = "dt_ano_exercicio";
    }
    @Override
    public void mapearCamposResposta() {
        camposResposta.put("sg_unidade_gestora", sgUnidadeGestora);
        camposResposta.put("cd_unidade_gestora", cdUnidadeGestora);
        camposResposta.put("dt_ano_exercicio", dtAnoExercicio);
        camposResposta.put("cd_contrato", cdContrato);
        camposResposta.put("cd_aditivo", cdAditivo);
        camposResposta.put("dt_inicio_vigencia", dtInicioVigencia);
        camposResposta.put("dt_fim_vigencia", dtFimVigencia);
        camposResposta.put("nm_categoria", nmCategoria);
        camposResposta.put("nm_fornecedor", nmFornecedor);
        camposResposta.put("nu_documento", nuDocumento);
        camposResposta.put("ds_objeto_contrato", dsObjetoContrato);
        camposResposta.put("vl_contrato", vlContrato);
        camposResposta.put("tp_contrato", tpContrato);
    }
    @Override
    protected void mapearParametros() {
        if (cdUnidadeGestoraFiltro != null) {
            camposParametros.put("cdUnidadeGestora", cdUnidadeGestoraFiltro);
        }
        if (dtAnoExercicioFiltro != null) {
            camposParametros.put("dtAnoExercicio", dtAnoExercicioFiltro);
        }
    }
    @Override
    public Map<String, Object> getCamposParametrosTodosOsAnos(String ugCd, Short ano) {
        camposParametros.clear();
        if (ugCd != null) {
            camposParametros.put("cdUnidadeGestora", ugCd);
        }
        if (ano != null) {
            camposParametros.put("dtAnoExercicio", ano.intValue());
        }
        return camposParametros;
    }
    @Override
    public Map<String, Object> getCamposParametrosAtual(String ugCd, ValidacaoUtil<?> utilsService) {
        camposParametros.clear();
        if (ugCd != null) {
            camposParametros.put("cdUnidadeGestora", ugCd);
        }
        camposParametros.put("dtAnoExercicio", 2025);
        return camposParametros;
    }
    public String getSgUnidadeGestora() {
        return sgUnidadeGestora;
    }
    public void setSgUnidadeGestora(String sgUnidadeGestora) {
        this.sgUnidadeGestora = sgUnidadeGestora;
    }
    public String getCdUnidadeGestora() {
        return cdUnidadeGestora;
    }
    public void setCdUnidadeGestora(String cdUnidadeGestora) {
        this.cdUnidadeGestora = cdUnidadeGestora;
    }
    public Integer getDtAnoExercicio() {
        return dtAnoExercicio;
    }
    public void setDtAnoExercicio(Integer dtAnoExercicio) {
        this.dtAnoExercicio = dtAnoExercicio;
    }
    public String getCdContrato() {
        return cdContrato;
    }
    public void setCdContrato(String cdContrato) {
        this.cdContrato = cdContrato;
    }
    public String getCdAditivo() {
        return cdAditivo;
    }
    public void setCdAditivo(String cdAditivo) {
        this.cdAditivo = cdAditivo;
    }
    public String getDtInicioVigenciaStr() {
        return dtInicioVigenciaStr;
    }
    public void setDtInicioVigenciaStr(String dtInicioVigenciaStr) {
        this.dtInicioVigenciaStr = dtInicioVigenciaStr;
        if (dtInicioVigenciaStr != null && !dtInicioVigenciaStr.isEmpty()) {
            try {
                this.dtInicioVigencia = LocalDate.parse(dtInicioVigenciaStr.substring(0, 10));
            } catch (Exception e) {
                this.dtInicioVigencia = null;
            }
        }
    }
    public String getDtFimVigenciaStr() {
        return dtFimVigenciaStr;
    }
    public void setDtFimVigenciaStr(String dtFimVigenciaStr) {
        this.dtFimVigenciaStr = dtFimVigenciaStr;
        if (dtFimVigenciaStr != null && !dtFimVigenciaStr.isEmpty()) {
            try {
                this.dtFimVigencia = LocalDate.parse(dtFimVigenciaStr.substring(0, 10));
            } catch (Exception e) {
                this.dtFimVigencia = null;
            }
        }
    }
    public String getNmCategoria() {
        return nmCategoria;
    }
    public void setNmCategoria(String nmCategoria) {
        this.nmCategoria = nmCategoria;
    }
    public String getNmFornecedor() {
        return nmFornecedor;
    }
    public void setNmFornecedor(String nmFornecedor) {
        this.nmFornecedor = nmFornecedor;
    }
    public String getNuDocumento() {
        return nuDocumento;
    }
    public void setNuDocumento(String nuDocumento) {
        this.nuDocumento = nuDocumento;
    }
    public String getDsObjetoContrato() {
        return dsObjetoContrato;
    }
    public void setDsObjetoContrato(String dsObjetoContrato) {
        this.dsObjetoContrato = dsObjetoContrato;
    }
    public BigDecimal getVlContrato() {
        return vlContrato;
    }
    public void setVlContrato(BigDecimal vlContrato) {
        this.vlContrato = vlContrato;
    }
    public String getTpContrato() {
        return tpContrato;
    }
    public void setTpContrato(String tpContrato) {
        this.tpContrato = tpContrato;
    }
    public LocalDate getDtInicioVigencia() {
        return dtInicioVigencia;
    }
    public void setDtInicioVigencia(LocalDate dtInicioVigencia) {
        this.dtInicioVigencia = dtInicioVigencia;
    }
    public LocalDate getDtFimVigencia() {
        return dtFimVigencia;
    }
    public void setDtFimVigencia(LocalDate dtFimVigencia) {
        this.dtFimVigencia = dtFimVigencia;
    }
    public String getCdUnidadeGestoraFiltro() {
        return cdUnidadeGestoraFiltro;
    }
    public void setCdUnidadeGestoraFiltro(String cdUnidadeGestoraFiltro) {
        this.cdUnidadeGestoraFiltro = cdUnidadeGestoraFiltro;
    }
    public Integer getDtAnoExercicioFiltro() {
        return dtAnoExercicioFiltro;
    }
    public void setDtAnoExercicioFiltro(Integer dtAnoExercicioFiltro) {
        this.dtAnoExercicioFiltro = dtAnoExercicioFiltro;
    }
}