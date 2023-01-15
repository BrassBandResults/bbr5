package uk.co.bbr.services.contests.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.contests.dao.ContestDao;

import java.util.Optional;

public interface ContestRepository extends JpaRepository<ContestDao, Long> {
    @Query("SELECT c FROM ContestDao c WHERE c.slug = ?1")
    Optional<ContestDao> fetchBySlug(String slug);

    @Query("SELECT c FROM ContestDao c WHERE c.name = ?1")
    Optional<ContestDao> fetchByName(String name);
}
