package uk.co.bbr.services.contests;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.contests.dao.ContestTypeDao;
import uk.co.bbr.services.contests.repo.ContestTypeRepository;
import uk.co.bbr.services.framework.mixins.SlugTools;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContestTypeServiceImpl implements ContestTypeService, SlugTools {

    private static final String DEFAULT_SLUG = "test-piece-contest";

    private final ContestTypeRepository contestTypeRepository;

    @Override
    public ContestTypeDao fetchDefaultContestType() {
        return this.contestTypeRepository.fetchBySlug(DEFAULT_SLUG).get();
    }

    @Override
    public Optional<ContestTypeDao> fetchByName(String contestTypeName) {
        return this.contestTypeRepository.fetchByName(contestTypeName);
    }

    @Override
    public List<ContestTypeDao> fetchAll() {
        return this.contestTypeRepository.fetchAllOrderByName();
    }

    @Override
    public Optional<ContestTypeDao> fetchById(Long contestTypeId) {
        return this.contestTypeRepository.findById(contestTypeId);
    }

    @Override
    public Optional<ContestTypeDao> fetchBySlug(String contestTypeSlug) {
        return this.contestTypeRepository.fetchBySlug(contestTypeSlug);
    }
}
