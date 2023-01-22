package uk.co.bbr.services.contests;

import uk.co.bbr.services.contests.dao.ContestEventDao;
import uk.co.bbr.services.contests.dao.ContestResultDao;
import uk.co.bbr.services.contests.dao.ContestResultPieceDao;

import java.util.List;

public interface ContestResultService {

    ContestResultDao addResult(ContestEventDao event, ContestResultDao result);
    ContestResultDao migrate(ContestEventDao event, ContestResultDao contestResult);

    List<ContestResultDao> fetchForEvent(ContestEventDao event);

    ContestResultPieceDao addPieceToResult(ContestResultDao contestResult, ContestResultPieceDao contestResultTestPiece);
}
