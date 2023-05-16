package uk.co.bbr.services.contests.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;

@Getter
public class ContestWinsSqlDto extends AbstractSqlDto {
    private final String bandSlug;
    private final String bandName;
    private final int winCount;

    public ContestWinsSqlDto(Object[] columnList) {
        this.bandSlug = (String)columnList[0];
        this.bandName = (String)columnList[1];
        this.winCount = (Integer)columnList[2];
    }
}
