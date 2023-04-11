package uk.co.bbr.services.years.sql.dto;

import lombok.Getter;

@Getter
public class YearListEntrySqlDto {
    private final Integer year;
    private final Integer bandCount;
    private final Integer eventCount;

    public YearListEntrySqlDto(Object[] eachRowData) {
        this.year = (Integer)eachRowData[0];
        this.bandCount = (Integer)eachRowData[1];
        this.eventCount = (Integer)eachRowData[2];
    }
}
