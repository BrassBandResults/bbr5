package uk.co.bbr.services.contests;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.dto.BandDetailsDto;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dao.ContestEventDao;
import uk.co.bbr.services.contests.dao.ContestEventTestPieceDao;
import uk.co.bbr.services.contests.dao.ContestGroupDao;
import uk.co.bbr.services.contests.dao.ContestResultDao;
import uk.co.bbr.services.contests.dao.ContestResultPieceDao;
import uk.co.bbr.services.contests.dao.ContestTagDao;
import uk.co.bbr.services.contests.sql.dto.ContestWinsSqlDto;
import uk.co.bbr.services.contests.repo.ContestResultPieceRepository;
import uk.co.bbr.services.contests.repo.ContestResultRepository;
import uk.co.bbr.services.contests.repo.ContestTagRepository;
import uk.co.bbr.services.contests.sql.ContestResultSql;
import uk.co.bbr.services.contests.sql.dto.BandResultSqlDto;
import uk.co.bbr.services.contests.sql.dto.ContestResultPieceSqlDto;
import uk.co.bbr.services.contests.sql.dto.EventPieceSqlDto;
import uk.co.bbr.services.contests.sql.dto.PersonConductingResultSqlDto;
import uk.co.bbr.services.contests.sql.dto.ResultPieceSqlDto;
import uk.co.bbr.services.contests.types.ContestEventDateResolution;
import uk.co.bbr.services.contests.types.ResultPositionType;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.people.dto.ConductingDetailsDto;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ContestResultServiceImpl implements ContestResultService {

    private final ContestResultRepository contestResultRepository;
    private final ContestResultPieceRepository contestResultPieceRepository;
    private final ContestTagRepository contestTagRepository;
    private final SecurityService securityService;
    private final EntityManager entityManager;

    @Override
    @IsBbrMember
    public ContestResultDao addResult(ContestEventDao event, ContestResultDao result) {
        result.setContestEvent(event);

        ContestResultDao returnResult = null;
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
        return this.contestResultRepository.findAllForEvent(event.getId());
    }

    @Override
    @IsBbrMember
    public ContestResultPieceDao addPieceToResult(ContestResultDao contestResult, ContestResultPieceDao contestResultTestPiece) {
        contestResultTestPiece.setContestResult(contestResult);

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
    public BandDetailsDto findResultsForBand(BandDao band) {
        List<BandResultSqlDto> bandResultsSql = ContestResultSql.selectBandResults(this.entityManager, band.getId());
        List<ResultPieceSqlDto> resultPiecesSql = ContestResultSql.selectBandResultPerformances(this.entityManager, band.getId());
        List<EventPieceSqlDto> eventPiecesSql = ContestResultSql.selectBandEventPieces(this.entityManager, band.getId());

        // combine
        List<ContestResultDao> bandResults = new ArrayList<>();
        List<ContestResultDao> whitResults = new ArrayList<>();

        for (BandResultSqlDto eachSqlResult : bandResultsSql) {
            ContestResultDao eachResult = new ContestResultDao();
            eachResult.setContestEvent(new ContestEventDao());
            eachResult.getContestEvent().setContest(new ContestDao());

            eachResult.setId(eachSqlResult.getContestResultId().longValue());
            eachResult.getContestEvent().setId(eachSqlResult.getContestEventId().longValue());

            eachResult.getContestEvent().setEventDate(eachSqlResult.getEventDate());
            eachResult.getContestEvent().setEventDateResolution(ContestEventDateResolution.fromCode(eachSqlResult.getEventDateResolution()));
            eachResult.getContestEvent().getContest().setSlug(eachSqlResult.getContestSlug());
            eachResult.getContestEvent().getContest().setName(eachSqlResult.getContestName());

            if (eachSqlResult.getGroupSlug() != null) {
                eachResult.getContestEvent().getContest().setContestGroup(new ContestGroupDao());
                eachResult.getContestEvent().getContest().getContestGroup().setName(eachSqlResult.getGroupName());
                eachResult.getContestEvent().getContest().getContestGroup().setSlug(eachSqlResult.getGroupSlug());
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
            } else {
                bandResults.add(eachResult);
            }
        }


        return new BandDetailsDto(bandResults, whitResults);
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
    public BandDetailsDto findResultsForBand(BandDao band, ContestDao contest) {
        BandDetailsDto returnData = this.findResultsForBand(band);

        List<ContestResultDao> filteredList = new ArrayList<>();
        for (ContestResultDao eachResult : returnData.getBandResults()) {
            if (eachResult.getContestEvent().getContest().getSlug().equals(contest.getSlug())) {
                filteredList.add(eachResult);
            }
        }

        return new BandDetailsDto(filteredList, returnData.getBandWhitResults());
    }

    @Override
    public BandDetailsDto findResultsForBand(BandDao band, ContestGroupDao contestGroup) {
        BandDetailsDto returnData = this.findResultsForBand(band);

        List<ContestResultDao> filteredList = new ArrayList<>();
        for (ContestResultDao eachResult : returnData.getBandResults()) {
            if (eachResult.getContestEvent().getContest().getContestGroup() != null && eachResult.getContestEvent().getContest().getContestGroup().getSlug().equals(contestGroup.getSlug())) {
                filteredList.add(eachResult);
            }
        }

        return new BandDetailsDto(filteredList, returnData.getBandWhitResults());
    }

    @Override
    public BandDetailsDto findResultsForBand(BandDao band, ContestTagDao contestTag) {
        List<ContestDao> contests = this.contestTagRepository.fetchContestsForTag(contestTag.getSlug());
        List<ContestGroupDao> groups = this.contestTagRepository.fetchGroupsForTag(contestTag.getSlug());

        BandDetailsDto returnData = this.findResultsForBand(band);

        List<ContestResultDao> filteredList = new ArrayList<>();
        for (ContestResultDao eachResult : returnData.getBandResults()) {
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

        return new BandDetailsDto(filteredList, returnData.getBandWhitResults());
    }

    @Override
    public ConductingDetailsDto findResultsForConductor(PersonDao person) {
        List<ContestResultDao> bandResults = new ArrayList<>();
        List<ContestResultDao> whitResults = new ArrayList<>();

        List<PersonConductingResultSqlDto> conductingResultsSql = ContestResultSql.selectPersonConductingResults(this.entityManager, person.getId());

        for (PersonConductingResultSqlDto eachSqlResult : conductingResultsSql) {
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
            if (eachSqlResult.getResultPosition() != null) {
                eachResult.setPosition(eachSqlResult.getResultPosition().toString());
            }
            eachResult.setResultPositionType(ResultPositionType.fromCode(eachSqlResult.getResultPositionType()));
            eachResult.setPointsTotal(eachSqlResult.getPoints());
            eachResult.setDraw(eachSqlResult.getDraw());

            if (eachResult.getContestEvent().getContest().getName().contains("Whit Friday")) {
                whitResults.add(eachResult);
            } else {
                bandResults.add(eachResult);
            }
        }


        return new ConductingDetailsDto(bandResults, whitResults);
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
    @IsBbrMember
    public ContestResultDao migrate(ContestEventDao event, ContestResultDao contestResult) {
        contestResult.setContestEvent(event);
        return this.contestResultRepository.saveAndFlush(contestResult);
    }
}
