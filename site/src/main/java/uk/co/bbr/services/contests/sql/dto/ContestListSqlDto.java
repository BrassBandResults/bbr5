package uk.co.bbr.services.contests.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;

import java.math.BigInteger;

@Getter
public class ContestListSqlDto extends AbstractSqlDto {

    private final String slug;
    private final String name;
    private final Integer eventCount;

    public ContestListSqlDto(Object[] columnList) {
        this.name = (String)columnList[0];
        this.slug = (String)columnList[1];
        if (columnList[2] != null) {
            this.eventCount = columnList[2] instanceof BigInteger ? ((BigInteger) columnList[2]).intValue() : (Integer) columnList[2];
        }
        else {
            this.eventCount = 0;
        }
    }
}
