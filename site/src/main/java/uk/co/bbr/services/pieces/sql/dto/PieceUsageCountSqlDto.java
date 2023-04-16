package uk.co.bbr.services.pieces.sql.dto;

import lombok.Getter;

import java.math.BigInteger;

@Getter
public class PieceUsageCountSqlDto {
    private final BigInteger pieceId;
    private final Integer setTestCount;
    private final Integer ownChoiceCount;

    public PieceUsageCountSqlDto(Object[] eachRowData) {
        this.pieceId = (BigInteger)eachRowData[0];
        this.setTestCount = (Integer)eachRowData[1];
        this.ownChoiceCount = (Integer)eachRowData[2];
    }
}
