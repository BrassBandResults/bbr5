package uk.co.bbr.services.contests.repo;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dao.ContestEventDao;
import uk.co.bbr.services.contests.dao.ContestGroupDao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ContestGroupRepository extends JpaRepository<ContestGroupDao, Long> {
    @Query("SELECT g FROM ContestGroupDao g WHERE g.slug = :slug")
    Optional<ContestGroupDao> fetchBySlug(String slug);
    @Query("SELECT g FROM ContestGroupDao g WHERE g.name = :name")
    Optional<ContestGroupDao> fetchByName(String name);

    @Query("SELECT g FROM ContestGroupDao g " +
            "ORDER BY g.name")
    List<ContestGroupDao> findAllOrderByName();

    @Query("SELECT g FROM ContestGroupDao g " +
            "WHERE UPPER(g.name) LIKE UPPER(CONCAT(:prefix, '%')) " +
            "ORDER BY g.name")
    List<ContestGroupDao> findByPrefixOrderByName(String prefix);

    @Query("SELECT c FROM ContestDao c " +
            "WHERE c.contestGroup.id = :groupId " +
            "AND c.extinct = false " +
            "ORDER BY c.name")
    List<ContestDao> fetchActiveContestsForGroup(Long groupId);

    @Query("SELECT c FROM ContestDao c " +
            "WHERE c.contestGroup.id = :groupId " +
            "AND c.extinct = true " +
            "ORDER BY c.name")
    List<ContestDao> fetchOldContestsForGroup(Long groupId);

    @Query("SELECT e FROM ContestEventDao e " +
            "WHERE e.contest.contestGroup.id = :groupId " +
            "ORDER BY e.eventDate")
    List<ContestEventDao> fetchEventsForGroupOrderByEventDate(Long groupId);

    @Query("SELECT e FROM ContestEventDao e " +
            "WHERE e.contest.contestGroup.id = :groupId " +
            "AND YEAR(e.eventDate) = :year " +
            "ORDER BY e.contest.ordering")
    List<ContestEventDao> selectByGroupSlugAndYear(Long groupId, Integer year);

    @Query("SELECT e FROM ContestEventDao e " +
            "WHERE e.contest.contestGroup.id = :groupId " +
            "AND YEAR(e.eventDate) > :after " +
            "ORDER BY e.eventDate")
    List<ContestEventDao> selectNextEventByGroupSlugAndYear(Long groupId, Integer after, Pageable pageable);

    @Query("SELECT e FROM ContestEventDao e " +
            "WHERE e.contest.contestGroup.id = :groupId " +
            "AND YEAR(e.eventDate) < :before " +
            "ORDER BY e.eventDate desc")
    List<ContestEventDao> selectPreviousEventByGroupSlugAndYear(Long groupId, Integer before, Pageable pageable);
}
