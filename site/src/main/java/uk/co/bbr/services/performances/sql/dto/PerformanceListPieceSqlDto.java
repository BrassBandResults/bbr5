package uk.co.bbr.services.performances.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.events.dao.ContestEventTestPieceDao;
import uk.co.bbr.services.events.dao.ContestResultPieceDao;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;
import uk.co.bbr.services.pieces.dao.PieceDao;

@Getter
public class PerformanceListPieceSqlDto extends AbstractSqlDto {

    private final Long resultId;
    private final String pieceName;
    private final String pieceSlug;
    private final String pieceYear;


    public PerformanceListPieceSqlDto(Object[] columnList) {
        this.resultId = this.getLong(columnList, 0);
        this.pieceName = this.getString(columnList, 1);
        this.pieceSlug = this.getString(columnList, 2);
        this.pieceYear = this.getString(columnList, 3);
    }

    public ContestResultPieceDao asResultPiece() {
        PieceDao piece = new PieceDao();
        piece.setName(this.pieceName);
        piece.setSlug(this.pieceSlug);
        piece.setYear(this.pieceYear);

        ContestResultPieceDao resultPiece = new ContestResultPieceDao();
        resultPiece.setPiece(piece);

        return resultPiece;
    }

    public ContestEventTestPieceDao asEventPiece() {
        PieceDao piece = new PieceDao();
        piece.setName(this.pieceName);
        piece.setSlug(this.pieceSlug);
        piece.setYear(this.pieceYear);

        ContestEventTestPieceDao eventPiece = new ContestEventTestPieceDao();
        eventPiece.setPiece(piece);

        return eventPiece;
    }
}
