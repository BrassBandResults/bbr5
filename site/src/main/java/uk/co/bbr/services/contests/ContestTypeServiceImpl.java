package uk.co.bbr.services.contests;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.contests.dao.ContestTypeDao;
import uk.co.bbr.services.contests.repo.ContestTypeRepository;
import uk.co.bbr.services.framework.mixins.SlugTools;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContestTypeServiceImpl implements ContestTypeService, SlugTools {

    private static final String DEFAULT_SLUG = "test-piece-contest";

    private final ContestTypeRepository contestTypeRepository;

    @Override
    public ContestTypeDao fetchDefaultContestType() {
        return this.contestTypeRepository.fetchBySlug(DEFAULT_SLUG);
    }

    @Override
    public Optional<ContestTypeDao> fetchByName(String contestTypeName) {
        return this.contestTypeRepository.fetchByName(contestTypeName);
    }
}
