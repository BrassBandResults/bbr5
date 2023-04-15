package uk.co.bbr.services.years.sql.dto;

import lombok.Getter;

import java.math.BigInteger;

@Getter
public class YearListEntrySqlDto {
    private final Integer year;
    private final Integer bandCount;
    private final Integer eventCount;

    public YearListEntrySqlDto(Object[] eachRowData) {
        this.year = (Integer)eachRowData[0];

        if (eachRowData[1] instanceof BigInteger) {
            this.bandCount = ((BigInteger)eachRowData[1]).intValue();
        } else {
            this.bandCount = (Integer) eachRowData[1];
        }

        if (eachRowData[2] instanceof BigInteger) {
            this.eventCount = ((BigInteger)eachRowData[2]).intValue();
        } else {
            this.eventCount = (Integer) eachRowData[2];
        }
    }
}
