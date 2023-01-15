package uk.co.bbr.services.sections;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.sections.dao.SectionDao;
import uk.co.bbr.services.sections.repo.SectionRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SectionServiceImpl implements SectionService {

    private final SectionRepository sectionRepository;
    @Override
    public Optional<SectionDao> fetchBySlug(String sectionSlug) {
        return this.sectionRepository.fetchBySlug(sectionSlug);
    }

    @Override
    public Optional<SectionDao> fetchByName(String sectionName) {
        return this.sectionRepository.fetchByName(sectionName);
    }
}
