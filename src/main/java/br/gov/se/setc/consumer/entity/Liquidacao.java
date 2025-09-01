package br.gov.se.setc.consumer.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
@Entity
@Table(name = "liquidacao", schema = "consumer_sefaz")
public class Liquidacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "sq_empenho")
    private Long sqEmpenho;
    @Column(name = "sq_liquidacao")
    private Long sqLiquidacao;
    @Column(name = "dt_liquidacao")
    private LocalDate dtLiquidacao;
    @Column(name = "cd_unidade_gestora")
    private String cdUnidadeGestora;
    @Column(name = "sg_unidade_gestora")
    private String sgUnidadeGestora;
    @Column(name = "id_orgao")
    private String idOrgao;
    @Column(name = "sg_orgao")
    private String sgOrgao;
    @Column(name = "id_orgao_supervisor")
    private String idOrgaoSupervisor;
    @Column(name = "sg_orgao_supervisor")
    private String sgOrgaoSupervisor;
    @Column(name = "vl_bruto_liquidacao", precision = 15, scale = 2)
    private BigDecimal vlBrutoLiquidacao;
    @Column(name = "nu_documento")
    private String nuDocumento;
    @Column(name = "tp_documento")
    private Integer tpDocumento;
    @Column(name = "dt_ano_exercicio_ctb")
    private Integer dtAnoExercicioCTB;
    @Column(name = "cd_gestao")
    private String cdGestao;
    @Column(name = "nm_razao_social_pessoa", length = 500)
    private String nmRazaoSocialPessoa;
    @Column(name = "cd_natureza_despesa")
    private String cdNaturezaDespesa;
    @Column(name = "vl_estornado_liquidacao", precision = 15, scale = 2)
    private BigDecimal vlEstornadoLiquidacao;
    @Column(name = "cd_funcao")
    private Integer cdFuncao;
    @Column(name = "nm_funcao", length = 500)
    private String nmFuncao;
    @Column(name = "cd_sub_funcao")
    private Integer cdSubFuncao;
    @Column(name = "nm_sub_funcao", length = 500)
    private String nmSubFuncao;
    @Column(name = "cd_elemento_despesa")
    private Integer cdElementoDespesa;
    @Column(name = "cd_fonte_recurso")
    private Integer cdFonteRecurso;
    @Column(name = "cd_licitacao")
    private Integer cdLicitacao;
    @Column(name = "ds_objeto_licitacao", length = 1000)
    private String dsObjetoLicitacao;
    @Column(name = "nu_processo_licitacao")
    private String nuProcessoLicitacao;
    @Column(name = "nm_modalidade_licitacao", length = 500)
    private String nmModalidadeLicitacao;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getSqEmpenho() {
        return sqEmpenho;
    }
    public void setSqEmpenho(Long sqEmpenho) {
        this.sqEmpenho = sqEmpenho;
    }
    public Long getSqLiquidacao() {
        return sqLiquidacao;
    }
    public void setSqLiquidacao(Long sqLiquidacao) {
        this.sqLiquidacao = sqLiquidacao;
    }
    public LocalDate getDtLiquidacao() {
        return dtLiquidacao;
    }
    public void setDtLiquidacao(LocalDate dtLiquidacao) {
        this.dtLiquidacao = dtLiquidacao;
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
    public String getIdOrgao() {
        return idOrgao;
    }
    public void setIdOrgao(String idOrgao) {
        this.idOrgao = idOrgao;
    }
    public String getSgOrgao() {
        return sgOrgao;
    }
    public void setSgOrgao(String sgOrgao) {
        this.sgOrgao = sgOrgao;
    }
    public String getIdOrgaoSupervisor() {
        return idOrgaoSupervisor;
    }
    public void setIdOrgaoSupervisor(String idOrgaoSupervisor) {
        this.idOrgaoSupervisor = idOrgaoSupervisor;
    }
    public String getSgOrgaoSupervisor() {
        return sgOrgaoSupervisor;
    }
    public void setSgOrgaoSupervisor(String sgOrgaoSupervisor) {
        this.sgOrgaoSupervisor = sgOrgaoSupervisor;
    }
    public BigDecimal getVlBrutoLiquidacao() {
        return vlBrutoLiquidacao;
    }
    public void setVlBrutoLiquidacao(BigDecimal vlBrutoLiquidacao) {
        this.vlBrutoLiquidacao = vlBrutoLiquidacao;
    }
    public String getNuDocumento() {
        return nuDocumento;
    }
    public void setNuDocumento(String nuDocumento) {
        this.nuDocumento = nuDocumento;
    }
    public Integer getTpDocumento() {
        return tpDocumento;
    }
    public void setTpDocumento(Integer tpDocumento) {
        this.tpDocumento = tpDocumento;
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
    public String getNmRazaoSocialPessoa() {
        return nmRazaoSocialPessoa;
    }
    public void setNmRazaoSocialPessoa(String nmRazaoSocialPessoa) {
        this.nmRazaoSocialPessoa = nmRazaoSocialPessoa;
    }
    public String getCdNaturezaDespesa() {
        return cdNaturezaDespesa;
    }
    public void setCdNaturezaDespesa(String cdNaturezaDespesa) {
        this.cdNaturezaDespesa = cdNaturezaDespesa;
    }
    public BigDecimal getVlEstornadoLiquidacao() {
        return vlEstornadoLiquidacao;
    }
    public void setVlEstornadoLiquidacao(BigDecimal vlEstornadoLiquidacao) {
        this.vlEstornadoLiquidacao = vlEstornadoLiquidacao;
    }
    public Integer getCdFuncao() {
        return cdFuncao;
    }
    public void setCdFuncao(Integer cdFuncao) {
        this.cdFuncao = cdFuncao;
    }
    public String getNmFuncao() {
        return nmFuncao;
    }
    public void setNmFuncao(String nmFuncao) {
        this.nmFuncao = nmFuncao;
    }
    public Integer getCdSubFuncao() {
        return cdSubFuncao;
    }
    public void setCdSubFuncao(Integer cdSubFuncao) {
        this.cdSubFuncao = cdSubFuncao;
    }
    public String getNmSubFuncao() {
        return nmSubFuncao;
    }
    public void setNmSubFuncao(String nmSubFuncao) {
        this.nmSubFuncao = nmSubFuncao;
    }
    public Integer getCdElementoDespesa() {
        return cdElementoDespesa;
    }
    public void setCdElementoDespesa(Integer cdElementoDespesa) {
        this.cdElementoDespesa = cdElementoDespesa;
    }
    public Integer getCdFonteRecurso() {
        return cdFonteRecurso;
    }
    public void setCdFonteRecurso(Integer cdFonteRecurso) {
        this.cdFonteRecurso = cdFonteRecurso;
    }
    public Integer getCdLicitacao() {
        return cdLicitacao;
    }
    public void setCdLicitacao(Integer cdLicitacao) {
        this.cdLicitacao = cdLicitacao;
    }
    public String getDsObjetoLicitacao() {
        return dsObjetoLicitacao;
    }
    public void setDsObjetoLicitacao(String dsObjetoLicitacao) {
        this.dsObjetoLicitacao = dsObjetoLicitacao;
    }
    public String getNuProcessoLicitacao() {
        return nuProcessoLicitacao;
    }
    public void setNuProcessoLicitacao(String nuProcessoLicitacao) {
        this.nuProcessoLicitacao = nuProcessoLicitacao;
    }
    public String getNmModalidadeLicitacao() {
        return nmModalidadeLicitacao;
    }
    public void setNmModalidadeLicitacao(String nmModalidadeLicitacao) {
        this.nmModalidadeLicitacao = nmModalidadeLicitacao;
    }
}
