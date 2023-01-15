package uk.co.bbr.services.sections.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.sections.dao.SectionDao;

import java.util.Optional;

public interface SectionRepository extends JpaRepository<SectionDao, Long> {
    @Query("SELECT s FROM SectionDao s WHERE s.slug = ?1")
    Optional<SectionDao> fetchBySlug(String sectionSlug);

    @Query("SELECT s FROM SectionDao s WHERE s.name = ?1")
    Optional<SectionDao> fetchByName(String sectionName);
}
