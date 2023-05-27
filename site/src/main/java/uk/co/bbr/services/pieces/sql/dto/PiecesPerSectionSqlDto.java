package uk.co.bbr.services.pieces.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.pieces.dao.PieceDao;

@Getter
public class PiecesPerSectionSqlDto extends AbstractSqlDto {
    private final String pieceName;
    private final String pieceSlug;
    private final String pieceYear;
    private final String composerSurname;
    private final String composerFirstNames;
    private final String composerSuffix;
    private final String composerSlug;
    private final String arrangerSurname;
    private final String arrangerFirstNames;
    private final String arrangerSuffix;
    private final String arrangerSlug;
    private final Integer setTestCount;
    private final Integer ownChoiceCount;

    public PiecesPerSectionSqlDto(Object[] columnList) {
        this.pieceSlug = (String)columnList[0];
        this.pieceName = (String)columnList[1];
        this.pieceYear = (String)columnList[2];
        this.composerSurname = (String)columnList[3];
        this.composerFirstNames = (String)columnList[4];
        this.composerSuffix = (String)columnList[5];
        this.composerSlug = (String)columnList[6];
        this.arrangerSurname = (String)columnList[7];
        this.arrangerFirstNames = (String)columnList[8];
        this.arrangerSuffix = (String)columnList[9];
        this.arrangerSlug = (String)columnList[10];
        this.setTestCount = (Integer)columnList[11];
        this.ownChoiceCount = (Integer)columnList[12];
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

