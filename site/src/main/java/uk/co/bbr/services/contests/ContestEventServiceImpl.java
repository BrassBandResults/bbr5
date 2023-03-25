package uk.co.bbr.services.contests;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.contests.dao.ContestAdjudicatorDao;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dao.ContestEventDao;
import uk.co.bbr.services.contests.dao.ContestEventTestPieceDao;
import uk.co.bbr.services.contests.repo.ContestAdjudicatorRepository;
import uk.co.bbr.services.contests.repo.ContestEventRepository;
import uk.co.bbr.services.contests.repo.ContestEventTestPieceRepository;
import uk.co.bbr.services.contests.types.ContestEventDateResolution;
import uk.co.bbr.services.contests.types.TestPieceAndOr;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.web.security.annotations.IsBbrAdmin;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContestEventServiceImpl implements ContestEventService {

    private final SecurityService securityService;
    private final ContestEventRepository contestEventRepository;
    private final ContestEventTestPieceRepository contestTestPieceRepository;
    private final ContestAdjudicatorRepository contestAdjudicatorRepository;

    @Override
    @IsBbrMember
    public ContestEventDao create(ContestDao contest, LocalDate eventDate) {
        ContestEventDao contestEvent = new ContestEventDao();
        contestEvent.setEventDate(eventDate);

        return this.create(contest, contestEvent);
    }

    @Override
    @IsBbrMember
    public ContestEventDao create(ContestDao contest, ContestEventDao event) {
        return this.create(contest, event, false);
    }

    @Override
    @IsBbrAdmin
    public ContestEventDao migrate(ContestDao contest, ContestEventDao contestEvent) {
        return this.create(contest, contestEvent, true);
    }

    @Override
    public ContestEventDao update(ContestEventDao event) {
       this.validateMandatory(event.getContest(), event);

        event.setUpdated(LocalDateTime.now());
        event.setUpdatedBy(this.securityService.getCurrentUsername());

        return this.contestEventRepository.saveAndFlush(event);
    }

    private ContestEventDao create(ContestDao contest, ContestEventDao event, boolean migrating) {
        this.validateMandatory(contest, event);

        if (!migrating) {
            event.setCreated(LocalDateTime.now());
            event.setCreatedBy(this.securityService.getCurrentUsername());
            event.setUpdated(LocalDateTime.now());
            event.setUpdatedBy(this.securityService.getCurrentUsername());
        }
        return this.contestEventRepository.saveAndFlush(event);
    }

    private void validateMandatory(ContestDao contest, ContestEventDao event) {
        if (contest != null) {
            event.setContest(contest);

            // default in contest type if not specified
            if (event.getContestType() == null) {
                event.setContestType(contest.getDefaultContestType());
            }

            // default in name if not specified
            if (event.getName() == null) {
                event.setName(contest.getName());
            }
        }

        // default in date resolution if not specified
        if (event.getEventDateResolution() == null) {
            event.setEventDateResolution(ContestEventDateResolution.EXACT_DATE);
        }

        if (event.getOriginalOwner() == null) {
            event.setOriginalOwner(this.securityService.getCurrentUsername());
        }
    }

    @Override
    public List<ContestAdjudicatorDao> addAdjudicator(ContestEventDao event, PersonDao adjudicator) {

        ContestAdjudicatorDao newAdjudicator = new ContestAdjudicatorDao();
        newAdjudicator.setAdjudicator(adjudicator);
        newAdjudicator.setName(adjudicator.getName());
        newAdjudicator.setContestEvent(event);

        newAdjudicator.setCreated(LocalDateTime.now());
        newAdjudicator.setCreatedBy(this.securityService.getCurrentUsername());
        newAdjudicator.setUpdated(LocalDateTime.now());
        newAdjudicator.setUpdatedBy(this.securityService.getCurrentUsername());

        this.contestAdjudicatorRepository.saveAndFlush(newAdjudicator);

        return this.fetchAdjudicators(event);
    }

    @Override
    public List<ContestAdjudicatorDao> fetchAdjudicators(ContestEventDao event) {
        return this.contestAdjudicatorRepository.fetchForEvent(event.getId());
    }

    @Override
    public ContestEventDao fetch(ContestDao contest, LocalDate eventDate) {
        return this.contestEventRepository.fetchByContestAndDate(contest.getId(), eventDate);
    }

    @Override
    public ContestEventTestPieceDao addTestPieceToContest(ContestEventDao event, ContestEventTestPieceDao testPiece) {
        testPiece.setContestEvent(event);
        testPiece.setCreated(LocalDateTime.now());
        testPiece.setCreatedBy(this.securityService.getCurrentUsername());
        testPiece.setUpdated(LocalDateTime.now());
        testPiece.setUpdatedBy(this.securityService.getCurrentUsername());
        return this.contestTestPieceRepository.saveAndFlush(testPiece);
    }

    @Override
    public ContestEventTestPieceDao addTestPieceToContest(ContestEventDao event, PieceDao testPiece) {
        ContestEventTestPieceDao newTestPiece = new ContestEventTestPieceDao();
        newTestPiece.setPiece(testPiece);
        return this.addTestPieceToContest(event, newTestPiece);
    }

    @Override
    public ContestEventTestPieceDao addTestPieceToContest(ContestEventDao event, PieceDao testPiece, TestPieceAndOr andOr) {
        ContestEventTestPieceDao newTestPiece = new ContestEventTestPieceDao();
        newTestPiece.setPiece(testPiece);
        newTestPiece.setAndOr(andOr);
        return this.addTestPieceToContest(event, newTestPiece);
    }

    @Override
    public List<ContestEventTestPieceDao> listTestPieces(ContestEventDao event) {
        return this.contestTestPieceRepository.fetchForEvent(event.getId());
    }


}
