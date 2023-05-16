package uk.co.bbr.services.venues.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;

import java.math.BigInteger;

@Getter
public class VenueListSqlDto  extends AbstractSqlDto {

    private final String venueSlug;
    private final String venueName;
    private final String regionSlug;
    private final String regionName;
    private final String countryCode;
    private final int eventCount;

    public VenueListSqlDto(Object[] columnList) {
        this.venueSlug = (String)columnList[0];
        this.venueName = (String)columnList[1];
        this.regionSlug = (String)columnList[2];
        this.regionName = (String)columnList[3];
        this.countryCode = (String)columnList[4];
        this.eventCount = columnList[5] instanceof BigInteger ? ((BigInteger)columnList[5]).intValue() : (Integer)columnList[5];
    }
}
