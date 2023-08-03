package uk.co.bbr.services.venues.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.venues.dao.VenueDao;

import java.math.BigInteger;

@Getter
public class VenueListSqlDto  extends AbstractSqlDto {

    private final String venueSlug;
    private final String venueName;
    private final String regionSlug;
    private final String regionName;
    private final String countryCode;
    private final int eventCount;
    private final String latitude;
    private final String longitude;
    private final Long venueId;

    public VenueListSqlDto(Object[] columnList) {
        this.venueSlug = (String)columnList[0];
        this.venueName = (String)columnList[1];
        this.regionSlug = (String)columnList[2];
        this.regionName = (String)columnList[3];
        this.countryCode = (String)columnList[4];
        this.eventCount = this.getInteger(columnList, 5);
        this.latitude = (String)columnList[6];
        this.longitude = (String)columnList[7];
        this.venueId = this.getLong(columnList, 8);
    }

    public VenueDao asVenue() {
        VenueDao venue = new VenueDao();
        venue.setName(this.getVenueName());
        venue.setSlug(this.getVenueSlug());
        venue.setEventCount(this.getEventCount());
        venue.setLatitude((this.getLatitude()));
        venue.setLongitude(this.getLongitude());
        venue.setId(this.venueId);

        if (this.getRegionSlug() != null && this.getRegionSlug().length() > 0) {
            RegionDao region = new RegionDao();
            region.setSlug(this.getRegionSlug());
            region.setName(this.getRegionName());
            region.setCountryCode(this.getCountryCode());
            venue.setRegion(region);
        }
        return venue;
    }
}
