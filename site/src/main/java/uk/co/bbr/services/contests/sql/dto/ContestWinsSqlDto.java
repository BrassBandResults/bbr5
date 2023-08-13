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
        this.bandSlug = this.getString(columnList, 0);
        this.bandName = this.getString(columnList, 1);
        this.winCount = this.getInteger(columnList,2);
    }
}
