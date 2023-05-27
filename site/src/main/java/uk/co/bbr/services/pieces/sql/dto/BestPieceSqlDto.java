package uk.co.bbr.services.pieces.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;
import uk.co.bbr.services.pieces.dao.PieceDao;

import java.math.BigInteger;
import java.sql.Date;
import java.time.LocalDate;

@Getter
public class BestPieceSqlDto extends AbstractSqlDto {
    private final String pieceName;
    private final String pieceSlug;
    private final String pieceYear;
    private final Integer resultPosition;

    public BestPieceSqlDto(Object[] columnList) {
        this.resultPosition = (Integer)columnList[0];
        this.pieceSlug = (String)columnList[1];
        this.pieceName = (String)columnList[2];
        this.pieceYear = (String)columnList[3];
    }

    public PieceDao getPiece() {
        PieceDao returnPiece = new PieceDao();
        returnPiece.setName(this.pieceName);
        returnPiece.setSlug(this.pieceSlug);
        returnPiece.setYear(this.pieceYear);
        return returnPiece;
    }
}
