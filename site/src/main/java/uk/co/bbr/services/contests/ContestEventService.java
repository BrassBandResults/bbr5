package uk.co.bbr.services.contests;

import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.contests.dao.ContestAdjudicatorDao;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dao.ContestEventDao;
import uk.co.bbr.services.contests.dao.ContestEventTestPieceDao;
import uk.co.bbr.services.contests.types.TestPieceAndOr;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.pieces.dao.PieceDao;

import java.time.LocalDate;
import java.util.List;

public interface ContestEventService {

    ContestEventDao create(ContestDao contest, LocalDate eventDate);

    ContestEventDao create(ContestDao contest, ContestEventDao event);

    ContestEventDao migrate(ContestDao contest, ContestEventDao contestEvent);

    List<ContestAdjudicatorDao> addAdjudicator(ContestEventDao event, PersonDao adjudicator);

    List<ContestAdjudicatorDao> fetchAdjudicators(ContestEventDao event);

    ContestEventDao fetch(ContestDao contest, LocalDate eventDate);

    ContestEventTestPieceDao addTestPieceToContest(ContestEventDao event, ContestEventTestPieceDao testPiece);
    ContestEventTestPieceDao addTestPieceToContest(ContestEventDao event, PieceDao testPiece);
    ContestEventTestPieceDao addTestPieceToContest(ContestEventDao event, PieceDao testPiece, TestPieceAndOr andOr);

    List<ContestEventTestPieceDao> listTestPieces(ContestEventDao event);
}
