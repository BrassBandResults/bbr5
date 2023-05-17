package uk.co.bbr.services.contests.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.contests.dao.ContestDao;

import java.util.List;
import java.util.Optional;

public interface ContestRepository extends JpaRepository<ContestDao, Long> {
    @Query("SELECT c FROM ContestDao c WHERE c.slug = ?1")
    Optional<ContestDao> fetchBySlug(String slug);

    @Query("SELECT c FROM ContestDao c WHERE c.name = ?1")
    Optional<ContestDao> fetchByName(String name);

    @Query("SELECT c FROM ContestDao c WHERE UPPER(c.name) LIKE UPPER(CONCAT(:prefix, '%')) ORDER BY c.name")
    List<ContestDao> findByPrefixOrderByName(String prefix);

    @Query("SELECT c FROM ContestDao c WHERE c.contestGroup IS NULL ORDER BY c.name")
    List<ContestDao> findAllOutsideGroupsOrderByName();

    @Query("SELECT c FROM ContestDao c " +
            "WHERE UPPER(c.name) LIKE UPPER(CONCAT(:prefix, '%')) AND c.contestGroup IS NULL ORDER BY c.name")
    List<ContestDao> findByPrefixOutsideGroupsOrderByName(String prefix);

    @Query("SELECT c FROM ContestDao c " +
            "WHERE c.region.id = :regionId")
    List<ContestDao> findContestsForRegion(Long regionId);
}
