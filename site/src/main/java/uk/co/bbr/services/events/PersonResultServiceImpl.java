package uk.co.bbr.services.events;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.types.ResultSetCategory;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.sql.ContestResultSql;
import uk.co.bbr.services.contests.sql.dto.PersonConductingResultSqlDto;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.events.dto.ResultDetailsDto;
import uk.co.bbr.services.events.types.ContestEventDateResolution;
import uk.co.bbr.services.events.types.ResultAwardType;
import uk.co.bbr.services.events.types.ResultPositionType;
import uk.co.bbr.services.groups.dao.ContestGroupDao;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.tags.dao.ContestTagDao;
import uk.co.bbr.services.tags.repo.ContestTagRepository;
import uk.co.bbr.services.tags.sql.ContestTagSql;
import uk.co.bbr.services.tags.sql.dto.ContestTagSqlDto;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PersonResultServiceImpl implements PersonResultService {

    private final ContestTagRepository contestTagRepository;
    private final EntityManager entityManager;


    @Override
    @Cacheable(cacheNames = "resultsForConductor", key = "#person.slug", cacheManager = "caffeineCacheManager")
    public ResultDetailsDto findResultsForConductor(PersonDao person, ResultSetCategory category) {
        List<PersonConductingResultSqlDto> conductingResultsSql = ContestResultSql.selectPersonConductingResults(this.entityManager, person.getId());

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plus(1, ChronoUnit.DAYS);


        List<ContestResultDao> bandResults = new ArrayList<>();
        List<ContestResultDao> whitResults = new ArrayList<>();
        List<ContestResultDao> allResults = new ArrayList<>();        Set<String> contestSlugs = new HashSet<>();
        Set<String> groupSlugs = new HashSet<>();


        for (PersonConductingResultSqlDto eachSqlResult : conductingResultsSql) {
            if (ResultSetCategory.FUTURE.equals(category) && eachSqlResult.getEventDate().isBefore(tomorrow)) {
                continue;
            }
            if (ResultSetCategory.PAST.equals(category) && eachSqlResult.getEventDate().isAfter(today)) {
                continue;
            }

            ContestResultDao eachResult = new ContestResultDao();
            eachResult.setContestEvent(new ContestEventDao());
            eachResult.getContestEvent().setContest(new ContestDao());

            eachResult.setId(eachSqlResult.getContestResultId().longValue());
            eachResult.setBandName(eachSqlResult.getBandCompetedAs());

            eachResult.setBand(new BandDao());
            eachResult.getBand().setName(eachSqlResult.getBandName());
            eachResult.getBand().setSlug(eachSqlResult.getBandSlug());

            eachResult.getBand().setRegion(new RegionDao());
            eachResult.getBand().getRegion().setName(eachSqlResult.getRegionName());
            eachResult.getBand().getRegion().setCountryCode(eachSqlResult.getRegionCountryCode());

            eachResult.getContestEvent().setId(eachSqlResult.getContestEventId().longValue());

            eachResult.getContestEvent().setEventDate(eachSqlResult.getEventDate());
            eachResult.getContestEvent().setEventDateResolution(ContestEventDateResolution.fromCode(eachSqlResult.getEventDateResolution()));
            eachResult.getContestEvent().getContest().setSlug(eachSqlResult.getContestSlug());
            eachResult.getContestEvent().getContest().setName(eachSqlResult.getContestName());

            contestSlugs.add(eachSqlResult.getContestSlug());

            if (eachSqlResult.getGroupSlug() != null) {
                eachResult.getContestEvent().getContest().setContestGroup(new ContestGroupDao());
                eachResult.getContestEvent().getContest().getContestGroup().setName(eachSqlResult.getGroupName());
                eachResult.getContestEvent().getContest().getContestGroup().setSlug(eachSqlResult.getGroupSlug());

                groupSlugs.add(eachSqlResult.getGroupSlug());
            }

            if (eachSqlResult.getResultPosition() != null) {
                eachResult.setPosition(eachSqlResult.getResultPosition().toString());
            }
            eachResult.setResultPositionType(ResultPositionType.fromCode(eachSqlResult.getResultPositionType()));
            eachResult.setResultAward(ResultAwardType.fromCode(eachSqlResult.getResultAward()));
            eachResult.setPointsTotal(eachSqlResult.getPoints());
            eachResult.setDraw(eachSqlResult.getDraw());
            eachResult.setNotes(eachSqlResult.getResultNotes());

            if (eachResult.getContestEvent().getContest().getName().contains("Whit Friday")) {
                whitResults.add(eachResult);
                allResults.add(eachResult);
            } else {
                bandResults.add(eachResult);
                allResults.add(eachResult);
            }
        }

        List<ContestTagSqlDto> contestTags = ContestTagSql.selectTagsForContestSlugs(this.entityManager, contestSlugs);
        List<ContestTagSqlDto> groupTags = ContestTagSql.selectTagsForGroupSlugs(this.entityManager, groupSlugs);

        for (ContestResultDao eachResult : allResults) {
            String contestSlug = eachResult.getContestEvent().getContest().getSlug();
            String groupSlug = null;
            if (eachResult.getContestEvent().getContest().getContestGroup() != null) {
                groupSlug = eachResult.getContestEvent().getContest().getContestGroup().getSlug();
            }
            for (ContestTagSqlDto eachContestTag : contestTags) {
                if (eachContestTag.getContestSlug().equals(contestSlug)) {
                    eachResult.getTags().add(eachContestTag);
                }
            }
            if (groupSlug != null) {
                for (ContestTagSqlDto eachGroupTag : groupTags) {
                    if (eachGroupTag.getContestSlug().equals(groupSlug)) {
                        eachResult.getTags().add(eachGroupTag);
                    }
                }
            }
        }


        List<ContestResultDao> currentChampions = new ArrayList<>();
        return new ResultDetailsDto(bandResults, whitResults, allResults, currentChampions);
    }

    @Override
    public ResultDetailsDto findResultsForConductor(PersonDao person, ResultSetCategory category, ContestDao contest) {
        ResultDetailsDto returnData = this.findResultsForConductor(person, category);

        List<ContestResultDao> filteredList = new ArrayList<>();
        for (ContestResultDao eachResult : returnData.getBandNonWhitResults()) {
            if (eachResult.getContestEvent().getContest().getSlug().equals(contest.getSlug())) {
                filteredList.add(eachResult);
            }
        }

        List<ContestResultDao> currentChampions = new ArrayList<>();
        return new ResultDetailsDto(filteredList, returnData.getBandWhitResults(), returnData.getBandAllResults(), currentChampions);
    }

    @Override
    public ResultDetailsDto findResultsForConductor(PersonDao person, ResultSetCategory category, ContestGroupDao contestGroup) {
        ResultDetailsDto returnData = this.findResultsForConductor(person, category);

        List<ContestResultDao> filteredList = new ArrayList<>();
        for (ContestResultDao eachResult : returnData.getBandNonWhitResults()) {
            if (eachResult.getContestEvent().getContest().getContestGroup() != null && eachResult.getContestEvent().getContest().getContestGroup().getSlug().equals(contestGroup.getSlug())) {
                filteredList.add(eachResult);
            }
        }

        List<ContestResultDao> currentChampions = new ArrayList<>();
        return new ResultDetailsDto(filteredList, returnData.getBandWhitResults(), returnData.getBandAllResults(), currentChampions);
    }

    @Override
    public ResultDetailsDto findResultsForConductor(PersonDao person, ResultSetCategory category, ContestTagDao contestTag) {
        List<ContestDao> contests = this.contestTagRepository.fetchContestsForTag(contestTag.getSlug());
        List<ContestGroupDao> groups = this.contestTagRepository.fetchGroupsForTag(contestTag.getSlug());

        ResultDetailsDto returnData = this.findResultsForConductor(person, category);

        List<ContestResultDao> filteredList = new ArrayList<>();
        for (ContestResultDao eachResult : returnData.getBandNonWhitResults()) {
            for (ContestDao tagContest : contests) {
                if (eachResult.getContestEvent().getContest().getSlug().equals(tagContest.getSlug())) {
                    filteredList.add(eachResult);
                    break;
                }
            }

            for (ContestGroupDao tagGroup : groups) {
                if (eachResult.getContestEvent().getContest().getContestGroup() != null && eachResult.getContestEvent().getContest().getContestGroup().getSlug().equals(tagGroup.getSlug())) {
                    filteredList.add(eachResult);
                }
            }
        }

        List<ContestResultDao> currentChampions = new ArrayList<>();
        return new ResultDetailsDto(filteredList, returnData.getBandWhitResults(), returnData.getBandAllResults(), currentChampions);
    }
}
