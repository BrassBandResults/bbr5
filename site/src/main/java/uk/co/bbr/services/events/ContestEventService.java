package uk.co.bbr.services.events;

import uk.co.bbr.services.events.dao.ContestAdjudicatorDao;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.dao.ContestEventTestPieceDao;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.events.sql.dto.EventUpDownLeftRightSqlDto;
import uk.co.bbr.services.events.types.TestPieceAndOr;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.pieces.dao.PieceDao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ContestEventService {

    ContestEventDao create(ContestDao contest, LocalDate eventDate);

    ContestEventDao create(ContestDao contest, ContestEventDao event);

    ContestEventDao update(ContestEventDao event);

    List<ContestAdjudicatorDao> addAdjudicator(ContestEventDao event, PersonDao adjudicator);

    List<ContestAdjudicatorDao> fetchAdjudicators(ContestEventDao event);

    ContestEventTestPieceDao addTestPieceToContest(ContestEventDao event, ContestEventTestPieceDao testPiece);

    ContestEventTestPieceDao addTestPieceToContest(ContestEventDao event, PieceDao testPiece);

    ContestEventTestPieceDao addTestPieceToContest(ContestEventDao event, PieceDao testPiece, TestPieceAndOr andOr);

    List<ContestEventTestPieceDao> listTestPieces(ContestEventDao event);

    Optional<ContestEventDao> fetchEvent(String contestSlug, LocalDate contestEventDate);
    Optional<ContestEventDao> fetchEvent(ContestDao contest, LocalDate contestEventDate);
    Optional<ContestEventDao> fetchEventWithinWiderDateRange(String contestSlug, LocalDate eventDate);

    List<ContestEventDao> fetchPastEventsForContest(ContestDao contest);

    List<ContestEventDao> fetchFutureEventsForContest(ContestDao contest);

    int fetchCountOfEvents(ContestDao contest);

    ContestEventDao fetchEventLinkNext(ContestEventDao contestEvent);

    ContestEventDao fetchEventLinkPrevious(ContestEventDao contestEvent);

    ContestEventDao fetchEventLinkUp(ContestEventDao contestEvent);

    ContestEventDao fetchEventLinkDown(ContestEventDao contestEvent);

    List<ContestResultDao> fetchLastWeekend();

    List<ContestResultDao> fetchThisWeekend();

    List<ContestResultDao> fetchNextWeekend();

    List<ContestResultDao> fetchEventsForMonth(LocalDate month);

    Optional<ContestEventTestPieceDao> fetchSetTestById(ContestEventDao contestEvent, Long eventPieceId);

    void removeSetTestPiece(ContestEventTestPieceDao eventPiece);

    void delete(ContestEventDao contestEvent);

    ContestResultDao fetchTodayInHistory();

    ContestResultDao fetchThisWeekInHistory();

    Optional<ContestEventDao> fetchEventBetweenDates(ContestDao contest, LocalDate startDate, LocalDate endDate);

    void removeAdjudicator(ContestEventDao contestEvent, Long adjudicatorId);
}
