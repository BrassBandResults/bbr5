package uk.co.bbr.services.section;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.section.dao.SectionDao;
import uk.co.bbr.services.section.dao.SectionRepository;

@Service
@RequiredArgsConstructor
public class SectionServiceImpl implements SectionService {

    private final SectionRepository sectionRepository;
    @Override
    public SectionDao fetchBySlug(String sectionSlug) {
        return this.sectionRepository.findBySlug(sectionSlug);
    }

    @Override
    public SectionDao fetchByName(String sectionName) {
        return this.sectionRepository.findByName(sectionName);
    }
}
