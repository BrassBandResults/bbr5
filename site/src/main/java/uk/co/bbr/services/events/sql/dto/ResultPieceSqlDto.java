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
    private final String composerSurname;
    private final String composerFirstNames;
    private final String composerSlug;
    private final String arrangerSurname;
    private final String arrangerFirstNames;
    private final String arrangerSlug;


    public ResultPieceSqlDto(Object[] columnList) {
        this.pieceName = this.getString(columnList, 0);
        this.pieceSlug = this.getString(columnList, 1);
        this.pieceYear = this.getString(columnList, 2);
        this.resultId = this.getLong(columnList,3);
        this.pieceOrdering = this.getInteger(columnList,4);
        this.composerSurname = this.getString(columnList, 5);
        this.composerFirstNames = this.getString(columnList, 6);
        this.composerSlug = this.getString(columnList, 7);
        this.arrangerSurname = this.getString(columnList, 8);
        this.arrangerFirstNames = this.getString(columnList, 9);
        this.arrangerSlug = this.getString(columnList, 10);
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

        if (this.composerSlug != null) {
            returnPiece.getPiece().setComposer(new PersonDao());
            returnPiece.getPiece().getComposer().setSlug(this.composerSlug);
            returnPiece.getPiece().getComposer().setFirstNames(this.composerFirstNames);
            returnPiece.getPiece().getComposer().setSurname(this.composerSurname);
        }

        if (this.arrangerSlug != null) {
            returnPiece.getPiece().setArranger(new PersonDao());
            returnPiece.getPiece().getArranger().setSlug(this.arrangerSlug);
            returnPiece.getPiece().getArranger().setFirstNames(this.arrangerFirstNames);
            returnPiece.getPiece().getArranger().setSurname(this.arrangerSurname);
        }

        return returnPiece;
    }
}
