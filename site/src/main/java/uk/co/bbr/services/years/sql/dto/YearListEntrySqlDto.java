package uk.co.bbr.services.years.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;

import java.math.BigInteger;

@Getter
public class YearListEntrySqlDto extends AbstractSqlDto {
    private final Integer year;
    private final Integer bandCount;
    private final Integer eventCount;

    public YearListEntrySqlDto(Object[] columnList) {
        this.year = (Integer)columnList[0];
        this.bandCount = columnList[1] instanceof BigInteger ? ((BigInteger)columnList[1]).intValue() : (Integer)columnList[1];
        this.eventCount = columnList[2] instanceof BigInteger ? ((BigInteger)columnList[2]).intValue() : (Integer)columnList[2];
    }
}
