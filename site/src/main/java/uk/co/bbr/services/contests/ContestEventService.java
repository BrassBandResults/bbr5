package uk.co.bbr.services.contests;

import uk.co.bbr.services.contests.dao.ContestAdjudicatorDao;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dao.ContestEventDao;
import uk.co.bbr.services.contests.dao.ContestEventTestPieceDao;
import uk.co.bbr.services.contests.dao.ContestResultDao;
import uk.co.bbr.services.contests.types.TestPieceAndOr;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.pieces.dao.PieceDao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ContestEventService {

    ContestEventDao create(ContestDao contest, LocalDate eventDate);

    ContestEventDao create(ContestDao contest, ContestEventDao event);

    ContestEventDao migrate(ContestDao contest, ContestEventDao contestEvent);

    ContestEventDao update(ContestEventDao event);

    List<ContestAdjudicatorDao> addAdjudicator(ContestEventDao event, PersonDao adjudicator);

    List<ContestAdjudicatorDao> fetchAdjudicators(ContestEventDao event);

    ContestEventTestPieceDao addTestPieceToContest(ContestEventDao event, ContestEventTestPieceDao testPiece);

    ContestEventTestPieceDao addTestPieceToContest(ContestEventDao event, PieceDao testPiece);

    ContestEventTestPieceDao addTestPieceToContest(ContestEventDao event, PieceDao testPiece, TestPieceAndOr andOr);

    List<ContestEventTestPieceDao> listTestPieces(ContestEventDao event);

    Optional<ContestEventDao> fetchEvent(String contestSlug, LocalDate contestEventDate);

    List<ContestEventDao> fetchPastEventsForContest(ContestDao contestDao);

    List<ContestEventDao> fetchFutureEventsForContest(ContestDao contestDao);
}
