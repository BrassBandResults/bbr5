package uk.co.bbr.services.contests.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dao.ContestTagDao;

import java.util.Optional;

public interface ContestTagRepository extends JpaRepository<ContestTagDao, Long> {
    @Query("SELECT t FROM ContestTagDao t WHERE t.slug = ?1")
    Optional<ContestTagDao> findBySlug(String slug);

    @Query("SELECT t FROM ContestTagDao t WHERE LOWER(t.name) = LOWER(?1)")
    Optional<ContestTagDao> findByName(String name);
}
