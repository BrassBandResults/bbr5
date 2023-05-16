package uk.co.bbr.services.pieces.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;

import java.math.BigInteger;

@Getter
public class PieceUsageCountSqlDto extends AbstractSqlDto {
    private final BigInteger pieceId;
    private final Integer setTestCount;
    private final Integer ownChoiceCount;

    public PieceUsageCountSqlDto(Object[] columnList) {
        this.pieceId = (BigInteger)columnList[0];
        this.setTestCount = columnList[1] instanceof BigInteger ? ((BigInteger)columnList[1]).intValue() : (Integer)columnList[1];
        this.ownChoiceCount = columnList[2] instanceof BigInteger ? ((BigInteger)columnList[2]).intValue() : (Integer)columnList[2];
    }
}
