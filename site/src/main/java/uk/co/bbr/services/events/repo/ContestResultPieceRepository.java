package uk.co.bbr.services.events.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.events.dao.ContestResultPieceDao;

import java.util.List;

public interface ContestResultPieceRepository extends JpaRepository<ContestResultPieceDao, Long> {
    @Query("SELECT p FROM ContestResultPieceDao p " +
            "WHERE p.contestResult.id = :contestResultId " +
            "ORDER BY p.ordering")
    List<ContestResultPieceDao> fetchForResult(Long contestResultId);

    @Query("SELECT COUNT(p) FROM ContestResultPieceDao p " +
            "WHERE p.contestResult.contestEvent.contest.id = :contestId")
    int fetchCountOfOwnChoiceForContest(Long contestId);
}
