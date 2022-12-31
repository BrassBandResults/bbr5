package uk.co.bbr.services.contests.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dao.ContestGroupDao;

import java.util.Optional;

public interface ContestRepository extends JpaRepository<ContestDao, Long> {
    @Query("SELECT c FROM ContestDao c WHERE c.slug = ?1")
    Optional<ContestGroupDao> findBySlug(String slug);

    @Query("SELECT c FROM ContestDao c WHERE c.name = ?1")
    Optional<ContestGroupDao> findByName(String name);
}
