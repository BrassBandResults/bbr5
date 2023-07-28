package uk.co.bbr.services.contests;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.contests.dao.ContestAliasDao;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.sql.ContestListSql;
import uk.co.bbr.services.contests.sql.dto.ContestListSqlDto;
import uk.co.bbr.services.groups.dao.ContestGroupAliasDao;
import uk.co.bbr.services.groups.dao.ContestGroupDao;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.tags.dao.ContestTagDao;
import uk.co.bbr.services.contests.dto.ContestListContestDto;
import uk.co.bbr.services.contests.dto.ContestListDto;
import uk.co.bbr.services.contests.repo.ContestAliasRepository;
import uk.co.bbr.services.groups.repo.ContestGroupAliasRepository;
import uk.co.bbr.services.groups.repo.ContestGroupRepository;
import uk.co.bbr.services.contests.repo.ContestRepository;
import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.web.security.annotations.IsBbrAdmin;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContestServiceImpl implements ContestService, SlugTools {

    private final SecurityService securityService;
    private final RegionService regionService;

    private final ContestTypeService contestTypeService;
    private final ContestAliasRepository contestAliasRepository;
    private final ContestGroupRepository contestGroupRepository;
    private final ContestGroupAliasRepository contestGroupAliasRepository;
    private final ContestRepository contestRepository;

    private final EntityManager entityManager;

    @Override
    @IsBbrMember
    public ContestDao create(String name) {
        ContestDao contest = new ContestDao();
        contest.setName(name);

        return this.create(contest);
    }

    @Override
    @IsBbrMember
    public ContestDao create(String contestName, ContestGroupDao group, int ordering) {
        ContestDao contest = new ContestDao();
        contest.setName(contestName);
        contest.setContestGroup(group);
        contest.setOrdering(ordering);

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

    @Override
    @IsBbrMember
    public ContestDao update(ContestDao contest) {
        // validation
        if (contest.getId() == null) {
            throw new ValidationException("Can't update without specific id");
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
        if (slugMatches.isPresent() && !slugMatches.get().getId().equals(contest.getId())) {
            throw new ValidationException("Contest with slug " + contest.getSlug() + " already exists.");
        }

        // does the name already exist?
        Optional<ContestDao> nameMatches = this.contestRepository.fetchByExactName(contest.getName());
        if (nameMatches.isPresent() && !nameMatches.get().getId().equals(contest.getId())) {
            throw new ValidationException("Contest with name " + contest.getName() + " already exists.");
        }

        contest.setUpdated(LocalDateTime.now());
        contest.setUpdatedBy(this.securityService.getCurrentUsername());

        return this.contestRepository.saveAndFlush(contest);
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

        if (contest.getRegion() == null) {
            contest.setRegion(this.regionService.fetchUnknownRegion());
        }

        // does the slug already exist?
        Optional<ContestDao> slugMatches = this.contestRepository.fetchBySlug(contest.getSlug());
        if (slugMatches.isPresent()) {
            throw new ValidationException("Contest with slug " + contest.getSlug() + " already exists.");
        }

        // does the name already exist?
        Optional<ContestDao> nameMatches = this.contestRepository.fetchByExactName(contest.getName());
        if (nameMatches.isPresent()) {
            throw new ValidationException("Contest with name " + contest.getName() + " already exists.");
        }

        if (!migrating) {
            contest.setCreated(LocalDateTime.now());
            contest.setCreatedBy(this.securityService.getCurrentUsername());
            contest.setUpdated(LocalDateTime.now());
            contest.setUpdatedBy(this.securityService.getCurrentUsername());
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

    @Override
    @IsBbrMember
    public ContestAliasDao createAlias(ContestDao contest, String previousName) {
        ContestAliasDao newAlias = new ContestAliasDao();
        newAlias.setName(previousName);
        return this.createAlias(contest, newAlias);
    }

    private ContestAliasDao createAlias(ContestDao contest, ContestAliasDao previousName, boolean migrating) {
        previousName.setContest(contest);
        if (!migrating) {
            previousName.setCreated(LocalDateTime.now());
            previousName.setCreatedBy(this.securityService.getCurrentUsername());
            previousName.setUpdated(LocalDateTime.now());
            previousName.setUpdatedBy(this.securityService.getCurrentUsername());
        }
        return this.contestAliasRepository.saveAndFlush(previousName);
    }

    @Override
    public Optional<ContestAliasDao> aliasExists(ContestDao contest, String aliasName) {
        return this.contestAliasRepository.fetchByNameAndContest(contest.getId(), aliasName);
    }

    @Override
    public Optional<ContestDao> fetchBySlug(String slug) {
        return this.contestRepository.fetchBySlug(slug);
    }

    @Override
    public List<ContestListSqlDto> listContestsStartingWith(String prefix) {

        List<ContestListSqlDto> contests;

        if (prefix.equalsIgnoreCase("ALL")) {
            contests = ContestListSql.listAllForContestList(this.entityManager);
        } else {
            if (prefix.trim().length() != 1) {
                throw new UnsupportedOperationException("Prefix must be a single character");
            }
            String upperPrefix = prefix.trim().toUpperCase();
            contests = ContestListSql.listByPrefixForContestList(this.entityManager, upperPrefix);
        }
        return contests;
    }

    @Override
    @IsBbrMember
    public ContestDao addContestToGroup(ContestDao contest, ContestGroupDao group) {
        contest.setContestGroup(group);
        return this.update(contest);
    }

    @Override
    @IsBbrMember
    public ContestDao addContestTag(ContestDao contest, ContestTagDao tag) {
        contest.getTags().add(tag);
        return this.update(contest);
    }

    @Override
    public List<ContestAliasDao> fetchAliases(ContestDao contest) {
        return this.contestAliasRepository.findForContest(contest.getId());
    }

    @Override
    public Optional<ContestDao> fetchByExactName(String contestName) {
        return this.contestRepository.fetchByExactName(contestName);
    }

    @Override
    public Optional<ContestDao> fetchByNameUpper(String contestName) {
        return this.contestRepository.fetchByNameUpper(contestName.toUpperCase());
    }
}
