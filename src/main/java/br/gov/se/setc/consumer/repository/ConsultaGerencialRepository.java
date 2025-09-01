package br.gov.se.setc.consumer.repository;
import br.gov.se.setc.consumer.entity.ConsultaGerencial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
/**
 * Repositório JPA para ConsultaGerencial.
 *
 * Substitui o uso de JdbcTemplate por operações JPA nativas,
 * garantindo consistência de tipos e validação automática.
 */
@Repository
public interface ConsultaGerencialRepository extends JpaRepository<ConsultaGerencial, Long> {
    /**
     * Busca consultas gerenciais por unidade gestora.
     */
    List<ConsultaGerencial> findByCdUnidadeGestora(String cdUnidadeGestora);
    /**
     * Busca consultas gerenciais por ano de exercício.
     */
    List<ConsultaGerencial> findByDtAnoExercicioCTB(Integer dtAnoExercicioCTB);
    /**
     * Busca consultas gerenciais por unidade gestora e ano.
     */
    List<ConsultaGerencial> findByCdUnidadeGestoraAndDtAnoExercicioCTB(
        String cdUnidadeGestora, Integer dtAnoExercicioCTB);
    /**
     * Remove registros do ano atual para permitir nova carga.
     * Equivale à lógica de deleteByMesVigente do sistema anterior.
     */
    @Modifying
    @Query("DELETE FROM ConsultaGerencial c WHERE c.dtAnoExercicioCTB = EXTRACT(YEAR FROM CURRENT_DATE)")
    void deleteByCurrentYear();
    /**
     * Remove registros por unidade gestora e ano específicos.
     */
    @Modifying
    @Query("DELETE FROM ConsultaGerencial c WHERE c.cdUnidadeGestora = :cdUnidadeGestora AND c.dtAnoExercicioCTB = :ano")
    void deleteByCdUnidadeGestoraAndAno(@Param("cdUnidadeGestora") String cdUnidadeGestora, @Param("ano") Integer ano);
    /**
     * Conta registros por unidade gestora e ano.
     */
    long countByCdUnidadeGestoraAndDtAnoExercicioCTB(String cdUnidadeGestora, Integer dtAnoExercicioCTB);
}