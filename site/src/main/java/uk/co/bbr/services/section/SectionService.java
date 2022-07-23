package uk.co.bbr.services.section;

import uk.co.bbr.services.section.dao.SectionDao;

public interface SectionService {
    SectionDao fetchBySlug(String sectionSlug);
}
