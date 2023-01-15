package uk.co.bbr.services.contests.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.contests.dao.ContestAliasDao;

import java.util.Optional;

public interface ContestAliasRepository extends JpaRepository<ContestAliasDao, Long> {
    @Query("SELECT a FROM ContestAliasDao a WHERE a.contest.id = ?1 AND a.name = ?2")
    Optional<ContestAliasDao> fetchByNameAndContest(Long contestId, String aliasName);
}
