package uk.co.bbr.services.contests;

import uk.co.bbr.services.contests.dao.ContestAdjudicatorDao;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dao.ContestEventDao;
import uk.co.bbr.services.contests.dao.ContestTestPieceDao;
import uk.co.bbr.services.people.dao.PersonDao;

import java.time.LocalDate;
import java.util.List;

public interface ContestEventService {

    ContestEventDao create(ContestDao contest, LocalDate eventDate);

    ContestEventDao create(ContestDao contest, ContestEventDao event);

    List<ContestAdjudicatorDao> addAdjudicator(ContestEventDao event, PersonDao adjudicator);

    List<ContestAdjudicatorDao> fetchAdjudicators(ContestEventDao event);

    ContestEventDao fetch(ContestDao contest, LocalDate eventDate);

    ContestTestPieceDao addTestPieceToContest(ContestEventDao event, ContestTestPieceDao testPiece);

    List<ContestTestPieceDao> listTestPieces(ContestEventDao event);
}
