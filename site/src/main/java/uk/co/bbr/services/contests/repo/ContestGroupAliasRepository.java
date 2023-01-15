package uk.co.bbr.services.contests.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.bbr.services.contests.dao.ContestAliasDao;
import uk.co.bbr.services.contests.dao.ContestGroupAliasDao;

public interface ContestGroupAliasRepository extends JpaRepository<ContestGroupAliasDao, Long> {
}
