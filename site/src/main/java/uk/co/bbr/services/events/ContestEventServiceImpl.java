package uk.co.bbr.services.events;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.events.dao.ContestAdjudicatorDao;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.dao.ContestEventTestPieceDao;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.events.repo.ContestAdjudicatorRepository;
import uk.co.bbr.services.events.repo.ContestEventRepository;
import uk.co.bbr.services.events.repo.ContestEventTestPieceRepository;
import uk.co.bbr.services.contests.repo.ContestRepository;
import uk.co.bbr.services.contests.sql.ContestResultSql;
import uk.co.bbr.services.contests.sql.dto.ContestEventResultSqlDto;
import uk.co.bbr.services.events.sql.EventSql;
import uk.co.bbr.services.events.sql.dto.EventResultSqlDto;
import uk.co.bbr.services.events.sql.dto.EventUpDownLeftRightSqlDto;
import uk.co.bbr.services.events.types.ContestEventDateResolution;
import uk.co.bbr.services.events.types.TestPieceAndOr;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.web.security.annotations.IsBbrAdmin;
import uk.co.bbr.web.security.annotations.IsBbrMember;
import uk.co.bbr.services.framework.DateTools;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContestEventServiceImpl implements ContestEventService {

    private final SecurityService securityService;
    private final ContestEventRepository contestEventRepository;
    private final ContestRepository contestRepository;
    private final ContestEventTestPieceRepository contestTestPieceRepository;
    private final ContestAdjudicatorRepository contestAdjudicatorRepository;
    private final EntityManager entityManager;

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
    @IsBbrMember
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

        if (event.getOwner() == null) {
            event.setOwner(this.securityService.getCurrentUsername());
        }
    }

    @Override
    @IsBbrMember
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
    @IsBbrMember
    public ContestEventTestPieceDao addTestPieceToContest(ContestEventDao event, ContestEventTestPieceDao testPiece) {
        testPiece.setContestEvent(event);
        testPiece.setCreated(LocalDateTime.now());
        testPiece.setCreatedBy(this.securityService.getCurrentUsername());
        testPiece.setUpdated(LocalDateTime.now());
        testPiece.setUpdatedBy(this.securityService.getCurrentUsername());
        return this.contestTestPieceRepository.saveAndFlush(testPiece);
    }

    @Override
    @IsBbrMember
    public ContestEventTestPieceDao addTestPieceToContest(ContestEventDao event, PieceDao testPiece) {
        ContestEventTestPieceDao newTestPiece = new ContestEventTestPieceDao();
        newTestPiece.setPiece(testPiece);
        return this.addTestPieceToContest(event, newTestPiece);
    }

    @Override
    @IsBbrMember
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

    @Override
    public Optional<ContestEventDao> fetchEvent(String contestSlug, LocalDate contestEventDate) {
        Optional<ContestDao> contest = this.contestRepository.fetchBySlug(contestSlug);
        if (contest.isEmpty()) {
            return Optional.empty();
        }
        return this.contestEventRepository.fetchByContestAndDate(contest.get().getId(), contestEventDate);
    }

    @Override
    public Optional<ContestEventDao> fetchEvent(ContestDao contest, LocalDate contestEventDate) {
        return this.fetchEvent(contest.getSlug(), contestEventDate);
    }

  @Override
  public Optional<ContestEventDao> fetchEventWithinWiderDateRange(String contestSlug, LocalDate eventDate) {
    Optional<ContestDao> contest = this.contestRepository.fetchBySlug(contestSlug);
    if (contest.isEmpty()) {
      return Optional.empty();
    }
    LocalDate startDate = eventDate.minus(14, ChronoUnit.DAYS);
    LocalDate endDate = eventDate.plus(14, ChronoUnit.DAYS);

    Optional<ContestEventDao> foundEvent = this.contestEventRepository.fetchByContestAndDateRange(contest.get().getId(), startDate, endDate);
    if (foundEvent.isPresent()) {
      return foundEvent;
    }

    LocalDate yearStart = LocalDate.of(eventDate.getYear(), 1, 1);
    LocalDate yearEnd = LocalDate.of(eventDate.getYear(), 12, 31);

    return this.contestEventRepository.fetchByContestAndDateRange(contest.get().getId(), yearStart, yearEnd);
  }

  @Override
    public List<ContestEventDao> fetchPastEventsForContest(ContestDao contest) {
        List<ContestEventDao> returnEvents = new ArrayList<>();

        List<ContestEventResultSqlDto> eventsSql = ContestResultSql.selectPastEventListForContest(this.entityManager, contest.getId());

        for (ContestEventResultSqlDto eachSqlEvent : eventsSql) {
            ContestEventDao currentEvent = new ContestEventDao();
            currentEvent.setContest(new ContestDao());
            currentEvent.setEventDate(eachSqlEvent.getEventDate());
            currentEvent.setEventDateResolution(ContestEventDateResolution.fromCode(eachSqlEvent.getEventDateResolution()));
            currentEvent.setNotes(eachSqlEvent.getEventNotes());
            currentEvent.setNoContest(eachSqlEvent.getNoContest());
            currentEvent.getContest().setSlug(eachSqlEvent.getContestSlug());
            if (!returnEvents.isEmpty()) {
                ContestEventDao previousEvent = returnEvents.get(returnEvents.size() - 1);
                if (previousEvent.getEventDate().equals(eachSqlEvent.getEventDate()) && previousEvent.getEventDateResolution().getCode().equals(eachSqlEvent.getEventDateResolution())) {
                    // it's the same event, we want to add winners to it
                    currentEvent = previousEvent;
                } else {
                    returnEvents.add(currentEvent);
                }
            } else {
                returnEvents.add(currentEvent);
            }

            ContestResultDao eachWinner = new ContestResultDao();
            eachWinner.setContestEvent(currentEvent);
            eachWinner.setPieces(new ArrayList<>());
            eachWinner.setBand(new BandDao());
            eachWinner.getBand().setRegion(new RegionDao());
            eachWinner.setBandName(eachSqlEvent.getBandCompetedAs());
            eachWinner.getBand().setSlug(eachSqlEvent.getBandSlug());
            eachWinner.getBand().setName(eachSqlEvent.getBandName());
            eachWinner.getBand().getRegion().setCountryCode(eachSqlEvent.getBandRegionCountryCode());

            if (eachSqlEvent.getSetPieceSlug() != null && eachSqlEvent.getSetPieceSlug().length() > 0) {
                eachWinner.getContestEvent().getPieces().add(new ContestEventTestPieceDao());
                eachWinner.getContestEvent().getPieces().get(0).setPiece(new PieceDao());
                eachWinner.getContestEvent().getPieces().get(0).getPiece().setSlug(eachSqlEvent.getSetPieceSlug());
                eachWinner.getContestEvent().getPieces().get(0).getPiece().setName(eachSqlEvent.getSetPieceName());
            }

            if (eachSqlEvent.getConductor1Slug() != null) {
                eachWinner.setConductor(new PersonDao());
                eachWinner.getConductor().setSlug(eachSqlEvent.getConductor1Slug());
                eachWinner.getConductor().setFirstNames(eachSqlEvent.getConductor1FirstNames());
                eachWinner.getConductor().setSurname(eachSqlEvent.getConductor1Surname());
            }

            if (eachSqlEvent.getConductor2Slug() != null) {
                eachWinner.setConductorSecond(new PersonDao());
                eachWinner.getConductorSecond().setSlug(eachSqlEvent.getConductor2Slug());
                eachWinner.getConductorSecond().setFirstNames(eachSqlEvent.getConductor2FirstNames());
                eachWinner.getConductorSecond().setSurname(eachSqlEvent.getConductor2Surname());
            }

            if (eachSqlEvent.getConductor3Slug() != null) {
                eachWinner.setConductorThird(new PersonDao());
                eachWinner.getConductorThird().setSlug(eachSqlEvent.getConductor3Slug());
                eachWinner.getConductorThird().setFirstNames(eachSqlEvent.getConductor3FirstNames());
                eachWinner.getConductorThird().setSurname(eachSqlEvent.getConductor3Surname());
            }
            currentEvent.getWinners().add(eachWinner);
        }

        return returnEvents;
    }

    @Override
    public List<ContestEventDao> fetchFutureEventsForContest(ContestDao contest) {
        return this.contestEventRepository.fetchFutureEventsByContest(contest.getId());
    }

    @Override
    public int fetchCountOfEvents(ContestDao contest) {
        return this.contestEventRepository.countEventsForContest(contest.getId());
    }

    @Override
    public ContestEventDao fetchEventLinkNext(ContestEventDao contestEventDao) {
        EventUpDownLeftRightSqlDto linkEvent = EventSql.selectLinkedNextEvent(this.entityManager, contestEventDao.getContest().getSlug(), contestEventDao.getEventDate().getYear());
        if (linkEvent == null) {
            return null;
        }
        return linkEvent.getEvent();
    }

    @Override
    public ContestEventDao fetchEventLinkPrevious(ContestEventDao contestEventDao) {
        EventUpDownLeftRightSqlDto linkEvent = EventSql.selectLinkedPreviousEvent(this.entityManager, contestEventDao.getContest().getSlug(), contestEventDao.getEventDate().getYear());
        if (linkEvent == null) {
            return null;
        }
        return linkEvent.getEvent();
    }

    @Override
    public ContestEventDao fetchEventLinkUp(ContestEventDao contestEventDao) {
        if (contestEventDao.getContest().getContestGroup() == null) {
            return null;
        }

        EventUpDownLeftRightSqlDto linkEvent = EventSql.selectLinkedUpEvent(this.entityManager, contestEventDao.getContest().getContestGroup().getSlug(), contestEventDao.getContest().getOrdering(), contestEventDao.getEventDate().getYear());
        if (linkEvent == null) {
            return null;
        }
        return linkEvent.getEvent();
    }

    @Override
    public ContestEventDao fetchEventLinkDown(ContestEventDao contestEventDao) {
        if (contestEventDao.getContest().getContestGroup() == null) {
            return null;
        }

        EventUpDownLeftRightSqlDto linkEvent = EventSql.selectLinkedDownEvent(this.entityManager, contestEventDao.getContest().getContestGroup().getSlug(), contestEventDao.getContest().getOrdering(), contestEventDao.getEventDate().getYear());
        if (linkEvent == null) {
            return null;
        }
        return linkEvent.getEvent();
    }

    @Override
    public List<ContestResultDao> fetchLastWeekend() {
        LocalDate previousSunday = DateTools.previousSundayDate();
        if (previousSunday == null) {
            return Collections.emptyList();
        }
        return this.fetchEventsForWeekend(previousSunday);
    }

    @Override
    public List<ContestResultDao> fetchThisWeekend() {
        LocalDate thisSunday = DateTools.thisWeekendSundayDate();
        if (thisSunday == null) {
            return Collections.emptyList();
        }
        return this.fetchEventsForWeekend(thisSunday);
    }

    @Override
    public List<ContestResultDao> fetchNextWeekend() {
        LocalDate nextSunday = DateTools.nextSundayDate();

        return this.fetchEventsForWeekend(nextSunday);
    }

    private List<ContestResultDao> fetchEventsForWeekend(LocalDate sunday) {

        LocalDate start = sunday.minus(4, ChronoUnit.DAYS);
        LocalDate end = sunday.plus(2, ChronoUnit.DAYS);

        List<EventResultSqlDto> weekendResults = EventSql.eventsForWeekend(this.entityManager, start, end);

        List<ContestResultDao> returnResults = new ArrayList<>();
        for (EventResultSqlDto eachResultSql : weekendResults) {
            returnResults.add(eachResultSql.getResult());
        }
        return returnResults;
    }

}
