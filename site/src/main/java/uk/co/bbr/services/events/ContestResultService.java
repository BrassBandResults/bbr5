package uk.co.bbr.services.events;

import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.dto.BandDetailsDto;
import uk.co.bbr.services.bands.types.ResultSetCategory;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.groups.dao.ContestGroupDao;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.events.dao.ContestResultPieceDao;
import uk.co.bbr.services.tags.dao.ContestTagDao;
import uk.co.bbr.services.contests.sql.dto.ContestWinsSqlDto;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.people.dto.ConductingDetailsDto;
import uk.co.bbr.services.pieces.dao.PieceDao;

import java.util.List;
import java.util.Set;

public interface ContestResultService {

    ContestResultDao addResult(ContestEventDao event, ContestResultDao result);
    ContestResultDao addResult(ContestEventDao event, String position, BandDao band, PersonDao conductor);
    ContestResultDao migrate(ContestEventDao event, ContestResultDao contestResult);

    List<ContestResultDao> fetchForEvent(ContestEventDao event);

    ContestResultPieceDao addPieceToResult(ContestResultDao contestResult, ContestResultPieceDao contestResultTestPiece);
    ContestResultPieceDao addPieceToResult(ContestResultDao contestResult, PieceDao piece);

    BandDetailsDto findResultsForBand(BandDao band, ResultSetCategory category);

    BandDetailsDto findResultsForBand(BandDao band, ResultSetCategory category, ContestDao contest);

    BandDetailsDto findResultsForBand(BandDao band, ResultSetCategory category, ContestGroupDao contestGroup);

    BandDetailsDto findResultsForBand(BandDao band, ResultSetCategory category, ContestTagDao contestTag);

    ConductingDetailsDto findResultsForConductor(PersonDao person);

    List<ContestResultPieceDao> fetchResultsWithOwnChoicePieces(ContestDao contest);

    int fetchCountOfOwnChoiceForContest(ContestDao contest);

    List<ContestWinsSqlDto> fetchWinsCounts(ContestDao contest);

    Set<PersonDao> fetchBandConductors(BandDao band);
}