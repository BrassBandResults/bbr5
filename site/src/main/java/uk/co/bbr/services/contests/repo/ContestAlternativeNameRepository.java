package uk.co.bbr.services.contests.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.bbr.services.contests.dao.ContestAlternativeNameDao;
import uk.co.bbr.services.contests.dao.ContestDao;

public interface ContestAlternativeNameRepository extends JpaRepository<ContestAlternativeNameDao, Long> {
}
