package uk.co.bbr.services.contests.sql.dto;

import uk.co.bbr.services.contests.dao.ContestResultDao;
import uk.co.bbr.services.contests.dao.ContestResultPieceDao;
import uk.co.bbr.services.pieces.dao.PieceDao;

import java.util.ArrayList;
import java.util.List;

public class BandResultsPiecesSqlDto {

    List<ResultPieceSqlDto> resultPieces = new ArrayList<>();

    public void populateResultPieces(ContestResultDao result) {
        result.setPieces(new ArrayList<>());
        for (ResultPieceSqlDto eachResultPiece : this.resultPieces) {
            if (eachResultPiece.getContestResultId().longValue() == result.getId()) {

                PieceDao piece = new PieceDao();
                piece.setSlug(eachResultPiece.getPieceSlug());
                piece.setName(eachResultPiece.getPieceName());
                piece.setYear(eachResultPiece.getPieceYear());

                ContestResultPieceDao resultPiece = new ContestResultPieceDao();
                resultPiece.setPiece(piece);
                resultPiece.setContestResult(result);

                result.getPieces().add(resultPiece);
            }
        }
    }

    public void add(ResultPieceSqlDto resultPiece) {
        this.resultPieces.add(resultPiece);
    }
}
