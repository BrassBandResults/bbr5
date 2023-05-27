package uk.co.bbr.services.pieces.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;
import uk.co.bbr.services.people.dao.PersonDao;
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
    private final String composerSurname;
    private final String composerFirstNames;
    private final String composerSuffix;
    private final String composerSlug;
    private final String arrangerSurname;
    private final String arrangerFirstNames;
    private final String arrangerSuffix;
    private final String arrangerSlug;

    public BestPieceSqlDto(Object[] columnList) {
        this.resultPosition = (Integer)columnList[0];
        this.pieceSlug = (String)columnList[1];
        this.pieceName = (String)columnList[2];
        this.pieceYear = (String)columnList[3];
        this.composerSurname = (String)columnList[4];
        this.composerFirstNames = (String)columnList[5];
        this.composerSuffix = (String)columnList[6];
        this.composerSlug = (String)columnList[7];
        this.arrangerSurname = (String)columnList[8];
        this.arrangerFirstNames = (String)columnList[9];
        this.arrangerSuffix = (String)columnList[10];
        this.arrangerSlug = (String)columnList[11];
    }

    public PieceDao getPiece() {
        PieceDao returnPiece = new PieceDao();
        returnPiece.setName(this.pieceName);
        returnPiece.setSlug(this.pieceSlug);
        returnPiece.setYear(this.pieceYear);

        if (this.composerSlug != null) {
            PersonDao composer = new PersonDao();
            composer.setSurname(this.composerSurname);
            composer.setFirstNames(this.composerFirstNames);
            composer.setSuffix(this.composerSuffix);
            composer.setSlug(this.composerSlug);
            returnPiece.setComposer(composer);
        }

        if (this.arrangerSlug != null) {
            PersonDao arranger = new PersonDao();
            arranger.setSurname(this.arrangerSurname);
            arranger.setFirstNames(this.arrangerFirstNames);
            arranger.setSuffix(this.arrangerSuffix);
            arranger.setSlug(this.arrangerSlug);
            returnPiece.setArranger(arranger);
        }

        return returnPiece;
    }
}

