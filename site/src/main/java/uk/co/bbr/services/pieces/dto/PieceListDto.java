package uk.co.bbr.services.pieces.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.co.bbr.services.pieces.dao.PieceDao;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class PieceListDto {
    private final int returnedPiecesCount;
    private final long allPiecesCount;
    private final String searchPrefix;
    private final List<PieceDao> returnedPieces;
}
