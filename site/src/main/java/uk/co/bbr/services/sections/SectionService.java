package uk.co.bbr.services.sections;

import uk.co.bbr.services.sections.dao.SectionDao;

import java.util.Optional;

public interface SectionService {
    Optional<SectionDao> fetchBySlug(String sectionSlug);

    Optional<SectionDao> fetchByName(String sectionName);
}
