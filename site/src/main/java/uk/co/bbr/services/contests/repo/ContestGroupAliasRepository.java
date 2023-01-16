package uk.co.bbr.services.contests.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.contests.dao.ContestAliasDao;
import uk.co.bbr.services.contests.dao.ContestGroupAliasDao;

import java.util.Optional;

public interface ContestGroupAliasRepository extends JpaRepository<ContestGroupAliasDao, Long> {

    @Query("SELECT a FROM ContestGroupAliasDao a WHERE a.contestGroup.id = ?1 and a.name = ?2")
    Optional<ContestGroupAliasDao> fetchByName(Long groupId, String name);
}
