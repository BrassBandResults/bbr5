package uk.co.bbr.services.contests;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.contests.dao.ContestTagDao;
import uk.co.bbr.services.contests.repo.ContestTagRepository;
import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.time.LocalDateTime;
import java.util.List;
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
    public ContestTagDao create(ContestTagDao contestTag) {
        // validation
        if (contestTag.getId() != null) {
            throw new ValidationException("Can't create with specific id");
        }

        if (contestTag.getName() == null || contestTag.getName().trim().length() == 0) {
            throw new ValidationException("Band name must be specified");
        }

        // defaults
        if (contestTag.getSlug() == null || contestTag.getSlug().trim().length() == 0) {
            contestTag.setSlug(slugify(contestTag.getName()));
        }

        // does the slug already exist?
        Optional<ContestTagDao> slugMatches = this.contestTagRepository.findBySlug(contestTag.getSlug());
        if (slugMatches.isPresent()) {
            throw new ValidationException("Contest Tag with slug " + contestTag.getSlug() + " already exists.");
        }

        // does the slug already exist?
        Optional<ContestTagDao> nameMatches = this.contestTagRepository.findByName(contestTag.getName());
        if (nameMatches.isPresent()) {
            throw new ValidationException("Contest Tag with name " + contestTag.getName() + " already exists.");
        }

        contestTag.setCreated(LocalDateTime.now());
        contestTag.setCreatedBy(this.securityService.getCurrentUserId());
        return this.contestTagRepository.saveAndFlush(contestTag);
    }
}
