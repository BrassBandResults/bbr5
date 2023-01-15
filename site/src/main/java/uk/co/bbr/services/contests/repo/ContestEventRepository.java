package uk.co.bbr.services.contests.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.contests.dao.ContestEventDao;

import java.time.LocalDate;

public interface ContestEventRepository extends JpaRepository<ContestEventDao, Long> {
    @Query("SELECT e FROM ContestEventDao e WHERE e.contest.id = ?1 and e.eventDate = ?2")
    ContestEventDao fetchByContestAndDate(Long contestId, LocalDate eventDate);
}
