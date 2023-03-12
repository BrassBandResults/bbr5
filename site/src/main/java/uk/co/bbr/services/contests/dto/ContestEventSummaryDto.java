package uk.co.bbr.services.contests.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.co.bbr.services.contests.dao.ContestEventDao;
import uk.co.bbr.services.contests.dao.ContestEventTestPieceDao;
import uk.co.bbr.services.contests.dao.ContestResultDao;
import uk.co.bbr.services.pieces.dao.PieceDao;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ContestEventSummaryDto {
    private final ContestEventDao contestEvent;
    private final List<ContestResultDao> winningBands;
    private final List<PieceDao> testPieces;
}
