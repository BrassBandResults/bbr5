package uk.co.bbr.services.events;

import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dto.ContestStreakDto;
import uk.co.bbr.services.contests.sql.dto.ContestWinsSqlDto;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.events.dao.ContestResultPieceDao;
import uk.co.bbr.services.events.dto.ContestEventFormGuideDto;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.pieces.dao.PieceDao;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ResultService {

    ContestResultDao addResult(ContestEventDao event, ContestResultDao result);
    ContestResultDao addResult(ContestEventDao event, String position, BandDao band, PersonDao conductor);
    ContestResultDao migrate(ContestEventDao event, ContestResultDao contestResult);

    List<ContestResultDao> fetchForEvent(ContestEventDao event);
    List<ContestResultDao> fetchObjectsForEvent(ContestEventDao contestEvent);

    ContestResultPieceDao addPieceToResult(ContestResultDao contestResult, ContestResultPieceDao contestResultTestPiece);
    ContestResultPieceDao addPieceToResult(ContestResultDao contestResult, PieceDao piece);
    ContestResultPieceDao addPieceToResult(ContestResultDao contestResult, PieceDao piece, String suffix);

    List<ContestResultPieceDao> fetchResultsWithOwnChoicePieces(ContestDao contest);

    int fetchCountOfOwnChoiceForContest(ContestDao contest);

    List<ContestWinsSqlDto> fetchWinsCounts(ContestDao contest);

    Set<PersonDao> fetchBandConductors(BandDao band);

    List<ContestResultDao> fetchResultsForContestAndPosition(ContestDao contestDao, String position);

    List<ContestResultDao> fetchResultsForContestAndDraw(ContestDao contestDao, int draw);

    ContestResultDao update(ContestResultDao result);

    List<ContestStreakDto> fetchStreaksForContest(ContestDao contest);

    Optional<ContestResultDao> fetchById(Long resultId);

    List<ContestResultPieceDao> listResultPieces(ContestResultDao result);

    Optional<ContestResultPieceDao> fetchResultPieceById(ContestResultDao contestResult, Long resultPieceId);

    void removePiece(ContestEventDao contestEvent, ContestResultDao contestResult, ContestResultPieceDao contestResultPiece);

    void delete(ContestResultDao contestResult);

    void workOutCanEdit(ContestEventDao contestEventDao, List<ContestResultDao> eventResults);

    List<ContestEventFormGuideDto> fetchFormGuideForEvent(ContestEventDao contestEvent);
}
