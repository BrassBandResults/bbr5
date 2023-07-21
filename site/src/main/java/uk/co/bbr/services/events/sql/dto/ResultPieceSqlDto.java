package uk.co.bbr.services.events.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.events.dao.ContestResultPieceDao;
import uk.co.bbr.services.events.types.ContestEventDateResolution;
import uk.co.bbr.services.events.types.ResultPositionType;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.regions.dao.RegionDao;

import java.math.BigInteger;
import java.sql.Date;
import java.time.LocalDate;

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
        this.pieceName = (String)columnList[0];
        this.pieceSlug = (String)columnList[1];
        this.pieceYear = (String)columnList[2];
        this.resultId = ((BigInteger)columnList[3]).longValue();
        this.pieceOrdering = (Integer)columnList[4];
        this.composerSurname = (String)columnList[5];
        this.composerFirstNames = (String)columnList[6];
        this.composerSlug = (String)columnList[7];
        this.arrangerSurname = (String)columnList[8];
        this.arrangerFirstNames = (String)columnList[9];
        this.arrangerSlug = (String)columnList[10];
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
