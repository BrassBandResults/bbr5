package uk.co.bbr.services.section;

import uk.co.bbr.services.section.dao.SectionDao;

import java.util.Optional;

public interface SectionService {
    SectionDao fetchBySlug(String sectionSlug);

    SectionDao fetchByName(String sectionName);
}
