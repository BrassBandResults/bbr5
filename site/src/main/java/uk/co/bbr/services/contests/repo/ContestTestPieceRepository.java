package uk.co.bbr.services.contests.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dao.ContestTestPieceDao;

import java.util.List;

public interface ContestTestPieceRepository extends JpaRepository<ContestTestPieceDao, Long> {
    @Query("SELECT cp FROM ContestTestPieceDao cp " +
            "INNER JOIN PieceDao p ON p.id = cp.piece.id " +
            "WHERE cp.contestEvent.id = ?1")
    List<ContestTestPieceDao> fetchForEvent(Long eventId);
}
