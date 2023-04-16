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
        if (eachRowData[1] instanceof BigInteger) {
            this.setTestCount = ((BigInteger)eachRowData[1]).intValue();
        } else {
            this.setTestCount = (Integer)eachRowData[1];
        }

        if (eachRowData[1] instanceof BigInteger) {
            this.ownChoiceCount = ((BigInteger)eachRowData[2]).intValue();
        } else {
            this.ownChoiceCount = (Integer)eachRowData[2];
        }
    }
}
