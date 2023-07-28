package uk.co.bbr.services.tags.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.groups.dao.ContestGroupDao;
import uk.co.bbr.services.tags.dao.ContestTagDao;

import java.util.List;
import java.util.Optional;

public interface ContestTagRepository extends JpaRepository<ContestTagDao, Long> {
    @Query("SELECT t FROM ContestTagDao t WHERE t.slug = ?1")
    Optional<ContestTagDao> fetchBySlug(String slug);

    @Query("SELECT t FROM ContestTagDao t WHERE LOWER(t.name) = LOWER(?1)")
    Optional<ContestTagDao> fetchByName(String name);

    @Query("SELECT t FROM ContestTagDao t ORDER BY t.name")
    List<ContestTagDao> findAllOrderByName();

    @Query("SELECT c FROM ContestDao c " +
            "INNER JOIN c.tags t ON t.slug = ?1 " +
            "ORDER BY c.name")
    List<ContestDao> fetchContestsForTag(String slug);

    @Query("SELECT g FROM ContestGroupDao g " +
            "INNER JOIN g.tags t ON t.slug = ?1 " +
            "ORDER BY g.name")
    List<ContestGroupDao> fetchGroupsForTag(String slug);


}
