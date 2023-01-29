package uk.co.bbr.services.contests;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.contests.dao.ContestGroupAliasDao;
import uk.co.bbr.services.contests.dao.ContestGroupDao;
import uk.co.bbr.services.contests.dto.GroupListDto;
import uk.co.bbr.services.contests.dto.GroupListGroupDto;
import uk.co.bbr.services.contests.repo.ContestGroupAliasRepository;
import uk.co.bbr.services.contests.repo.ContestGroupRepository;
import uk.co.bbr.services.contests.types.ContestGroupType;
import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.web.security.annotations.IsBbrAdmin;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContestGroupServiceImpl implements ContestGroupService, SlugTools {

    private final ContestGroupRepository contestGroupRepository;
    private final ContestGroupAliasRepository contestGroupAliasRepository;
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
        return this.create(contestGroup, false);
    }

    @Override
    @IsBbrAdmin
    public ContestGroupDao migrate(ContestGroupDao contestGroup) {
        return this.create(contestGroup, true);
    }

    private ContestGroupDao create(ContestGroupDao contestGroup, boolean migrating) {
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
        Optional<ContestGroupDao> slugMatches = this.contestGroupRepository.fetchBySlug(contestGroup.getSlug());
        if (slugMatches.isPresent()) {
            throw new ValidationException("Contest Group with slug " + contestGroup.getSlug() + " already exists.");
        }

        // does the name already exist?
        Optional<ContestGroupDao> nameMatches = this.contestGroupRepository.fetchByName(contestGroup.getName());
        if (nameMatches.isPresent()) {
            throw new ValidationException("Contest Group with name " + contestGroup.getName() + " already exists.");
        }

        if (!migrating) {
            contestGroup.setCreated(LocalDateTime.now());
            contestGroup.setCreatedBy(this.securityService.getCurrentUser());
            contestGroup.setUpdated(LocalDateTime.now());
            contestGroup.setUpdatedBy(this.securityService.getCurrentUser());
        }
        return this.contestGroupRepository.saveAndFlush(contestGroup);
    }

    @Override
    @IsBbrMember
    public ContestGroupDao update(ContestGroupDao group) {
        if (group.getId() == null) {
            throw new UnsupportedOperationException("Can't update without an id");
        }

        group.setUpdated(LocalDateTime.now());
        group.setUpdatedBy(this.securityService.getCurrentUser());
        return this.contestGroupRepository.saveAndFlush(group);
    }

    @Override
    public Optional<ContestGroupAliasDao> aliasExists(ContestGroupDao group, String name) {
        String searchName = group.simplifyName(name);
        return this.contestGroupAliasRepository.fetchByName(group.getId(), searchName);
    }

    @Override
    @IsBbrAdmin
    public ContestGroupAliasDao migrateAlias(ContestGroupDao group, ContestGroupAliasDao alias) {
        return this.createAlias(group, alias, true);
    }
    @Override
    @IsBbrMember
    public ContestGroupAliasDao createAlias(ContestGroupDao group, ContestGroupAliasDao alias) {
        return this.createAlias(group, alias, false);
    }

    @Override
    public ContestGroupAliasDao createAlias(ContestGroupDao group, String alias) {
        ContestGroupAliasDao newAlias = new ContestGroupAliasDao();
        newAlias.setName(alias);
        return this.createAlias(group, newAlias);
    }

    private ContestGroupAliasDao createAlias(ContestGroupDao group, ContestGroupAliasDao previousName, boolean migrating) {
        previousName.setContestGroup(group);
        if (!migrating) {
            previousName.setCreated(LocalDateTime.now());
            previousName.setCreatedBy(this.securityService.getCurrentUser());
            previousName.setUpdated(LocalDateTime.now());
            previousName.setUpdatedBy(this.securityService.getCurrentUser());
        }
        return this.contestGroupAliasRepository.saveAndFlush(previousName);
    }

    @Override
    public Optional<ContestGroupDao> fetchBySlug(String groupSlug) {
        return this.contestGroupRepository.fetchBySlug(groupSlug);
    }

    @Override
    public GroupListDto listGroupsStartingWith(String prefix) {
        List<ContestGroupDao> groupsToReturn;

        switch (prefix.toUpperCase()) {
            case "ALL" -> groupsToReturn = this.contestGroupRepository.findAll();
            default -> {
                if (prefix.trim().length() != 1) {
                    throw new UnsupportedOperationException("Prefix must be a single character");
                }
                groupsToReturn = this.contestGroupRepository.findByPrefixOrderByName(prefix.trim().toUpperCase());
            }
        }

        long allGroupsCount = this.contestGroupRepository.count();

        List<GroupListGroupDto> returnedBands = new ArrayList<>();
        for (ContestGroupDao eachGroup : groupsToReturn) {
            returnedBands.add(new GroupListGroupDto(eachGroup.getSlug(), eachGroup.getName(), eachGroup.getContestCount()));
        }
        return new GroupListDto(groupsToReturn.size(), allGroupsCount, prefix, returnedBands);
    }


}
