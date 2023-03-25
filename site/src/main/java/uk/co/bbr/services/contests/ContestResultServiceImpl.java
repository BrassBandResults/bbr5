package uk.co.bbr.services.contests;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.dto.BandDetailsDto;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dao.ContestEventDao;
import uk.co.bbr.services.contests.dao.ContestResultDao;
import uk.co.bbr.services.contests.dao.ContestResultPieceDao;
import uk.co.bbr.services.contests.repo.ContestResultPieceRepository;
import uk.co.bbr.services.contests.repo.ContestResultRepository;
import uk.co.bbr.services.contests.sql.ContestResultSql;
import uk.co.bbr.services.contests.sql.dto.BandEventPiecesSqlDto;
import uk.co.bbr.services.contests.sql.dto.BandResultSqlDto;
import uk.co.bbr.services.contests.sql.dto.BandResultsPiecesSqlDto;
import uk.co.bbr.services.contests.types.ContestEventDateResolution;
import uk.co.bbr.services.contests.types.ResultPositionType;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContestResultServiceImpl implements ContestResultService {

    private final ContestResultRepository contestResultRepository;
    private final ContestResultPieceRepository contestResultPieceRepository;
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
    public ContestResultPieceDao addPieceToResult(ContestResultDao contestResult, ContestResultPieceDao contestResultTestPiece) {
        contestResultTestPiece.setContestResult(contestResult);

        contestResultTestPiece.setCreated(LocalDateTime.now());
        contestResultTestPiece.setCreatedBy(this.securityService.getCurrentUsername());
        contestResultTestPiece.setUpdated(LocalDateTime.now());
        contestResultTestPiece.setUpdatedBy(this.securityService.getCurrentUsername());

        return this.contestResultPieceRepository.saveAndFlush(contestResultTestPiece);
    }

    @Override
    public ContestResultPieceDao addPieceToResult(ContestResultDao contestResult, PieceDao piece) {
        ContestResultPieceDao newPiece = new ContestResultPieceDao();
        newPiece.setPiece(piece);
        return this.addPieceToResult(contestResult, newPiece);
    }

    @Override
    public BandDetailsDto findResultsForBand(BandDao band) {

        List<BandResultSqlDto> bandResultsSql = ContestResultSql.selectBandResults(this.entityManager, band.getId());
        BandResultsPiecesSqlDto resultPiecesSql = ContestResultSql.selectBandResultPerformances(this.entityManager, band.getId());
        BandEventPiecesSqlDto eventPiecesSql = ContestResultSql.selectBandEventPieces(this.entityManager, band.getId());

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
            if (eachSqlResult.getResultPosition() != null) {
                eachResult.setPosition(eachSqlResult.getResultPosition().toString());
            }
            eachResult.setResultPositionType(ResultPositionType.fromCode(eachSqlResult.getResultPositionType()));
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

            resultPiecesSql.populateResultPieces(eachResult);
            eventPiecesSql.populateEventPieces(eachResult.getContestEvent());

            if (eachResult.getContestEvent().getContest().getName().contains("Whit Friday")) {
                whitResults.add(eachResult);
            } else {
                bandResults.add(eachResult);
            }
        }


        return new BandDetailsDto(bandResults, whitResults);
    }

    @Override
    public ContestResultDao migrate(ContestEventDao event, ContestResultDao contestResult) {
        contestResult.setContestEvent(event);
        return this.contestResultRepository.saveAndFlush(contestResult);
    }
}
