package uk.co.bbr.services.groups.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.groups.dao.ContestGroupAliasDao;

import java.util.List;
import java.util.Optional;

public interface ContestGroupAliasRepository extends JpaRepository<ContestGroupAliasDao, Long> {

    @Query("SELECT a FROM ContestGroupAliasDao a WHERE a.contestGroup.id = ?1 and a.name = ?2")
    Optional<ContestGroupAliasDao> fetchByName(Long groupId, String name);

    @Query("SELECT a FROM ContestGroupAliasDao a " +
            "INNER JOIN ContestGroupDao g ON g.id = a.contestGroup.id " +
            "WHERE a.contestGroup.id = :contestGroupId " +
            "ORDER BY a.name")
    List<ContestGroupAliasDao> findByGroup(Long contestGroupId);
}
