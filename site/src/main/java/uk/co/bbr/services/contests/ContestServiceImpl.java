package uk.co.bbr.services.contests;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.contests.dao.ContestAliasDao;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dao.ContestGroupAliasDao;
import uk.co.bbr.services.contests.dao.ContestGroupDao;
import uk.co.bbr.services.contests.repo.ContestAliasRepository;
import uk.co.bbr.services.contests.repo.ContestRepository;
import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.web.security.annotations.IsBbrAdmin;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContestServiceImpl implements ContestService, SlugTools {

    private final SecurityService securityService;
    private final ContestTypeService contestTypeService;
    private final ContestAliasRepository contestAliasRepository;

    private final ContestRepository contestRepository;


    @Override
    @IsBbrMember
    public ContestDao create(String name) {
        ContestDao contest = new ContestDao();
        contest.setName(name);

        return this.create(contest);
    }

    @Override
    @IsBbrMember
    public ContestDao create(ContestDao contest) {
        return this.create(contest, false);
    }

    @Override
    @IsBbrAdmin
    public ContestDao migrate(ContestDao contest) {
        return this.create(contest, true);
    }

    private ContestDao create(ContestDao contest, boolean migrating) {
        // validation
        if (contest.getId() != null) {
            throw new ValidationException("Can't create with specific id");
        }

        if (StringUtils.isBlank(contest.getName())) {
            throw new ValidationException("Band name must be specified");
        }

        // defaults
        if (StringUtils.isBlank(contest.getSlug())) {
            contest.setSlug(slugify(contest.getName()));
        }

        if (contest.getDefaultContestType() == null) {
            contest.setDefaultContestType(this.contestTypeService.fetchDefaultContestType());
        }

        // does the slug already exist?
        Optional<ContestDao> slugMatches = this.contestRepository.fetchBySlug(contest.getSlug());
        if (slugMatches.isPresent()) {
            throw new ValidationException("Contest with slug " + contest.getSlug() + " already exists.");
        }

        // does the slug already exist?
        Optional<ContestDao> nameMatches = this.contestRepository.fetchByName(contest.getName());
        if (nameMatches.isPresent()) {
            throw new ValidationException("Contest with name " + contest.getName() + " already exists.");
        }

        if (!migrating) {
            contest.setCreated(LocalDateTime.now());
            contest.setCreatedBy(this.securityService.getCurrentUser());
            contest.setUpdated(LocalDateTime.now());
            contest.setUpdatedBy(this.securityService.getCurrentUser());
        }
        return this.contestRepository.saveAndFlush(contest);
    }

    @Override
    @IsBbrAdmin
    public ContestAliasDao migrateAlias(ContestDao contest, ContestAliasDao alias) {
        return this.createAlias(contest, alias, true);
    }
    @Override
    @IsBbrMember
    public ContestAliasDao createAlias(ContestDao contest, ContestAliasDao alias) {
        return this.createAlias(contest, alias, false);
    }

    private ContestAliasDao createAlias(ContestDao contest, ContestAliasDao previousName, boolean migrating) {
        previousName.setContest(contest);
        if (!migrating) {
            previousName.setCreated(LocalDateTime.now());
            previousName.setCreatedBy(this.securityService.getCurrentUser());
            previousName.setUpdated(LocalDateTime.now());
            previousName.setUpdatedBy(this.securityService.getCurrentUser());
        }
        return this.contestAliasRepository.saveAndFlush(previousName);
    }

    @Override
    public Optional<ContestAliasDao> aliasExists(ContestDao contest, String aliasName) {
        return this.contestAliasRepository.fetchByNameAndContest(contest.getId(), aliasName);
    }
}
