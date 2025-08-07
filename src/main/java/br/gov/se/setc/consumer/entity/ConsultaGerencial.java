package br.gov.se.setc.consumer.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "consulta_gerencial", schema = "consumer_sefaz")
public class ConsultaGerencial {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "cd_unidade_gestora")
    private String cdUnidadeGestora;
    
    @Column(name = "sg_unidade_gestora")
    private String sgUnidadeGestora;
    
    @Column(name = "dt_ano_exercicio_ctb")
    private Integer dtAnoExercicioCTB;
    
    @Column(name = "cd_gestao")
    private String cdGestao;
    
    @Column(name = "tx_motivo_solicitacao", columnDefinition = "TEXT")
    private String txMotivoSolicitacao;
    
    @Column(name = "dt_saida_solicitacao_diaria")
    private LocalDate dtSaidaSolicitacaoDiaria;
    
    @Column(name = "dt_retorno_solicitacao_diaria")
    private LocalDate dtRetornoSolicitacaoDiaria;
    
    @Column(name = "qtd_diaria_solicitacao_diaria")
    private Integer qtdDiariaSolicitacaoDiaria;
    
    @Column(name = "vl_total_solicitacao_diaria", precision = 18, scale = 2)
    private BigDecimal vlTotalSolicitacaoDiaria;
    
    @Column(name = "vl_desconto_solicitacao_diaria", precision = 18, scale = 2)
    private BigDecimal vlDescontoSolicitacaoDiaria;
    
    @Column(name = "vl_valor_moeda", precision = 18, scale = 2)
    private BigDecimal vlValorMoeda;
    
    @Column(name = "sq_solic_empenho")
    private Long sqSolicEmpenho;
    
    @Column(name = "sq_empenho")
    private Long sqEmpenho;
    
    @Column(name = "sq_solicitacao_diaria")
    private Long sqSolicitacaoDiaria;
    
    @Column(name = "sq_ob")
    private Long sqOB;
    
    @Column(name = "sq_previsao_desembolso")
    private Long sqPrevisaoDesembolso;
    
    @Column(name = "tp_documento")
    private Integer tpDocumento;
    
    @Column(name = "nu_documento")
    private String nuDocumento;
    
    @Column(name = "nm_razao_social_pessoa")
    private String nmRazaoSocialPessoa;
    
    @Column(name = "ds_qualificacao_vinculo")
    private String dsQualificacaoVinculo;
    
    @Column(name = "destino_viagem_pais_solicitacao_diaria")
    private String destinoViagemPaisSolicitacaoDiaria;
    
    @Column(name = "destino_viagem_uf_solicitacao_diaria")
    private String destinoViagemUFSolicitacaoDiaria;
    
    @Column(name = "destino_viagem_municipio_solicitacao_diaria")
    private String destinoViagemMunicipioSolicitacaoDiaria;
    
    @Column(name = "tp_transporte_viagem_solicitacao_diaria")
    private String tpTransporteViagemSolicitacaoDiaria;
    
    @Column(name = "tp_viagem_solicitacao_diaria")
    private String tpViagemSolicitacaoDiaria;
    
    @Column(name = "nm_cargo")
    private String nmCargo;
    
    // Campos de auditoria
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public ConsultaGerencial() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getQtdDiariaSolicitacaoDiaria() {
        return qtdDiariaSolicitacaoDiaria;
    }

    public void setQtdDiariaSolicitacaoDiaria(Integer qtdDiariaSolicitacaoDiaria) {
        this.qtdDiariaSolicitacaoDiaria = qtdDiariaSolicitacaoDiaria;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
