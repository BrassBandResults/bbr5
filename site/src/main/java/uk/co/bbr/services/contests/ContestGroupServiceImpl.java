package uk.co.bbr.services.contests;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.contests.dao.ContestGroupDao;
import uk.co.bbr.services.contests.repo.ContestGroupRepository;
import uk.co.bbr.services.contests.repo.ContestGroupRepository;
import uk.co.bbr.services.contests.types.ContestGroupType;
import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContestGroupServiceImpl implements ContestGroupService, SlugTools {

    private final ContestGroupRepository contestGroupRepository;
    private final SecurityService securityService;

    @Override
    @IsBbrMember
    public ContestGroupDao create(String name) {
        ContestGroupDao newGroup = new ContestGroupDao();
        newGroup.setName(name);

        return this.create(newGroup);
    }

    @Override
    @IsBbrMember
    public ContestGroupDao create(ContestGroupDao contestGroup) {
        // validation
        if (contestGroup.getId() != null) {
            throw new ValidationException("Can't create with specific id");
        }

        if (StringUtils.isBlank(contestGroup.getName())) {
            throw new ValidationException("Band name must be specified");
        }

        // defaults
        if (StringUtils.isBlank(contestGroup.getSlug())) {
            contestGroup.setSlug(slugify(contestGroup.getName()));
        }

        if (contestGroup.getGroupType() == null) {
            contestGroup.setGroupType(ContestGroupType.NORMAL);
        }

        // does the slug already exist?
        Optional<ContestGroupDao> slugMatches = this.contestGroupRepository.findBySlug(contestGroup.getSlug());
        if (slugMatches.isPresent()) {
            throw new ValidationException("Contest Group with slug " + contestGroup.getSlug() + " already exists.");
        }

        // does the name already exist?
        Optional<ContestGroupDao> nameMatches = this.contestGroupRepository.findByName(contestGroup.getName());
        if (nameMatches.isPresent()) {
            throw new ValidationException("Contest Group with name " + contestGroup.getName() + " already exists.");
        }

        contestGroup.setCreated(LocalDateTime.now());
        contestGroup.setCreatedBy(this.securityService.getCurrentUserId());
        return this.contestGroupRepository.saveAndFlush(contestGroup);
    }

    @Override
    @IsBbrMember
    public ContestGroupDao update(ContestGroupDao group) {
        if (group.getId() == null) {
            throw new UnsupportedOperationException("Can't update without an id");
        }

        group.setUpdated(LocalDateTime.now());
        group.setUpdatedBy(this.securityService.getCurrentUserId());
        return this.contestGroupRepository.saveAndFlush(group);
    }
}
