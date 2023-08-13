package uk.co.bbr.services.bands.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;
import uk.co.bbr.services.regions.dao.RegionDao;

import java.math.BigInteger;

@Getter
public class BandListSqlDto extends AbstractSqlDto {

    private final String bandSlug;
    private final String bandName;
    private final String regionSlug;
    private final String regionName;
    private final String countryCode;
    private final Integer resultCount;

    public BandListSqlDto(Object[] columnList) {
        this.bandName = this.getString(columnList, 0);
        this.bandSlug = this.getString(columnList, 1);
        this.regionName = this.getString(columnList, 2);
        this.regionSlug = this.getString(columnList, 3);
        this.countryCode = this.getString(columnList, 4);
        this.resultCount = this.getIntegerOrZero(columnList, 5);
    }

    public BandDao asBand() {
        BandDao returnBand = new BandDao();
        returnBand.setName(this.bandName);
        returnBand.setSlug(this.bandSlug);
        returnBand.setResultsCount(this.resultCount);

        if (this.regionSlug != null && this.regionSlug.length() > 0) {
            returnBand.setRegion(new RegionDao());
            returnBand.getRegion().setSlug(this.regionSlug);
            returnBand.getRegion().setName(this.regionName);
            returnBand.getRegion().setCountryCode(this.countryCode);
        }

        return returnBand;
    }
}
