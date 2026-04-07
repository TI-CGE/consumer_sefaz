package br.gov.se.setc.logging.repository;

import br.gov.se.setc.logging.entity.ErrorLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ErrorLogRepository extends JpaRepository<ErrorLogEntity, Long> {
    List<ErrorLogEntity> findByComponent(String component);

    List<ErrorLogEntity> findByExceptionType(String exceptionType);

    List<ErrorLogEntity> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT e FROM ErrorLogEntity e WHERE e.timestamp >= :since ORDER BY e.timestamp DESC")
    List<ErrorLogEntity> findRecentErrors(@Param("since") LocalDateTime since);

    @Query("SELECT e FROM ErrorLogEntity e WHERE e.component = :component AND e.timestamp >= :since ORDER BY e.timestamp DESC")
    List<ErrorLogEntity> findByComponentAndSince(@Param("component") String component, @Param("since") LocalDateTime since);
}

