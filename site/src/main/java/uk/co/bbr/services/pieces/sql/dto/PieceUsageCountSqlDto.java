package uk.co.bbr.services.pieces.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;

import java.math.BigInteger;

@Getter
public class PieceUsageCountSqlDto extends AbstractSqlDto {
    private final Long pieceId;
    private final Integer setTestCount;
    private final Integer ownChoiceCount;

    public PieceUsageCountSqlDto(Object[] columnList) {
        this.pieceId = this.getLong(columnList,0);
        this.setTestCount = this.getInteger(columnList, 1);
        this.ownChoiceCount = this.getInteger(columnList, 2);
    }
}
