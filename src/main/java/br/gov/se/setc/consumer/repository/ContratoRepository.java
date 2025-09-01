package br.gov.se.setc.consumer.repository;
import br.gov.se.setc.consumer.entity.Contrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
/**
 * Repositório JPA para Contrato.
 */
@Repository
public interface ContratoRepository extends JpaRepository<Contrato, Long> {
    /**
     * Busca contratos por unidade gestora.
     */
    List<Contrato> findByCdUnidadeGestora(String cdUnidadeGestora);
    /**
     * Busca contratos por ano de exercício.
     */
    List<Contrato> findByDtAnoExercicio(Integer dtAnoExercicio);
    /**
     * Busca contratos por unidade gestora e ano.
     */
    List<Contrato> findByCdUnidadeGestoraAndDtAnoExercicio(String cdUnidadeGestora, Integer dtAnoExercicio);
    /**
     * Remove registros do ano atual.
     */
    @Modifying
    @Query("DELETE FROM Contrato c WHERE c.dtAnoExercicio = EXTRACT(YEAR FROM CURRENT_DATE)")
    void deleteByCurrentYear();
    /**
     * Remove registros por unidade gestora e ano específicos.
     */
    @Modifying
    @Query("DELETE FROM Contrato c WHERE c.cdUnidadeGestora = :cdUnidadeGestora AND c.dtAnoExercicio = :ano")
    void deleteByCdUnidadeGestoraAndAno(@Param("cdUnidadeGestora") String cdUnidadeGestora, @Param("ano") Integer ano);
}
