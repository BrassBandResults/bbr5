package uk.co.bbr.services.groups;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.dao.ContestEventTestPieceDao;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.events.dao.ContestResultPieceDao;
import uk.co.bbr.services.events.dto.ContestEventSummaryDto;
import uk.co.bbr.services.events.dto.GroupListDto;
import uk.co.bbr.services.events.repo.ContestEventRepository;
import uk.co.bbr.services.events.repo.ContestResultPieceRepository;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.groups.dao.ContestGroupAliasDao;
import uk.co.bbr.services.groups.dao.ContestGroupDao;
import uk.co.bbr.services.groups.dto.ContestGroupDetailsDto;
import uk.co.bbr.services.groups.dto.ContestGroupYearDto;
import uk.co.bbr.services.groups.dto.ContestGroupYearsDetailsDto;
import uk.co.bbr.services.groups.dto.ContestGroupYearsDetailsYearDto;
import uk.co.bbr.services.groups.dto.WhitFridayOverallBandResultDto;
import uk.co.bbr.services.groups.dto.WhitFridayOverallResultsDto;
import uk.co.bbr.services.groups.repo.ContestGroupAliasRepository;
import uk.co.bbr.services.groups.repo.ContestGroupRepository;
import uk.co.bbr.services.groups.sql.GroupSql;
import uk.co.bbr.services.groups.sql.WhitFridaySql;
import uk.co.bbr.services.groups.sql.dao.ContestListSqlDto;
import uk.co.bbr.services.groups.sql.dao.GroupListSqlDto;
import uk.co.bbr.services.groups.sql.dao.WhitFridayResultSqlDto;
import uk.co.bbr.services.groups.types.ContestGroupType;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.tags.dao.ContestTagDao;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final EntityManager entityManager;

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
    @IsBbrMember
    public ContestGroupAliasDao createAlias(ContestGroupDao group, ContestGroupAliasDao alias) {
        return this.createAlias(group, alias, false);
    }

    @Override
    @IsBbrMember
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
        List<GroupListSqlDto> groups;

        if (prefix.equalsIgnoreCase("ALL")) {
            groups = GroupSql.findAllForList(this.entityManager);
        } else {
            if (prefix.strip().length() != 1) {
                throw new UnsupportedOperationException("Prefix must be a single character");
            }
            groups = GroupSql.findByPrefixForList(this.entityManager, prefix.strip().toUpperCase());
        }

        List<ContestGroupDao> groupsToReturn = new ArrayList<>();
        for (GroupListSqlDto eachGroup : groups) {
            groupsToReturn.add(eachGroup.asGroup());
        }

        long allGroupsCount = this.contestGroupRepository.count();

        return new GroupListDto(groupsToReturn.size(), allGroupsCount, prefix, groupsToReturn);
    }

    @Override
    public GroupListDto listUnusedGroups() {
        List<GroupListSqlDto> groups = GroupSql.findUnusedGroupsForList(this.entityManager);

        List<ContestGroupDao> groupsToReturn = new ArrayList<>();
        for (GroupListSqlDto eachGroup : groups) {
            groupsToReturn.add(eachGroup.asGroup());
        }

        long allGroupsCount = this.contestGroupRepository.count();

        return new GroupListDto(groupsToReturn.size(), allGroupsCount, "UNUSED", groupsToReturn);
    }

    @Override
    @IsBbrMember
    public ContestGroupDao addGroupTag(ContestGroupDao group, ContestTagDao tag) {
        group.getTags().add(tag);
        return this.update(group);
    }

    @Override
    public ContestGroupDetailsDto fetchDetail(ContestGroupDao contestGroup) {
        Optional<ContestGroupDao> matchingContestGroup = this.contestGroupRepository.fetchBySlug(contestGroup.getSlug().toUpperCase());
        if (matchingContestGroup.isEmpty()) {
            throw NotFoundException.groupNotFoundBySlug(contestGroup.getSlug());
        }

        List<ContestListSqlDto> contests = GroupSql.contestsForGroup(this.entityManager, matchingContestGroup.get().getSlug());
;
        List<ContestDao> activeContests = new ArrayList<>();
        List<ContestDao> oldContests = new ArrayList<>();

        for (ContestListSqlDto eachContest : contests) {
            if (eachContest.isExtinct()) {
                oldContests.add(eachContest.asContest());
            } else {
                activeContests.add(eachContest.asContest());
            }
        }

        return new ContestGroupDetailsDto(matchingContestGroup.get(), activeContests, oldContests);
    }

    @Override
    public ContestGroupYearsDetailsDto fetchYearsBySlug(String groupSlug) {
        Optional<ContestGroupDao> contestGroup = this.contestGroupRepository.fetchBySlug(groupSlug.toUpperCase());
        if (contestGroup.isEmpty()) {
            throw NotFoundException.groupNotFoundBySlug(groupSlug);
        }

        List<ContestEventDao> events = this.contestGroupRepository.fetchEventsForGroupOrderByEventDate(contestGroup.get().getId());
        Map<String, Integer> yearCounts = new HashMap<>();
        for (ContestEventDao event : events) {
            String year = String.valueOf(event.getEventDate().getYear());
            if (yearCounts.containsKey(year)) {
                yearCounts.put(year, yearCounts.get(year) + 1);
            } else {
                yearCounts.put(year, 1);
            }
        }

        List<ContestGroupYearsDetailsYearDto> displayYears = new ArrayList<>();
        for (String eachYearKey : yearCounts.keySet().stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList())) {
            displayYears.add(new ContestGroupYearsDetailsYearDto(eachYearKey, yearCounts.get(eachYearKey)));
        }

        return new ContestGroupYearsDetailsDto(contestGroup.get(), displayYears);
    }

    @Override
    public ContestGroupYearDto fetchEventsByGroupSlugAndYear(String groupSlug, Integer year) {
        Optional<ContestGroupDao> contestGroup = this.contestGroupRepository.fetchBySlug(groupSlug.toUpperCase());
        if (contestGroup.isEmpty()) {
            throw NotFoundException.groupNotFoundBySlug(groupSlug);
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

        String stringYear = year == null ? null : String.valueOf(year);
        String stringNextYear = nextYear == null ? null : String.valueOf(nextYear);
        String stringPreviousYear = previousYear == null ? null : String.valueOf(previousYear);
        return new ContestGroupYearDto(contestGroup.get(), stringYear, contestEvents, stringNextYear, stringPreviousYear);
    }

    @Override
    public List<ContestGroupAliasDao> fetchAliases(ContestGroupDao contestGroup) {
        return this.contestGroupAliasRepository.findByGroup(contestGroup.getId());
    }

    @Override
    public void delete(ContestGroupDao contestGroup) {
        List<ContestGroupAliasDao> aliases = this.contestGroupAliasRepository.findByGroup(contestGroup.getId());
        this.contestGroupAliasRepository.deleteAll(aliases);

        this.contestGroupRepository.delete(contestGroup);
    }

    @Override
    public WhitFridayOverallResultsDto fetchWhitFridayOverallResults(ContestGroupDao group, Integer year) {
        List<WhitFridayResultSqlDto> rawResults = WhitFridaySql.fetchWhitFridayResults(this.entityManager, group.getSlug(), year);

        List<WhitFridayOverallBandResultDto> bandList = new ArrayList<>();

        for (WhitFridayResultSqlDto eachResult : rawResults) {
            WhitFridayOverallBandResultDto matchingBand = this.findBandForSlug(bandList, eachResult.getBandSlug());
            if (matchingBand == null) {
                matchingBand = new WhitFridayOverallBandResultDto(eachResult.getBandName(), eachResult.getBandSlug(), eachResult.getRegionName(), eachResult.getRegionSlug(), eachResult.getRegionCountryCode());
                bandList.add(matchingBand);
            }
            matchingBand.addResult(eachResult.getPosition());
        }

        for (WhitFridayOverallBandResultDto eachBand : bandList) {
            eachBand.sortResults();
        }
        bandList = bandList.stream().filter(a -> a.getResults().size() >= 6).sorted(Comparator.comparing(WhitFridayOverallBandResultDto::getTotalResults)).collect(Collectors.toList());
        int position = 1;
        int previousPoints = 0;
        for (WhitFridayOverallBandResultDto eachRow : bandList) {
            int totalResults = eachRow.getTotalResults();
            if (previousPoints != totalResults) {
                eachRow.setPosition(position);
            }
            previousPoints = totalResults;
            position++;
        }

        return new WhitFridayOverallResultsDto(group, year, bandList);
    }

    @Override
    public WhitFridayOverallResultsDto fetchWhitFridayOverallResultsMedian(ContestGroupDao group, Integer year) {
        List<WhitFridayResultSqlDto> rawResults = WhitFridaySql.fetchWhitFridayResults(this.entityManager, group.getSlug(), year);

        List<WhitFridayOverallBandResultDto> bandList = new ArrayList<>();

        for (WhitFridayResultSqlDto eachResult : rawResults) {
            WhitFridayOverallBandResultDto matchingBand = this.findBandForSlug(bandList, eachResult.getBandSlug());
            if (matchingBand == null) {
                matchingBand = new WhitFridayOverallBandResultDto(eachResult.getBandName(), eachResult.getBandSlug(), eachResult.getRegionName(), eachResult.getRegionSlug(), eachResult.getRegionCountryCode());
                bandList.add(matchingBand);
            }
            matchingBand.addResult(eachResult.getPosition());
        }

        for (WhitFridayOverallBandResultDto eachBand : bandList) {
            eachBand.sortResults();
        }
        bandList = bandList.stream().filter(a -> a.getResults().size() >= 6).sorted(Comparator.comparing(WhitFridayOverallBandResultDto::getTotalResults)).collect(Collectors.toList());
        bandList.sort(new BandMedianResultsComparator());

        int previousPoints = 0;
        int position = 1;
        for (WhitFridayOverallBandResultDto eachRow : bandList) {
            int totalResults = eachRow.getMedianResult();
            if (previousPoints != totalResults) {
                eachRow.setPosition(position);
            }
            previousPoints = totalResults;
            position++;
        }

        return new WhitFridayOverallResultsDto(group, year, bandList);
    }

    private WhitFridayOverallBandResultDto findBandForSlug(List<WhitFridayOverallBandResultDto> bandList, String bandSlug) {
        for (WhitFridayOverallBandResultDto eachBand : bandList) {
            if (eachBand.getBandSlug().equals(bandSlug)) {
                return eachBand;
            }
       }
        return null;
    }
}
