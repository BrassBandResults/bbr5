package uk.co.bbr.services.sections.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.sections.dao.SectionDao;

import java.util.List;
import java.util.Optional;

public interface SectionRepository extends JpaRepository<SectionDao, Long> {
    @Query("SELECT s FROM SectionDao s WHERE s.slug = :sectionSlug")
    Optional<SectionDao> fetchBySlug(String sectionSlug);

    @Query("SELECT s FROM SectionDao s WHERE s.name = :sectionName")
    Optional<SectionDao> fetchByName(String sectionName);

    @Query("SELECT s FROM SectionDao s ORDER BY s.name")
    List<SectionDao> findAllSortByName();

    @Query("SELECT s FROM SectionDao s WHERE s.id = :sectionId")
    Optional<SectionDao> fetchById(Long sectionId);
}
