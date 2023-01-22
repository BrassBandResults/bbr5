package uk.co.bbr.services.contests.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.bbr.services.contests.dao.ContestResultDao;
import uk.co.bbr.services.contests.dao.ContestResultPieceDao;

public interface ContestResultPieceRepository extends JpaRepository<ContestResultPieceDao, Long> {
}
