package br.gov.se.setc.consumer.repository;
import br.gov.se.setc.consumer.entity.ConsultaGerencial;
import org.springframework.data.jpa.repository.JpaRepository;
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
     * Conta registros por unidade gestora e ano.
     */
    long countByCdUnidadeGestoraAndDtAnoExercicioCTB(String cdUnidadeGestora, Integer dtAnoExercicioCTB);
}