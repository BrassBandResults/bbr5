package uk.co.bbr.services.contests;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dao.ContestEventDao;
import uk.co.bbr.services.contests.dao.ContestEventTestPieceDao;
import uk.co.bbr.services.contests.dao.ContestGroupAliasDao;
import uk.co.bbr.services.contests.dao.ContestGroupDao;
import uk.co.bbr.services.contests.dao.ContestResultDao;
import uk.co.bbr.services.contests.dao.ContestResultPieceDao;
import uk.co.bbr.services.contests.dao.ContestTagDao;
import uk.co.bbr.services.contests.dto.ContestEventSummaryDto;
import uk.co.bbr.services.contests.dto.ContestGroupDetailsDto;
import uk.co.bbr.services.contests.dto.ContestGroupYearDto;
import uk.co.bbr.services.contests.dto.ContestGroupYearsDetailsDto;
import uk.co.bbr.services.contests.dto.ContestGroupYearsDetailsYearDto;
import uk.co.bbr.services.contests.dto.GroupListDto;
import uk.co.bbr.services.contests.dto.GroupListGroupDto;
import uk.co.bbr.services.contests.repo.ContestEventRepository;
import uk.co.bbr.services.contests.repo.ContestGroupAliasRepository;
import uk.co.bbr.services.contests.repo.ContestGroupRepository;
import uk.co.bbr.services.contests.repo.ContestResultPieceRepository;
import uk.co.bbr.services.contests.repo.ContestResultRepository;
import uk.co.bbr.services.contests.types.ContestGroupType;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.web.security.annotations.IsBbrAdmin;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContestGroupServiceImpl implements ContestGroupService, SlugTools {

    private final ContestGroupRepository contestGroupRepository;
    private final ContestEventRepository contestEventRepository;
    private final ContestGroupAliasRepository contestGroupAliasRepository;
    private final ContestResultPieceRepository contestResultPieceRepository;
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
            contestGroup.setCreatedBy(this.securityService.getCurrentUsername());
            contestGroup.setUpdated(LocalDateTime.now());
            contestGroup.setUpdatedBy(this.securityService.getCurrentUsername());
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
        group.setUpdatedBy(this.securityService.getCurrentUsername());
        return this.contestGroupRepository.saveAndFlush(group);
    }

    @Override
    public Optional<ContestGroupAliasDao> aliasExists(ContestGroupDao group, String name) {
        String searchName = group.simplifyContestName(name);
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
            previousName.setCreatedBy(this.securityService.getCurrentUsername());
            previousName.setUpdated(LocalDateTime.now());
            previousName.setUpdatedBy(this.securityService.getCurrentUsername());
        }
        return this.contestGroupAliasRepository.saveAndFlush(previousName);
    }

    @Override
    public Optional<ContestGroupDao> fetchBySlug(String groupSlug) {
        return this.contestGroupRepository.fetchBySlug(groupSlug.toUpperCase());
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

    @Override
    public ContestGroupDao addGroupTag(ContestGroupDao group, ContestTagDao tag) {
        group.getTags().add(tag);
        System.out.println("Linking group " + group.getId() + " [" + group.getName() + "] with tag " + tag.getId()+ " [" + tag.getName() + "]");
        return this.update(group);
    }

    @Override
    public ContestGroupDetailsDto fetchDetailBySlug(String groupSlug) {
        Optional<ContestGroupDao> contestGroup = this.contestGroupRepository.fetchBySlug(groupSlug.toUpperCase());
        if (contestGroup.isEmpty()) {
            throw new NotFoundException("Group with slug " + groupSlug + " not found");
        }

        List<ContestDao> activeContests = this.contestGroupRepository.fetchActiveContestsForGroup(contestGroup.get().getId());
        List<ContestDao> oldContests = this.contestGroupRepository.fetchOldContestsForGroup(contestGroup.get().getId());

        ContestGroupDetailsDto contestGroupDetails = new ContestGroupDetailsDto(contestGroup.get(), activeContests, oldContests);
        return contestGroupDetails;
    }

    @Override
    public ContestGroupYearsDetailsDto fetchYearsBySlug(String groupSlug) {
        Optional<ContestGroupDao> contestGroup = this.contestGroupRepository.fetchBySlug(groupSlug.toUpperCase());
        if (contestGroup.isEmpty()) {
            throw new NotFoundException("Group with slug " + groupSlug + " not found");
        }

        List<ContestEventDao> events = this.contestGroupRepository.fetchEventsForGroupOrderByEventDate(contestGroup.get().getId());
        Hashtable<String, Integer> yearCounts = new Hashtable<>();
        for (ContestEventDao event : events) {
            String year = "" + event.getEventDate().getYear();
            if (yearCounts.keySet().contains(year)) {
                yearCounts.put(year, yearCounts.get(year) + 1);
            } else {
                yearCounts.put(year, 1);
            }
        }

        List<ContestGroupYearsDetailsYearDto> displayYears = new ArrayList<>();
        for (String eachYearKey : yearCounts.keySet().stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList())) {
            displayYears.add(new ContestGroupYearsDetailsYearDto(eachYearKey, yearCounts.get(eachYearKey)));
        }

        ContestGroupYearsDetailsDto contestGroupDetails = new ContestGroupYearsDetailsDto(contestGroup.get(), displayYears);
        return contestGroupDetails;
    }

    @Override
    public ContestGroupYearDto fetchEventsByGroupSlugAndYear(String groupSlug, Integer year) {
        Optional<ContestGroupDao> contestGroup = this.contestGroupRepository.fetchBySlug(groupSlug.toUpperCase());
        if (contestGroup.isEmpty()) {
            throw new NotFoundException("Group with slug " + groupSlug + " not found");
        }
        List<ContestEventDao> eventsForYear = this.contestGroupRepository.selectByGroupSlugAndYear(contestGroup.get().getId(), year);

        Integer nextYear = null;
        List<ContestEventDao> nextEvent = this.contestGroupRepository.selectNextEventByGroupSlugAndYear(contestGroup.get().getId(), year, PageRequest.of(0, 1));
        if (nextEvent != null && !nextEvent.isEmpty()) {
            nextYear = nextEvent.get(0).getEventDate().getYear();
        }

        Integer previousYear = null;
        List<ContestEventDao> previousEvent = this.contestGroupRepository.selectPreviousEventByGroupSlugAndYear(contestGroup.get().getId(), year, PageRequest.of(0, 1));
        if (previousEvent != null && !previousEvent.isEmpty()) {
            previousYear = previousEvent.get(0).getEventDate().getYear();
        }

        List<ContestEventSummaryDto> contestEvents = new ArrayList<>();
        for (ContestEventDao event : eventsForYear) {
            List<ContestResultDao> winningBands = this.contestEventRepository.fetchWinningBands(event.getId());

            List<ContestEventTestPieceDao> contestTestPieces = this.contestEventRepository.fetchTestPieces(event.getId());
            List<PieceDao> testPieces = new ArrayList<>();
            for (ContestEventTestPieceDao eachSetPiece : contestTestPieces) {
                testPieces.add(eachSetPiece.getPiece());
            }

            if (testPieces.isEmpty()) {
                for (ContestResultDao eachResult : winningBands) {
                    List<ContestResultPieceDao> pieces = this.contestResultPieceRepository.fetchForResult(eachResult.getId());
                    for (ContestResultPieceDao eachPiece : pieces) {
                        testPieces.add(eachPiece.getPiece());
                    }
                }
            }
            contestEvents.add(new ContestEventSummaryDto(event, winningBands, testPieces));
        }

        return new ContestGroupYearDto(contestGroup.get(), year, contestEvents, nextYear, previousYear);
    }


}
