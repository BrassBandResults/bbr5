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
        this.bandSlug = (String)columnList[0];
        this.bandName = (String)columnList[1];
        this.regionSlug = (String)columnList[2];
        this.regionName = (String)columnList[3];
        this.countryCode = (String)columnList[4];
        if (columnList[5] != null) {
            this.resultCount = columnList[5] instanceof BigInteger ? ((BigInteger) columnList[5]).intValue() : (Integer) columnList[5];
        }
        else {
            this.resultCount = 0;
        }
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
