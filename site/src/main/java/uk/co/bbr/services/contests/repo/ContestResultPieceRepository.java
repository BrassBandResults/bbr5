package uk.co.bbr.services.contests.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.bbr.services.contests.dao.ContestResultDao;

public interface ContestResultPieceRepository extends JpaRepository<ContestResultDao, Long> {
}
