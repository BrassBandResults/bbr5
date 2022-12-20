package uk.co.bbr.services.sections;

import uk.co.bbr.services.sections.dao.SectionDao;

public interface SectionService {
    SectionDao fetchBySlug(String sectionSlug);

    SectionDao fetchByName(String sectionName);
}
