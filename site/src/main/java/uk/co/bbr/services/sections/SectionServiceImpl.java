package uk.co.bbr.services.sections;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.sections.dao.SectionDao;
import uk.co.bbr.services.sections.repo.SectionRepository;

import java.util.List;
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

    @Override
    public List<SectionDao> fetchAll() {
        return this.sectionRepository.findAllSortByName();
    }

    @Override
    public Optional<SectionDao> fetchById(Long sectionId) {
        return this.sectionRepository.fetchById(sectionId);
    }
}
