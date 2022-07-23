package uk.co.bbr.services.section.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SectionRepository extends JpaRepository<SectionDao, Long> {
    @Query("SELECT s FROM SectionDao s WHERE s.slug = ?1")
    SectionDao findBySlug(String sectionSlug);
}
