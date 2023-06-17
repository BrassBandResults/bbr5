package uk.co.bbr.services.contests.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;

import java.math.BigInteger;

@Getter
public class ContestWinsSqlDto extends AbstractSqlDto {
    private final String bandSlug;
    private final String bandName;
    private final int winCount;

    public ContestWinsSqlDto(Object[] columnList) {
        this.bandSlug = (String)columnList[0];
        this.bandName = (String)columnList[1];
        this.winCount = columnList[2] instanceof Integer ? (Integer)columnList[2] : ((BigInteger)columnList[2]).intValue();
    }
}
