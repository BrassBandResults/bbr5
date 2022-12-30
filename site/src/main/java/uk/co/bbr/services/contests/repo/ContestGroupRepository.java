package uk.co.bbr.services.contests.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dao.ContestGroupDao;

import java.util.Optional;

public interface ContestGroupRepository extends JpaRepository<ContestGroupDao, Long> {
    @Query("SELECT g FROM ContestGroupDao g WHERE g.slug = ?1")
    Optional<ContestGroupDao> findBySlug(String slug);
    @Query("SELECT g FROM ContestGroupDao g WHERE g.name = ?1")
    Optional<ContestGroupDao> findByName(String name);
}