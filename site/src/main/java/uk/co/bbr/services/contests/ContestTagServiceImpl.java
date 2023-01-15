package uk.co.bbr.services.contests;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.contests.dao.ContestTagDao;
import uk.co.bbr.services.contests.repo.ContestTagRepository;
import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContestTagServiceImpl implements ContestTagService, SlugTools {

    private final ContestTagRepository contestTagRepository;

    private final SecurityService securityService;

    @Override
    @IsBbrMember
    public ContestTagDao create(String name) {
        ContestTagDao newTag = new ContestTagDao();
        newTag.setName(name);

        return this.create(newTag);
    }

    @Override
    @IsBbrMember
    public ContestTagDao migrate(ContestTagDao contestTag) {
        return this.create(contestTag, true);
    }

    @Override
    public Optional<ContestTagDao> fetchByName(String name) {
        return this.contestTagRepository.fetchByName(name);
    }

    @Override
    @IsBbrMember
    public ContestTagDao create(ContestTagDao contestTag) {
        return this.create(contestTag, false);
    }

    private ContestTagDao create(ContestTagDao contestTag, boolean migrating) {
        // validation
        if (contestTag.getId() != null) {
            throw new ValidationException("Can't create with specific id");
        }

        if (StringUtils.isBlank(contestTag.getName())) {
            throw new ValidationException("Band name must be specified");
        }

        // defaults
        if (StringUtils.isBlank(contestTag.getSlug())) {
            contestTag.setSlug(slugify(contestTag.getName()));
        }

        // does the slug already exist?
        Optional<ContestTagDao> slugMatches = this.contestTagRepository.fetchBySlug(contestTag.getSlug());
        if (slugMatches.isPresent()) {
            throw new ValidationException("Contest Tag with slug " + contestTag.getSlug() + " already exists.");
        }

        // does the slug already exist?
        Optional<ContestTagDao> nameMatches = this.contestTagRepository.fetchByName(contestTag.getName());
        if (nameMatches.isPresent()) {
            throw new ValidationException("Contest Tag with name " + contestTag.getName() + " already exists.");
        }

        if (!migrating) {
            contestTag.setCreated(LocalDateTime.now());
            contestTag.setCreatedBy(this.securityService.getCurrentUser());
            contestTag.setUpdated(LocalDateTime.now());
            contestTag.setUpdatedBy(this.securityService.getCurrentUser());
        }
        return this.contestTagRepository.saveAndFlush(contestTag);
    }
}
