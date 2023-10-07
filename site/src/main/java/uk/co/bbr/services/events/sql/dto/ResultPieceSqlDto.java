package uk.co.bbr.services.events.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.events.dao.ContestResultPieceDao;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.pieces.dao.PieceDao;

@Getter
public class ResultPieceSqlDto extends AbstractSqlDto {

    private final String pieceName;
    private final String pieceSlug;
    private final String pieceYear;
    private final Long resultId;
    private final Integer pieceOrdering;

    public ResultPieceSqlDto(Object[] columnList) {
        this.pieceName = this.getString(columnList, 0);
        this.pieceSlug = this.getString(columnList, 1);
        this.pieceYear = this.getString(columnList, 2);
        this.resultId = this.getLong(columnList,3);
        this.pieceOrdering = this.getInteger(columnList,4);
    }

    public ContestResultPieceDao asResultPiece() {
        ContestResultPieceDao returnPiece = new ContestResultPieceDao();
        returnPiece.setPiece(new PieceDao());
        returnPiece.getPiece().setName(this.pieceName);
        returnPiece.getPiece().setSlug(this.pieceSlug);
        returnPiece.getPiece().setYear(this.pieceYear);
        returnPiece.setOrdering(this.pieceOrdering);
        returnPiece.setContestResult(new ContestResultDao());
        returnPiece.getContestResult().setId(this.resultId);

        return returnPiece;
    }
}
