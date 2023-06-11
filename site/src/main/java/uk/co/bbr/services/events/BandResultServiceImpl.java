package uk.co.bbr.services.events;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.types.ResultSetCategory;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.sql.ContestResultSql;
import uk.co.bbr.services.contests.sql.dto.BandResultSqlDto;
import uk.co.bbr.services.contests.sql.dto.EventPieceSqlDto;
import uk.co.bbr.services.contests.sql.dto.ResultPieceSqlDto;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.dao.ContestEventTestPieceDao;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.events.dao.ContestResultPieceDao;
import uk.co.bbr.services.events.dto.ResultDetailsDto;
import uk.co.bbr.services.events.types.ContestEventDateResolution;
import uk.co.bbr.services.events.types.ResultPositionType;
import uk.co.bbr.services.groups.dao.ContestGroupDao;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.tags.dao.ContestTagDao;
import uk.co.bbr.services.tags.repo.ContestTagRepository;
import uk.co.bbr.services.tags.sql.ContestTagSql;
import uk.co.bbr.services.tags.sql.dto.ContestTagSqlDto;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BandResultServiceImpl implements BandResultService {

    private final ContestTagRepository contestTagRepository;
    private final EntityManager entityManager;

    @Override
    public ResultDetailsDto findResultsForBand(BandDao band, ResultSetCategory category) {
        List<BandResultSqlDto> bandResultsSql = ContestResultSql.selectBandResults(this.entityManager, band.getId());
        List<ResultPieceSqlDto> resultPiecesSql = ContestResultSql.selectBandResultPerformances(this.entityManager, band.getId());
        List<EventPieceSqlDto> eventPiecesSql = ContestResultSql.selectBandEventPieces(this.entityManager, band.getId());

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plus(1, ChronoUnit.DAYS);

        // combine
        List<ContestResultDao> bandResults = new ArrayList<>();
        List<ContestResultDao> whitResults = new ArrayList<>();
        List<ContestResultDao> allResults = new ArrayList<>();
        Set<String> contestSlugs = new HashSet<>();
        Set<String> groupSlugs = new HashSet<>();

        for (BandResultSqlDto eachSqlResult : bandResultsSql) {
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
            eachResult.setBandName(eachSqlResult.getBandName());
            eachResult.setDraw(eachSqlResult.getDraw());

            if (eachSqlResult.getConductor1Slug() != null) {
                eachResult.setConductor(new PersonDao());
                eachResult.getConductor().setSlug(eachSqlResult.getConductor1Slug());
                eachResult.getConductor().setFirstNames(eachSqlResult.getConductor1FirstNames());
                eachResult.getConductor().setSurname(eachSqlResult.getConductor1Surname());
            }

            if (eachSqlResult.getConductor2Slug() != null) {
                eachResult.setConductorSecond(new PersonDao());
                eachResult.getConductorSecond().setSlug(eachSqlResult.getConductor2Slug());
                eachResult.getConductorSecond().setFirstNames(eachSqlResult.getConductor2FirstNames());
                eachResult.getConductorSecond().setSurname(eachSqlResult.getConductor2Surname());
            }

            if (eachSqlResult.getConductor3Slug() != null) {
                eachResult.setConductorThird(new PersonDao());
                eachResult.getConductorThird().setSlug(eachSqlResult.getConductor3Slug());
                eachResult.getConductorThird().setFirstNames(eachSqlResult.getConductor3FirstNames());
                eachResult.getConductorThird().setSurname(eachSqlResult.getConductor3Surname());
            }

            this.populateResultPieces(resultPiecesSql, eachResult);
            this.populateEventPieces(eventPiecesSql, eachResult.getContestEvent());

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


        return new ResultDetailsDto(bandResults, whitResults, allResults);
    }

    private void populateResultPieces(List<ResultPieceSqlDto> resultPieces, ContestResultDao result) {
        result.setPieces(new ArrayList<>());
        for (ResultPieceSqlDto eachResultPiece : resultPieces) {
            if (eachResultPiece.getContestResultId().longValue() == result.getId()) {

                PieceDao piece = new PieceDao();
                piece.setSlug(eachResultPiece.getPieceSlug());
                piece.setName(eachResultPiece.getPieceName());
                piece.setYear(eachResultPiece.getPieceYear());

                ContestResultPieceDao resultPiece = new ContestResultPieceDao();
                resultPiece.setPiece(piece);
                resultPiece.setContestResult(result);

                result.getPieces().add(resultPiece);
            }
        }
    }

    private void populateEventPieces(List<EventPieceSqlDto> eventPieces, ContestEventDao event) {
        event.setPieces(new ArrayList<>());
        for (EventPieceSqlDto eachResultPiece : eventPieces) {
            if (eachResultPiece.getContestEventId().longValue() == event.getId()) {

                PieceDao piece = new PieceDao();
                piece.setSlug(eachResultPiece.getPieceSlug());
                piece.setName(eachResultPiece.getPieceName());
                piece.setYear(eachResultPiece.getPieceYear());

                ContestEventTestPieceDao eventPiece = new ContestEventTestPieceDao();
                eventPiece.setPiece(piece);
                eventPiece.setContestEvent(event);

                event.getPieces().add(eventPiece);
            }
        }
    }

    @Override
    public ResultDetailsDto findResultsForBand(BandDao band, ResultSetCategory category, ContestDao contest) {
        ResultDetailsDto returnData = this.findResultsForBand(band, category);

        List<ContestResultDao> filteredList = new ArrayList<>();
        for (ContestResultDao eachResult : returnData.getBandNonWhitResults()) {
            if (eachResult.getContestEvent().getContest().getSlug().equals(contest.getSlug())) {
                filteredList.add(eachResult);
            }
        }

        return new ResultDetailsDto(filteredList, returnData.getBandWhitResults(), returnData.getBandAllResults());
    }

    @Override
    public ResultDetailsDto findResultsForBand(BandDao band, ResultSetCategory category, ContestGroupDao contestGroup) {
        ResultDetailsDto returnData = this.findResultsForBand(band, category);

        List<ContestResultDao> filteredList = new ArrayList<>();
        for (ContestResultDao eachResult : returnData.getBandNonWhitResults()) {
            if (eachResult.getContestEvent().getContest().getContestGroup() != null && eachResult.getContestEvent().getContest().getContestGroup().getSlug().equals(contestGroup.getSlug())) {
                filteredList.add(eachResult);
            }
        }

        return new ResultDetailsDto(filteredList, returnData.getBandWhitResults(), returnData.getBandAllResults());
    }

    @Override
    public ResultDetailsDto findResultsForBand(BandDao band, ResultSetCategory category, ContestTagDao contestTag) {
        List<ContestDao> contests = this.contestTagRepository.fetchContestsForTag(contestTag.getSlug());
        List<ContestGroupDao> groups = this.contestTagRepository.fetchGroupsForTag(contestTag.getSlug());

        ResultDetailsDto returnData = this.findResultsForBand(band, category);

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

        return new ResultDetailsDto(filteredList, returnData.getBandWhitResults(), returnData.getBandAllResults());
    }
}
