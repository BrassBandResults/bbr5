package uk.co.bbr.services.contests.sql.dto;

import uk.co.bbr.services.contests.dao.ContestEventDao;
import uk.co.bbr.services.contests.dao.ContestEventTestPieceDao;
import uk.co.bbr.services.contests.dao.ContestResultDao;
import uk.co.bbr.services.contests.dao.ContestResultPieceDao;
import uk.co.bbr.services.pieces.dao.PieceDao;

import java.util.ArrayList;
import java.util.List;

public class BandEventPiecesSqlDto {

    List<EventPieceSqlDto> eventPieces = new ArrayList<>();

    public void populateEventPieces(ContestEventDao event) {
        event.setPieces(new ArrayList<>());
        for (EventPieceSqlDto eachResultPiece : this.eventPieces) {
            if (eachResultPiece.getContestEventId().longValue() == event.getId()) {

                PieceDao piece = new PieceDao();
                piece.setSlug(eachResultPiece.getPieceSlug());
                piece.setName(eachResultPiece.getPieceName());
                piece.setYear(eachResultPiece.getPieceYear());

                ContestEventTestPieceDao eventPiece = new ContestEventTestPieceDao();
                eventPiece.setPiece(piece);
                eventPiece.setContestEvent(event);

                event.getPieces().add(eventPiece);
            }
        }
    }

    public void add(EventPieceSqlDto eventPiece) {
        this.eventPieces.add(eventPiece);
    }
}
