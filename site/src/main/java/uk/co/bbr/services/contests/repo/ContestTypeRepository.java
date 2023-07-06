package uk.co.bbr.services.contests.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.contests.dao.ContestTypeDao;

import java.util.List;
import java.util.Optional;

public interface ContestTypeRepository extends JpaRepository<ContestTypeDao, Long> {
    @Query("SELECT t FROM ContestTypeDao t WHERE t.slug = ?1")
    ContestTypeDao fetchBySlug(String slug);

    @Query("SELECT t FROM ContestTypeDao t WHERE t.name = ?1")
    Optional<ContestTypeDao> fetchByName(String contestTypeName);

    @Query("SELECT t FROM ContestTypeDao t ORDER BY t.name")
    List<ContestTypeDao> fetchAllOrderByName();
}
