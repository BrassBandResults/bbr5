package uk.co.bbr.services.contests;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.contests.dao.ContestAdjudicatorDao;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dao.ContestEventDao;
import uk.co.bbr.services.contests.dao.ContestGroupDao;
import uk.co.bbr.services.contests.dao.ContestTestPieceDao;
import uk.co.bbr.services.contests.repo.ContestAdjudicatorRepository;
import uk.co.bbr.services.contests.repo.ContestEventRepository;
import uk.co.bbr.services.contests.repo.ContestGroupRepository;
import uk.co.bbr.services.contests.repo.ContestTestPieceRepository;
import uk.co.bbr.services.contests.types.ContestEventDateResolution;
import uk.co.bbr.services.contests.types.ContestGroupType;
import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContestEventServiceImpl implements ContestEventService {

    private final SecurityService securityService;
    private final ContestEventRepository contestEventRepository;
    private final ContestTestPieceRepository contestTestPieceRepository;
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
        event.setContest(contest);

        // default in contest type if not specified
        if (event.getContestType() == null) {
            event.setContestType(contest.getDefaultContestType());
        }

        // default in date resolution if not specified
        if (event.getDateResolution() == null) {
            event.setDateResolution(ContestEventDateResolution.EXACT_DATE);
        }

        // default in name if not specified
        if (event.getName() == null) {
            event.setName(contest.getName());
        }

        if (event.getOriginalOwner() == null) {
            event.setOriginalOwner(this.securityService.getCurrentUsername());
        }

        event.setCreated(LocalDateTime.now());
        event.setCreatedBy(this.securityService.getCurrentUser());
        event.setUpdated(LocalDateTime.now());
        event.setUpdatedBy(this.securityService.getCurrentUser());
        return this.contestEventRepository.saveAndFlush(event);
    }

    @Override
    public List<ContestAdjudicatorDao> addAdjudicator(ContestEventDao event, PersonDao adjudicator) {

        ContestAdjudicatorDao newAdjudicator = new ContestAdjudicatorDao();
        newAdjudicator.setAdjudicator(adjudicator);
        newAdjudicator.setName(adjudicator.getName());
        newAdjudicator.setContestEvent(event);

        newAdjudicator.setCreated(LocalDateTime.now());
        newAdjudicator.setCreatedBy(this.securityService.getCurrentUser());
        newAdjudicator.setUpdated(LocalDateTime.now());
        newAdjudicator.setUpdatedBy(this.securityService.getCurrentUser());

        this.contestAdjudicatorRepository.saveAndFlush(newAdjudicator);

        return this.fetchAdjudicators(event);
    }

    @Override
    public List<ContestAdjudicatorDao> fetchAdjudicators(ContestEventDao event) {
        return this.contestAdjudicatorRepository.fetchForEvent(event.getId());
    }

    @Override
    public ContestEventDao fetch(ContestDao contest, LocalDate eventDate) {
        return this.contestEventRepository.findByContestAndDate(contest.getId(), eventDate);
    }

    @Override
    public ContestTestPieceDao addTestPieceToContest(ContestEventDao event, ContestTestPieceDao testPiece) {
        testPiece.setContestEvent(event);
        testPiece.setCreated(LocalDateTime.now());
        testPiece.setCreatedBy(this.securityService.getCurrentUser());
        testPiece.setUpdated(LocalDateTime.now());
        testPiece.setUpdatedBy(this.securityService.getCurrentUser());
        return this.contestTestPieceRepository.saveAndFlush(testPiece);
    }

    @Override
    public List<ContestTestPieceDao> listTestPieces(ContestEventDao event) {
        return this.contestTestPieceRepository.fetchForEvent(event.getId());
    }


}
