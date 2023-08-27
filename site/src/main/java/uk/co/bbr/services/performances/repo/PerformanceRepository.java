package uk.co.bbr.services.performances.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.performances.dao.PerformanceDao;

import java.util.List;
import java.util.Optional;

public interface PerformanceRepository extends JpaRepository<PerformanceDao, Long> {

    @Query("SELECT p FROM PerformanceDao p WHERE p.createdBy = :usercode AND p.status = 'P' ORDER BY p.result.contestEvent.eventDate DESC")
    List<PerformanceDao> fetchPendingUserPerformances(String usercode);

    @Query("SELECT p FROM PerformanceDao p WHERE p.createdBy = :usercode AND p.status = 'A' ORDER BY p.result.contestEvent.eventDate DESC")
    List<PerformanceDao> fetchApprovedUserPerformances(String usercode);

    @Query("SELECT p FROM PerformanceDao p WHERE p.status = 'A' AND p.result.contestEvent.id = :eventId ORDER BY p.result.position")
    List<PerformanceDao> fetchForEvent(Long eventId);

    @Query("SELECT p FROM PerformanceDao p WHERE p.result.id = :resultId")
    List<PerformanceDao> fetchPerformancesForResult(Long resultId);

    @Query("SELECT p FROM PerformanceDao p WHERE p.createdBy = :usercode AND p.id = :performanceId")
    Optional<PerformanceDao> fetchByUserAndId(String usercode, Long performanceId);

    @Query("SELECT p FROM PerformanceDao p WHERE p.createdBy = :usercode AND p.result.id = :resultId")
    Optional<PerformanceDao> fetchForResult(String usercode, Long resultId);
}
