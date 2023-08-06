package uk.co.bbr.services.events;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dto.ContestStreakContainerDto;
import uk.co.bbr.services.contests.dto.ContestStreakDto;
import uk.co.bbr.services.contests.sql.ContestResultSql;
import uk.co.bbr.services.contests.sql.dto.ContestResultPieceSqlDto;
import uk.co.bbr.services.contests.sql.dto.ContestWinsSqlDto;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.events.dao.ContestResultPieceDao;
import uk.co.bbr.services.events.repo.ContestResultPieceRepository;
import uk.co.bbr.services.events.repo.ContestResultRepository;
import uk.co.bbr.services.events.sql.EventSql;
import uk.co.bbr.services.events.sql.ResultFilterSql;
import uk.co.bbr.services.events.sql.dto.ContestResultDrawPositionSqlDto;
import uk.co.bbr.services.events.sql.dto.EventResultSqlDto;
import uk.co.bbr.services.events.sql.dto.ResultPieceSqlDto;
import uk.co.bbr.services.events.types.ContestEventDateResolution;
import uk.co.bbr.services.events.types.ResultPositionType;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.dao.SiteUserDao;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ResultServiceImpl implements ResultService {

    private final ContestResultRepository contestResultRepository;
    private final ContestResultPieceRepository contestResultPieceRepository;
    private final BandService bandService;
    private final SecurityService securityService;
    private final EntityManager entityManager;

    @Override
    @IsBbrMember
    public ContestResultDao addResult(ContestEventDao event, ContestResultDao result) {
        result.setContestEvent(event);

        ContestResultDao returnResult;
        // is there an existing result for the same band?
        Optional<ContestResultDao> existingResult = this.contestResultRepository.fetchForEventAndBand(event.getId(), result.getBand().getId());
        if (existingResult.isPresent()) {
            ContestResultDao existingResultObject = existingResult.get();
            existingResultObject.populateFrom(result);
            existingResultObject.setUpdated(LocalDateTime.now());
            existingResultObject.setUpdatedBy(this.securityService.getCurrentUsername());
            returnResult = this.contestResultRepository.saveAndFlush(existingResultObject);
        } else {
            result.setCreated(LocalDateTime.now());
            result.setCreatedBy(this.securityService.getCurrentUsername());
            result.setUpdated(LocalDateTime.now());
            result.setUpdatedBy(this.securityService.getCurrentUsername());
            returnResult = this.contestResultRepository.saveAndFlush(result);
        }

        return returnResult;
    }

    @Override
    @IsBbrMember
    public ContestResultDao addResult(ContestEventDao event, String position, BandDao band, PersonDao conductor) {
        ContestResultDao newResult = new ContestResultDao();
        newResult.setPosition(position);
        newResult.setBand(band);
        newResult.setBandName(band.getName());
        newResult.setConductor(conductor);

        return this.addResult(event, newResult);
    }

    @Override
    public List<ContestResultDao> fetchForEvent(ContestEventDao event) {
        List<ContestResultDao> returnResults = new ArrayList<>();
        List<EventResultSqlDto> resultsSql = EventSql.selectEventResults(this.entityManager, event.getId());
        for (EventResultSqlDto eachResultSql : resultsSql) {
            returnResults.add(eachResultSql.toResult());
        }

        List<ResultPieceSqlDto> resultsPieces = EventSql.selectEventResultPieces(this.entityManager, event.getId());
        for (ResultPieceSqlDto eachResultPiece : resultsPieces) {
            for (ContestResultDao eachResult : returnResults) {
                if (eachResult.getId().equals(eachResultPiece.getResultId())) {
                    eachResult.getPieces().add(eachResultPiece.asResultPiece());
                    break;
                }
            }
        }

        return returnResults;
    }

    @Override
    @IsBbrMember
    public ContestResultPieceDao addPieceToResult(ContestResultDao contestResult, ContestResultPieceDao contestResultTestPiece) {
        contestResultTestPiece.setContestResult(contestResult);

        Integer maxOrdering = this.contestResultPieceRepository.fetchMaxOrdering(contestResult.getId());
        if (maxOrdering == null) {
            maxOrdering = 10;
        }
        contestResultTestPiece.setOrdering(maxOrdering + 2);

        contestResultTestPiece.setCreated(LocalDateTime.now());
        contestResultTestPiece.setCreatedBy(this.securityService.getCurrentUsername());
        contestResultTestPiece.setUpdated(LocalDateTime.now());
        contestResultTestPiece.setUpdatedBy(this.securityService.getCurrentUsername());

        return this.contestResultPieceRepository.saveAndFlush(contestResultTestPiece);
    }

    @Override
    @IsBbrMember
    public ContestResultPieceDao addPieceToResult(ContestResultDao contestResult, PieceDao piece) {
        ContestResultPieceDao newPiece = new ContestResultPieceDao();
        newPiece.setPiece(piece);
        return this.addPieceToResult(contestResult, newPiece);
    }

    @Override
    public ContestResultPieceDao addPieceToResult(ContestResultDao contestResult, PieceDao piece, String suffix) {
        ContestResultPieceDao newPiece = new ContestResultPieceDao();
        newPiece.setPiece(piece);
        newPiece.setSuffix(suffix);
        return this.addPieceToResult(contestResult, newPiece);
    }

    @Override
    public List<ContestResultPieceDao> fetchResultsWithOwnChoicePieces(ContestDao contest) {
        List<ContestResultPieceDao> returnData = new ArrayList<>();

        List<ContestResultPieceSqlDto> pieceResults = ContestResultSql.selectOwnChoiceUsedForContest(this.entityManager, contest.getId());
        for (ContestResultPieceSqlDto eachResult : pieceResults) {
            ContestResultPieceDao eachReturnPiece = new ContestResultPieceDao();
            eachReturnPiece.setPiece(new PieceDao());
            eachReturnPiece.setContestResult(new ContestResultDao());
            eachReturnPiece.getContestResult().setBand(new BandDao());
            eachReturnPiece.getContestResult().getBand().setRegion(new RegionDao());
            eachReturnPiece.getContestResult().setContestEvent(new ContestEventDao());
            eachReturnPiece.getContestResult().getContestEvent().setContest(new ContestDao());

            eachReturnPiece.getContestResult().getContestEvent().setEventDate(eachResult.getEventDate());
            eachReturnPiece.getContestResult().getContestEvent().setEventDateResolution(ContestEventDateResolution.fromCode(eachResult.getDateResolution()));
            eachReturnPiece.getContestResult().getContestEvent().getContest().setSlug(eachResult.getContestSlug());
            eachReturnPiece.getContestResult().setBandName(eachResult.getBandCompetedAs());
            eachReturnPiece.getContestResult().getBand().setSlug(eachResult.getBandSlug());
            eachReturnPiece.getContestResult().getBand().setName(eachResult.getBandName());
            eachReturnPiece.getPiece().setName(eachResult.getPieceName());
            eachReturnPiece.getPiece().setSlug(eachResult.getPieceSlug());
            eachReturnPiece.getPiece().setYear(eachResult.getPieceYear());
            eachReturnPiece.getContestResult().setPosition(eachResult.getPosition());
            eachReturnPiece.getContestResult().setResultPositionType(ResultPositionType.fromCode(eachResult.getPositionType()));
            eachReturnPiece.getContestResult().getBand().getRegion().setName(eachResult.getRegionName());
            eachReturnPiece.getContestResult().getBand().getRegion().setCountryCode(eachResult.getRegionCountryCode());

            returnData.add(eachReturnPiece);
        }

        return returnData;
    }

    @Override
    public int fetchCountOfOwnChoiceForContest(ContestDao contest) {
        return this.contestResultPieceRepository.fetchCountOfOwnChoiceForContest(contest.getId());
    }

    @Override
    public List<ContestWinsSqlDto> fetchWinsCounts(ContestDao contest) {
        return ContestResultSql.selectWinsForContest(this.entityManager, contest.getId());
    }

    @Override
    public Set<PersonDao> fetchBandConductors(BandDao band) {
        Set<PersonDao> conductors = new HashSet<>();

        if (band != null) {
            List<ContestResultDao> bandResults = this.contestResultRepository.findAllForBand(band.getId());

            for (ContestResultDao eachResult : bandResults) {
                if (eachResult.getConductor() != null) {
                    conductors.add(eachResult.getConductor());
                }
                if (eachResult.getConductorSecond() != null) {
                    conductors.add(eachResult.getConductorSecond());
                }
                if (eachResult.getConductorThird() != null) {
                    conductors.add(eachResult.getConductorThird());
                }
            }
        }

        return conductors;
    }

    @Override
    public List<ContestResultDao> fetchResultsForContestAndPosition(ContestDao contest, String position) {
        List<ContestResultDrawPositionSqlDto> results;
        switch (position) {
            case "W":
                results = ResultFilterSql.selectContestResultsForWithdrawn(this.entityManager, contest.getSlug());
                break;
            case "D":
                results = ResultFilterSql.selectContestResultsForDisqualified(this.entityManager, contest.getSlug());
                break;
            default:
                results = ResultFilterSql.selectContestResultsForPosition(this.entityManager, contest.getSlug(), position);
                break;
        }

        List<ContestResultDao> resultsToReturn = new ArrayList<>();
        for (ContestResultDrawPositionSqlDto eachSqlResult : results) {
            resultsToReturn.add(eachSqlResult.getResult());
        }

        return resultsToReturn;
    }

    @Override
    public List<ContestResultDao> fetchResultsForContestAndDraw(ContestDao contest, int draw) {
        List<ContestResultDrawPositionSqlDto> results = ResultFilterSql.selectContestResultsForDraw(this.entityManager, contest.getSlug(), draw);

        List<ContestResultDao> resultsToReturn = new ArrayList<>();
        for (ContestResultDrawPositionSqlDto eachSqlResult : results) {
            resultsToReturn.add(eachSqlResult.getResult());
        }

        return resultsToReturn;
    }

    @Override
    public ContestResultDao update(ContestResultDao result) {
        result.setUpdatedBy(this.securityService.getCurrentUsername());
        result.setUpdated(LocalDateTime.now());
        return this.contestResultRepository.saveAndFlush(result);
    }

    @Override
    public List<ContestStreakDto> fetchStreaksForContest(ContestDao contest) {
        Map<String, List<Integer>> streaksBandSlugToYear = this.fetchStreakData(contest);

        ContestStreakContainerDto streaks = new ContestStreakContainerDto();
        streaks.populate(streaksBandSlugToYear, this.bandService);
        return streaks.getStreaks();
    }

    @Override
    public Optional<ContestResultDao> fetchById(Long resultId) {
        return this.contestResultRepository.findById(resultId);
    }

    @Override
    public List<ContestResultPieceDao> listResultPieces(ContestResultDao result) {
        return this.contestResultPieceRepository.fetchForResult(result.getId());
    }

    @Override
    public Optional<ContestResultPieceDao> fetchResultPieceById(ContestResultDao contestResult, Long resultPieceId) {
        return this.contestResultPieceRepository.fetchForContestAndResultPieceId(contestResult.getId(), resultPieceId);
    }

    @Override
    public void removePiece(ContestEventDao contestEvent, ContestResultDao contestResult, ContestResultPieceDao contestResultPiece) {
        Optional<ContestResultPieceDao> matchingPiece = this.contestResultPieceRepository.fetchForContestAndResultPieceId(contestResult.getId(), contestResultPiece.getId());
        if (matchingPiece.isEmpty()) {
            throw NotFoundException.resultPieceNotFoundById();
        }

        this.contestResultPieceRepository.delete(matchingPiece.get());
    }

    @Override
    public void delete(ContestResultDao contestResult) {
        this.contestResultRepository.delete(contestResult);
    }

    @Override
    public void workOutCanEdit(ContestEventDao contestEvent, List<ContestResultDao> eventResults) {
        SiteUserDao currentUser = this.securityService.getCurrentUser();
        boolean canEditAnyResult = false;
        for (ContestResultDao result : eventResults){
            boolean canEditThisResult = false;
            if (currentUser == null) {
                continue;
            }

            LocalDate twoWeeksAgo = LocalDate.now().minus(15, ChronoUnit.DAYS);
            if (currentUser.isSuperuser() ||
                contestEvent.getEventDate().isAfter(twoWeeksAgo) ||
                result.getCreatedBy().equals(currentUser.getUsercode())) {
                canEditThisResult = true;
                canEditAnyResult = true;
            }

            result.setCanEdit(canEditThisResult);
        }

        contestEvent.setCanEdit(canEditAnyResult);
    }

    private Map<String, List<Integer>> fetchStreakData(ContestDao contest) {
        Map<String, List<Integer>> streaksBandSlugToYear = new HashMap<>();

        List<ContestResultDrawPositionSqlDto> wins = ResultFilterSql.selectContestResultsForPosition(this.entityManager, contest.getSlug(), "1");
        for (ContestResultDrawPositionSqlDto win : wins) {
            String currentBandSlug = win.getResult().getBand().getSlug();
            List<Integer> existingRecord = streaksBandSlugToYear.get(currentBandSlug);
            if (existingRecord == null) {
                existingRecord = new ArrayList<>();
            }
            existingRecord.add(win.getResult().getContestEvent().getEventDate().getYear());
            streaksBandSlugToYear.put(currentBandSlug, existingRecord);
        }
        return streaksBandSlugToYear;
    }

    @Override
    @IsBbrMember
    public ContestResultDao migrate(ContestEventDao event, ContestResultDao contestResult) {
        contestResult.setContestEvent(event);
        return this.contestResultRepository.saveAndFlush(contestResult);
    }
}
