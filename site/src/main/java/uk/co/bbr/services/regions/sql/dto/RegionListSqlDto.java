package uk.co.bbr.services.regions.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;
import uk.co.bbr.services.regions.dao.RegionDao;

@Getter
public class RegionListSqlDto extends AbstractSqlDto {

    private final Long regionId;
    private final String regionSlug;
    private final String regionName;
    private final String countryCode;
    private final Integer activeBandsCount;
    private final Integer extinctBandsCount;

    public RegionListSqlDto(Object[] columnList) {
        this.regionSlug = this.getString(columnList, 0);
        this.regionName = this.getString(columnList, 1);
        this.countryCode = this.getString(columnList, 2);
        this.activeBandsCount = this.getIntegerOrZero(columnList, 3);
        this.extinctBandsCount = this.getIntegerOrZero(columnList, 4);
        this.regionId = this.getLong(columnList, 5);
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
