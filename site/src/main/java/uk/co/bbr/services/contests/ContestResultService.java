package uk.co.bbr.services.contests;

import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.contests.dao.ContestEventDao;
import uk.co.bbr.services.contests.dao.ContestResultDao;
import uk.co.bbr.services.contests.dao.ContestResultPieceDao;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.pieces.dao.PieceDao;

import java.util.List;

public interface ContestResultService {

    ContestResultDao addResult(ContestEventDao event, ContestResultDao result);
    ContestResultDao addResult(ContestEventDao event, String position, BandDao band, PersonDao conductor);
    ContestResultDao migrate(ContestEventDao event, ContestResultDao contestResult);

    List<ContestResultDao> fetchForEvent(ContestEventDao event);

    ContestResultPieceDao addPieceToResult(ContestResultDao contestResult, ContestResultPieceDao contestResultTestPiece);
    ContestResultPieceDao addPieceToResult(ContestResultDao contestResult, PieceDao piece);

    List<ContestResultDao> findNonWhitResultsForBand(BandDao bandDao);
}
