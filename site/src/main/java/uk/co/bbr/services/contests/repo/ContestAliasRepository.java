package uk.co.bbr.services.contests.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.contests.dao.ContestAliasDao;

import java.util.List;
import java.util.Optional;

public interface ContestAliasRepository extends JpaRepository<ContestAliasDao, Long> {
    @Query("SELECT a FROM ContestAliasDao a WHERE a.contest.id = :contestId AND a.name = :aliasName")
    Optional<ContestAliasDao> fetchByNameAndContest(Long contestId, String aliasName);

    @Query("SELECT a FROM ContestAliasDao a " +
            "WHERE a.contest.id = :contestId " +
            "ORDER BY a.name")
    List<ContestAliasDao> findForContest(Long contestId);
}
