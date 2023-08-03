package uk.co.bbr.services.regions.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;
import uk.co.bbr.services.regions.dao.RegionDao;

import java.math.BigInteger;

@Getter
public class RegionListSqlDto extends AbstractSqlDto {

    private final Long regionId;
    private final String regionSlug;
    private final String regionName;
    private final String countryCode;
    private final Integer activeBandsCount;
    private final Integer extinctBandsCount;

    public RegionListSqlDto(Object[] columnList) {
        this.regionSlug = (String)columnList[0];
        this.regionName = (String)columnList[1];
        this.countryCode = (String)columnList[2];
        if (columnList[3] != null) {
            this.activeBandsCount = this.getInteger(columnList, 3);
        }
        else {
            this.activeBandsCount = 0;
        }
        if (columnList[4] != null) {
            this.extinctBandsCount = this.getInteger(columnList, 4);
        }
        else {
            this.extinctBandsCount = 0;
        }
        if (columnList[5] != null) {
            this.regionId = this.getLong(columnList, 5);
        }
        else {
            this.regionId = null;
        }
    }

    public RegionDao asRegion() {
        RegionDao returnRegion = new RegionDao();
        returnRegion.setId(this.regionId);
        returnRegion.setName(this.regionName);
        returnRegion.setSlug(this.regionSlug);
        returnRegion.setCountryCode(this.countryCode);
        returnRegion.setActiveBandsCount(this.activeBandsCount);
        returnRegion.setExtinctBandsCount(this.extinctBandsCount);
        returnRegion.setBandsCount(this.activeBandsCount + this.extinctBandsCount);

        return returnRegion;
    }
}
