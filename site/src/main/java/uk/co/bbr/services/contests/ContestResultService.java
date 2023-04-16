package uk.co.bbr.services.contests;

import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.dto.BandDetailsDto;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dao.ContestEventDao;
import uk.co.bbr.services.contests.dao.ContestResultDao;
import uk.co.bbr.services.contests.dao.ContestResultPieceDao;
import uk.co.bbr.services.contests.dto.ContestWinsDto;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.people.dto.ConductingDetailsDto;
import uk.co.bbr.services.pieces.dao.PieceDao;

import java.util.List;

public interface ContestResultService {

    ContestResultDao addResult(ContestEventDao event, ContestResultDao result);
    ContestResultDao addResult(ContestEventDao event, String position, BandDao band, PersonDao conductor);
    ContestResultDao migrate(ContestEventDao event, ContestResultDao contestResult);

    List<ContestResultDao> fetchForEvent(ContestEventDao event);

    ContestResultPieceDao addPieceToResult(ContestResultDao contestResult, ContestResultPieceDao contestResultTestPiece);
    ContestResultPieceDao addPieceToResult(ContestResultDao contestResult, PieceDao piece);

    BandDetailsDto findResultsForBand(BandDao band);

    ConductingDetailsDto findResultsForConductor(PersonDao person);

    List<ContestResultPieceDao> fetchResultsWithOwnChoicePieces(ContestDao contest);

    int fetchCountOfOwnChoiceForContest(ContestDao contest);

    List<ContestWinsDto> fetchWinsCounts(ContestDao contest);
}
