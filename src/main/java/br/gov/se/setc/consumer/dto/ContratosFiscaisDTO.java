package br.gov.se.setc.consumer.dto;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import br.gov.se.setc.consumer.contracts.EndpontSefaz;
import br.gov.se.setc.util.ValidacaoUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
public class ContratosFiscaisDTO extends EndpontSefaz  {
    @JsonProperty("dtFimVigenciaContrato")
    private LocalDate dtFimVigenciaContrato;
    @JsonProperty("sgUnidadeGestora")
    private String sgUnidadeGestora;
    @JsonProperty("cdLicitacao")
    private String cdLicitacao;
    @JsonProperty("cdUnidadeGestora")
    private String cdUnidadeGestora;
    @JsonProperty("nmFiscal")
    private String nmFiscal;
    @JsonProperty("nuDocumentoContratado")
    private String nuDocumentoContratado;
    @JsonProperty("nmContratado")
    private String nmContratado;
    @JsonProperty("dtAnoExercicio")
    private Integer dtAnoExercicio;
    @JsonProperty("cdContrato")
    private String cdContrato;
    @JsonProperty("dtInicioVigenciaContrato")
    private LocalDate dtInicioVigenciaContrato;
    @JsonProperty("dsQualificador")
    private String dsQualificador;
    public ContratosFiscaisDTO() {
        inicializarDadosEndpoint();
    }
    public ContratosFiscaisDTO(String dsQualificador, LocalDate dtFimVigenciaContrato, String sgUnidadeGestora, String cdLicitacao, String cdUnidadeGestora, String nmFiscal, String nuDocumentoContratado, String nmContratado, Integer dtAnoExercicio, String cdContrato, LocalDate dtInicioVigenciaContrato) {
        this.dtFimVigenciaContrato = dtFimVigenciaContrato;
        this.sgUnidadeGestora = sgUnidadeGestora;
        this.cdLicitacao = cdLicitacao;
        this.cdUnidadeGestora = cdUnidadeGestora;
        this.nmFiscal = nmFiscal;
        this.nuDocumentoContratado = nuDocumentoContratado;
        this.nmContratado = nmContratado;
        this.dtAnoExercicio = dtAnoExercicio;
        this.cdContrato = cdContrato;
        this.dtInicioVigenciaContrato = dtInicioVigenciaContrato;
        this.dsQualificador = dsQualificador;
        inicializarDadosEndpoint();
        mapearParametros();
        mapearCamposResposta();
    }
    @Override
    protected void inicializarDadosEndpoint() {
        tabelaBanco = "consumer_sefaz.contratos_fiscais";
        nomeDataInicialPadraoFiltro = "dt_inicio_vigencia_contrato";
        nomeDataFinalPadraoFiltro = "dt_fim_vigencia_contrato";
        dtAnoPadrao = "dt_ano_exercicio";
        url = "https://api-transparencia.apps.sefaz.se.gov.br/gbp/v1/contrato-fiscais";
    }
    @Override
    public void mapearCamposResposta() {
        camposResposta.put("dt_fim_vigencia_contrato", dtFimVigenciaContrato);
        camposResposta.put("sg_unidade_gestora", sgUnidadeGestora);
        camposResposta.put("cd_licitacao", cdLicitacao);
        camposResposta.put("cd_unidade_gestora", cdUnidadeGestora);
        camposResposta.put("nm_fiscal", nmFiscal);
        camposResposta.put("nu_documento_contratado", nuDocumentoContratado);
        camposResposta.put("nm_contratado", nmContratado);
        camposResposta.put("dt_ano_exercicio", dtAnoExercicio);
        camposResposta.put("cd_contrato", cdContrato);
        camposResposta.put("dt_inicio_vigencia_contrato", dtInicioVigenciaContrato);
        camposResposta.put("ds_qualificador", dsQualificador);
    }
    @Override
    protected void mapearParametros(){
        camposParametros.put("cdUnidadeGestora",cdUnidadeGestora  );
        camposParametros.put("dtAnoExercicio", dtAnoExercicio);
        camposParametros.put("cdContrato", cdContrato);
        camposParametros.put("nuDocumentoContratado", nuDocumentoContratado);
    }
    public void setCamposParamentrosObrigatorios(String cdUnidadeGestora,Short dtAnoExercicio ) {
        camposParametros.put("cdUnidadeGestora",cdUnidadeGestora  );
        camposParametros.put("dtAnoExercicio", dtAnoExercicio);
    }
    @Override
    public Map<String, Object> getCamposParametrosAtual(String cdUnidadeGestora, ValidacaoUtil<?> utilsService) {
        Map<String, Object> camposParametros = new LinkedHashMap<>();
        camposParametros.put("cdUnidadeGestora", cdUnidadeGestora);
        camposParametros.put("dtAnoExercicio", utilsService.getAnoAtual());
        camposParametros.put("nuMes", utilsService.getMesAtual());
        camposParametros.put("cdQualificador", 133);
        return camposParametros;
    }
    @Override
    public  Map<String, Object> getCamposParametrosTodosOsAnos(String cdUnidadeGestora,Short ano ) {
        Map<String, Object> camposParametros = new LinkedHashMap<>();
        camposParametros.put("cdUnidadeGestora", cdUnidadeGestora);
        camposParametros.put("dtAnoExercicio", ano);
        camposParametros.put("cdQualificador", 133);
        return camposParametros;
    }
    public void setCamposParametros(Map<String, Object> camposParametros) {
        this.camposParametros = camposParametros;
    }
    public Map<String, Object> getCamposResposta() {
        return camposResposta;
    }
    public void setCamposResposta(Map<String, Object> camposResposta) {
        this.camposResposta = camposResposta;
    }
    public LocalDate getDtFimVigenciaContrato() {
        return dtFimVigenciaContrato;
    }
    public void setDtFimVigenciaContrato(LocalDate dtFimVigenciaContrato) {
        this.dtFimVigenciaContrato = dtFimVigenciaContrato;
    }
    public String getSgUnidadeGestora() {
        return sgUnidadeGestora;
    }
    public void setSgUnidadeGestora(String sgUnidadeGestora) {
        this.sgUnidadeGestora = sgUnidadeGestora;
    }
    public String getCdLicitacao() {
        return cdLicitacao;
    }
    public void setCdLicitacao(String cdLicitacao) {
        this.cdLicitacao = cdLicitacao;
    }
    public String getCdUnidadeGestora() {
        return cdUnidadeGestora;
    }
    public void setCdUnidadeGestora(String cdUnidadeGestora) {
        this.cdUnidadeGestora = cdUnidadeGestora;
    }
    public String getNmFiscal() {
        return nmFiscal;
    }
    public void setNmFiscal(String nmFiscal) {
        this.nmFiscal = nmFiscal;
    }
    public String getNuDocumentoContratado() {
        return nuDocumentoContratado;
    }
    public void setNuDocumentoContratado(String nuDocumentoContratado) {
        this.nuDocumentoContratado = nuDocumentoContratado;
    }
    public String getNmContratado() {
        return nmContratado;
    }
    public void setNmContratado(String nmContratado) {
        this.nmContratado = nmContratado;
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
    public LocalDate getDtInicioVigenciaContrato() {
        return dtInicioVigenciaContrato;
    }
    public void setDtInicioVigenciaContrato(LocalDate dtInicioVigenciaContrato) {
        this.dtInicioVigenciaContrato = dtInicioVigenciaContrato;
    }
    public String getDsQualificador() {
        return dsQualificador;
    }
    public void setDsQualificador(String dsQualificador) {
        this.dsQualificador = dsQualificador;
    }
}