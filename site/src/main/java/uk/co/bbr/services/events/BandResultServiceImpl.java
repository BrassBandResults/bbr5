package uk.co.bbr.services.events;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.types.ResultSetCategory;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.sql.ContestResultSql;
import uk.co.bbr.services.contests.sql.dto.BandResultSqlDto;
import uk.co.bbr.services.contests.sql.dto.ContestWinsSqlDto;
import uk.co.bbr.services.contests.sql.dto.EventPieceSqlDto;
import uk.co.bbr.services.contests.sql.dto.ResultPieceSqlDto;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.dao.ContestEventTestPieceDao;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.events.dao.ContestResultPieceDao;
import uk.co.bbr.services.events.dto.ResultDetailsDto;
import uk.co.bbr.services.events.types.ResultPositionType;
import uk.co.bbr.services.groups.dao.ContestGroupDao;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.tags.dao.ContestTagDao;
import uk.co.bbr.services.tags.repo.ContestTagRepository;
import uk.co.bbr.services.tags.sql.ContestTagSql;
import uk.co.bbr.services.tags.sql.dto.ContestTagSqlDto;

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
            // we've only asked for future results, is this result before tomorrow?
            if (ResultSetCategory.FUTURE.equals(category) && eachSqlResult.getEventDate().isBefore(tomorrow)) {
                continue;
            }
            // we've only asked for past results, is this result in the future?
            if (ResultSetCategory.PAST.equals(category) && eachSqlResult.getEventDate().isAfter(today)) {
                continue;
            }

            ContestResultDao eachResult = eachSqlResult.toContestResultDao();
            contestSlugs.add(eachSqlResult.getContestSlug());

            if (eachResult.getContestEvent().getContest().getContestGroup() != null) {
                groupSlugs.add(eachResult.getContestEvent().getContest().getContestGroup().getSlug());
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

        // add tags to results
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

        // current champions
        LocalDate thirteenMonthsAgo = LocalDate.now().minus(13, ChronoUnit.MONTHS);
        List<ContestResultDao> currentChampions = allResults.stream()
                .filter(r -> r.getResultPositionType().equals(ResultPositionType.RESULT))
                .filter(r -> r.getPosition() != null)
                .filter(r -> r.getPosition() == 1)
                .filter(p -> p.getContestEvent().getEventDate().isAfter(thirteenMonthsAgo))
                .toList();

        List<ContestResultDao> filteredChampions = this.removeWinsWithLaterResult(currentChampions);

        return new ResultDetailsDto(bandResults, whitResults, allResults, filteredChampions);
    }

    private List<ContestResultDao>  removeWinsWithLaterResult(List<ContestResultDao> currentChampions) {
        List<ContestResultDao> filteredList = new ArrayList<>();
        LocalDate tenMonthsAgo = LocalDate.now().minus(10, ChronoUnit.MONTHS);

        for (ContestResultDao eachResult : currentChampions) {
            if (eachResult.getContestEvent().getEventDate().isAfter(tenMonthsAgo)) {
                filteredList.add(eachResult);
                continue;
            }

            List<ContestWinsSqlDto> moreRecentResult = ContestResultSql.selectMoreRecentResult(this.entityManager, eachResult);
            if (moreRecentResult.isEmpty()) {
                filteredList.add(eachResult);
            }
        }
        return filteredList;
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

        List<ContestResultDao> currentChampions = new ArrayList<>();
        return new ResultDetailsDto(filteredList, returnData.getBandWhitResults(), returnData.getBandAllResults(), currentChampions);
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

        List<ContestResultDao> currentChampions = new ArrayList<>();
        return new ResultDetailsDto(filteredList, returnData.getBandWhitResults(), returnData.getBandAllResults(), currentChampions);
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

        List<ContestResultDao> currentChampions = new ArrayList<>();
        return new ResultDetailsDto(filteredList, returnData.getBandWhitResults(), returnData.getBandAllResults(), currentChampions);
    }
}
