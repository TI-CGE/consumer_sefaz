package br.gov.se.setc.consumer.repository;

import br.gov.se.setc.consumer.entity.Termo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório JPA para Termo (Convênios).
 * 
 * Substitui o uso de JdbcTemplate por operações JPA nativas,
 * garantindo consistência de tipos e validação automática.
 * Inclui métodos para evitar duplicatas baseadas em cd_convenio.
 */
@Repository
public interface TermoRepository extends JpaRepository<Termo, Long> {
    
    /**
     * Busca termo por código do convênio.
     */
    Optional<Termo> findByCdConvenio(Long cdConvenio);
    
    /**
     * Busca termos por unidade gestora.
     */
    List<Termo> findByCdUnidadeGestora(String cdUnidadeGestora);
    
    /**
     * Busca termos por código de gestão.
     */
    List<Termo> findByCdGestao(String cdGestao);
    
    /**
     * Busca termos por situação do convênio.
     */
    List<Termo> findByCdConvenioSituacao(String cdConvenioSituacao);
    
    /**
     * Busca termos por unidade gestora e situação.
     */
    List<Termo> findByCdUnidadeGestoraAndCdConvenioSituacao(String cdUnidadeGestora, String cdConvenioSituacao);
    
    /**
     * Remove registros do ano atual para permitir nova carga.
     * Equivale à lógica de deleteByMesVigente do sistema anterior.
     */
    @Modifying
    @Query("DELETE FROM Termo t WHERE EXTRACT(YEAR FROM t.dtInicioVigenciaConv) = EXTRACT(YEAR FROM CURRENT_DATE) " +
           "AND EXTRACT(MONTH FROM t.dtInicioVigenciaConv) = EXTRACT(MONTH FROM CURRENT_DATE)")
    void deleteByCurrentYearAndMonth();
    
    /**
     * Remove registros por unidade gestora e ano específicos.
     */
    @Modifying
    @Query("DELETE FROM Termo t WHERE t.cdUnidadeGestora = :cdUnidadeGestora " +
           "AND EXTRACT(YEAR FROM t.dtInicioVigenciaConv) = :ano")
    void deleteByCdUnidadeGestoraAndAno(@Param("cdUnidadeGestora") String cdUnidadeGestora, @Param("ano") Integer ano);
    
    /**
     * Conta registros por unidade gestora.
     */
    long countByCdUnidadeGestora(String cdUnidadeGestora);
    
    /**
     * Verifica se existe termo com o código do convênio.
     */
    boolean existsByCdConvenio(Long cdConvenio);
}
