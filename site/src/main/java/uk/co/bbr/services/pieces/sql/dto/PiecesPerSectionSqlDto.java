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
        this.pieceSlug = this.getString(columnList, 0);
        this.pieceName = this.getString(columnList, 1);
        this.pieceYear = this.getString(columnList, 2);
        this.composerSurname = this.getString(columnList, 3);
        this.composerFirstNames = this.getString(columnList, 4);
        this.composerSuffix = this.getString(columnList, 5);
        this.composerSlug = this.getString(columnList, 6);
        this.arrangerSurname = this.getString(columnList, 7);
        this.arrangerFirstNames = this.getString(columnList, 8);
        this.arrangerSuffix = this.getString(columnList, 9);
        this.arrangerSlug = this.getString(columnList, 10);
        this.setTestCount = this.getInteger(columnList,11);
        this.ownChoiceCount = this.getInteger(columnList,2);
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

