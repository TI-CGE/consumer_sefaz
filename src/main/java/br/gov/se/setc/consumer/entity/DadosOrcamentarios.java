package br.gov.se.setc.consumer.entity;
import jakarta.persistence.*;
@Entity
@Table(name = "dados_orcamentarios", schema = "consumer_sefaz")
public class DadosOrcamentarios {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "cd_funcao_plo")
    private Integer cdFuncaoPLO;
    @Column(name = "nm_funcao_plo", length = 500)
    private String nmFuncaoPLO;
    @Column(name = "cd_sub_funcao")
    private Integer cdSubFuncao;
    @Column(name = "nm_sub_funcao", length = 500)
    private String nmSubFuncao;
    @Column(name = "cd_programa_governo")
    private Integer cdProgramaGoverno;
    @Column(name = "nm_programa_governo", length = 500)
    private String nmProgramaGoverno;
    @Column(name = "cd_categoria_economica")
    private Integer cdCategoriaEconomica;
    @Column(name = "nm_categoria_economica", length = 500)
    private String nmCategoriaEconomica;
    @Column(name = "cd_modalidade_aplicacao")
    private Integer cdModalidadeAplicacao;
    @Column(name = "nm_modalidade_aplicacao", length = 500)
    private String nmModalidadeAplicacao;
    @Column(name = "cd_fonte_recurso", length = 50)
    private String cdFonteRecurso;
    @Column(name = "nm_fonte_recurso", length = 500)
    private String nmFonteRecurso;
    @Column(name = "cd_grupo_despesa")
    private Integer cdGrupoDespesa;
    @Column(name = "nm_grupo_despesa", length = 500)
    private String nmGrupoDespesa;
    @Column(name = "cd_licitacao")
    private String cdLicitacao;
    @Column(name = "ds_objeto_licitacao", length = 1000)
    private String dsObjetoLicitacao;
    @Column(name = "nu_processo_licitacao")
    private String nuProcessoLicitacao;
    @Column(name = "cd_elemento_despesa")
    private Integer cdElementoDespesa;
    @Column(name = "nm_elemento_despesa", length = 500)
    private String nmElementoDespesa;
    @Column(name = "cd_tipo_despesa")
    private Integer cdTipoDespesa;
    @Column(name = "ds_tipo_despesa", length = 500)
    private String dsTipoDespesa;
    public DadosOrcamentarios() {
    }
    public DadosOrcamentarios(Integer cdFuncaoPLO, String nmFuncaoPLO, Integer cdSubFuncao, String nmSubFuncao,
                             Integer cdProgramaGoverno, String nmProgramaGoverno, Integer cdCategoriaEconomica,
                             String nmCategoriaEconomica, Integer cdModalidadeAplicacao, String nmModalidadeAplicacao,
                             String cdFonteRecurso, String nmFonteRecurso, Integer cdGrupoDespesa, String nmGrupoDespesa,
                             String cdLicitacao, String dsObjetoLicitacao, String nuProcessoLicitacao,
                             Integer cdElementoDespesa, String nmElementoDespesa, Integer cdTipoDespesa, String dsTipoDespesa) {
        this.cdFuncaoPLO = cdFuncaoPLO;
        this.nmFuncaoPLO = nmFuncaoPLO;
        this.cdSubFuncao = cdSubFuncao;
        this.nmSubFuncao = nmSubFuncao;
        this.cdProgramaGoverno = cdProgramaGoverno;
        this.nmProgramaGoverno = nmProgramaGoverno;
        this.cdCategoriaEconomica = cdCategoriaEconomica;
        this.nmCategoriaEconomica = nmCategoriaEconomica;
        this.cdModalidadeAplicacao = cdModalidadeAplicacao;
        this.nmModalidadeAplicacao = nmModalidadeAplicacao;
        this.cdFonteRecurso = cdFonteRecurso;
        this.nmFonteRecurso = nmFonteRecurso;
        this.cdGrupoDespesa = cdGrupoDespesa;
        this.nmGrupoDespesa = nmGrupoDespesa;
        this.cdLicitacao = cdLicitacao;
        this.dsObjetoLicitacao = dsObjetoLicitacao;
        this.nuProcessoLicitacao = nuProcessoLicitacao;
        this.cdElementoDespesa = cdElementoDespesa;
        this.nmElementoDespesa = nmElementoDespesa;
        this.cdTipoDespesa = cdTipoDespesa;
        this.dsTipoDespesa = dsTipoDespesa;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Integer getCdFuncaoPLO() {
        return cdFuncaoPLO;
    }
    public void setCdFuncaoPLO(Integer cdFuncaoPLO) {
        this.cdFuncaoPLO = cdFuncaoPLO;
    }
    public String getNmFuncaoPLO() {
        return nmFuncaoPLO;
    }
    public void setNmFuncaoPLO(String nmFuncaoPLO) {
        this.nmFuncaoPLO = nmFuncaoPLO;
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
    public Integer getCdProgramaGoverno() {
        return cdProgramaGoverno;
    }
    public void setCdProgramaGoverno(Integer cdProgramaGoverno) {
        this.cdProgramaGoverno = cdProgramaGoverno;
    }
    public String getNmProgramaGoverno() {
        return nmProgramaGoverno;
    }
    public void setNmProgramaGoverno(String nmProgramaGoverno) {
        this.nmProgramaGoverno = nmProgramaGoverno;
    }
    public Integer getCdCategoriaEconomica() {
        return cdCategoriaEconomica;
    }
    public void setCdCategoriaEconomica(Integer cdCategoriaEconomica) {
        this.cdCategoriaEconomica = cdCategoriaEconomica;
    }
    public String getNmCategoriaEconomica() {
        return nmCategoriaEconomica;
    }
    public void setNmCategoriaEconomica(String nmCategoriaEconomica) {
        this.nmCategoriaEconomica = nmCategoriaEconomica;
    }
    public Integer getCdModalidadeAplicacao() {
        return cdModalidadeAplicacao;
    }
    public void setCdModalidadeAplicacao(Integer cdModalidadeAplicacao) {
        this.cdModalidadeAplicacao = cdModalidadeAplicacao;
    }
    public String getNmModalidadeAplicacao() {
        return nmModalidadeAplicacao;
    }
    public void setNmModalidadeAplicacao(String nmModalidadeAplicacao) {
        this.nmModalidadeAplicacao = nmModalidadeAplicacao;
    }
    public String getCdFonteRecurso() {
        return cdFonteRecurso;
    }
    public void setCdFonteRecurso(String cdFonteRecurso) {
        this.cdFonteRecurso = cdFonteRecurso;
    }
    public String getNmFonteRecurso() {
        return nmFonteRecurso;
    }
    public void setNmFonteRecurso(String nmFonteRecurso) {
        this.nmFonteRecurso = nmFonteRecurso;
    }
    public Integer getCdGrupoDespesa() {
        return cdGrupoDespesa;
    }
    public void setCdGrupoDespesa(Integer cdGrupoDespesa) {
        this.cdGrupoDespesa = cdGrupoDespesa;
    }
    public String getNmGrupoDespesa() {
        return nmGrupoDespesa;
    }
    public void setNmGrupoDespesa(String nmGrupoDespesa) {
        this.nmGrupoDespesa = nmGrupoDespesa;
    }
    public String getCdLicitacao() {
        return cdLicitacao;
    }
    public void setCdLicitacao(String cdLicitacao) {
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
    public Integer getCdElementoDespesa() {
        return cdElementoDespesa;
    }
    public void setCdElementoDespesa(Integer cdElementoDespesa) {
        this.cdElementoDespesa = cdElementoDespesa;
    }
    public String getNmElementoDespesa() {
        return nmElementoDespesa;
    }
    public void setNmElementoDespesa(String nmElementoDespesa) {
        this.nmElementoDespesa = nmElementoDespesa;
    }
    public Integer getCdTipoDespesa() {
        return cdTipoDespesa;
    }
    public void setCdTipoDespesa(Integer cdTipoDespesa) {
        this.cdTipoDespesa = cdTipoDespesa;
    }
    public String getDsTipoDespesa() {
        return dsTipoDespesa;
    }
    public void setDsTipoDespesa(String dsTipoDespesa) {
        this.dsTipoDespesa = dsTipoDespesa;
    }
    @Override
    public String toString() {
        return "DadosOrcamentarios{" +
                "id=" + id +
                ", cdFuncaoPLO=" + cdFuncaoPLO +
                ", nmFuncaoPLO='" + nmFuncaoPLO + '\'' +
                ", cdSubFuncao=" + cdSubFuncao +
                ", nmSubFuncao='" + nmSubFuncao + '\'' +
                ", cdProgramaGoverno=" + cdProgramaGoverno +
                ", nmProgramaGoverno='" + nmProgramaGoverno + '\'' +
                ", cdCategoriaEconomica=" + cdCategoriaEconomica +
                ", nmCategoriaEconomica='" + nmCategoriaEconomica + '\'' +
                ", cdModalidadeAplicacao=" + cdModalidadeAplicacao +
                ", nmModalidadeAplicacao='" + nmModalidadeAplicacao + '\'' +
                ", cdFonteRecurso=" + cdFonteRecurso +
                ", nmFonteRecurso='" + nmFonteRecurso + '\'' +
                ", cdGrupoDespesa=" + cdGrupoDespesa +
                ", nmGrupoDespesa='" + nmGrupoDespesa + '\'' +
                ", cdLicitacao='" + cdLicitacao + '\'' +
                ", dsObjetoLicitacao='" + dsObjetoLicitacao + '\'' +
                ", nuProcessoLicitacao='" + nuProcessoLicitacao + '\'' +
                ", cdElementoDespesa=" + cdElementoDespesa +
                ", nmElementoDespesa='" + nmElementoDespesa + '\'' +
                ", cdTipoDespesa=" + cdTipoDespesa +
                ", dsTipoDespesa='" + dsTipoDespesa + '\'' +
                '}';
    }
}