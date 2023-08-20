package uk.co.bbr.services.bands.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;
import uk.co.bbr.services.regions.dao.RegionDao;

@Getter
public class BandWinnersSqlDto extends AbstractSqlDto {

    private final String bandSlug;
    private final String bandName;
    private final String regionSlug;
    private final String regionName;
    private final String countryCode;
    private final Integer wins;
    private final Integer contests;

    public BandWinnersSqlDto(Object[] columnList) {
        this.bandSlug = this.getString(columnList, 0);
        this.bandName = this.getString(columnList, 1);
        this.wins = this.getInteger(columnList, 2);
        this.contests = this.getInteger(columnList, 3);
        this.regionSlug = this.getString(columnList, 4);
        this.regionName = this.getString(columnList, 5);
        this.countryCode = this.getString(columnList, 6);
    }

    public BandDao getBand() {
        BandDao returnBand = new BandDao();
        returnBand.setName(this.bandName);
        returnBand.setSlug(this.bandSlug);

        if (this.regionSlug != null && this.regionSlug.length() > 0) {
            returnBand.setRegion(new RegionDao());
            returnBand.getRegion().setSlug(this.regionSlug);
            returnBand.getRegion().setName(this.regionName);
            returnBand.getRegion().setCountryCode(this.countryCode);
        }

        return returnBand;
    }

    public Integer getPercentage() {
        return (this.wins * 100) / this.contests;
    }
}
