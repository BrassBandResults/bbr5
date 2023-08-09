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
        this.name = this.getString(columnList, 0);
        this.slug = this.getString(columnList, 1);
        if (columnList[2] != null) {
            this.eventCount = this.getInteger(columnList, 2);
        }
        else {
            this.eventCount = 0;
        }
    }
}
