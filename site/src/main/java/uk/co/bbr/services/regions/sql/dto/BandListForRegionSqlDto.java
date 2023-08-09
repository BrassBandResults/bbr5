package uk.co.bbr.services.regions.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.types.BandStatus;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.sections.dao.SectionDao;

import java.math.BigInteger;

@Getter
public class BandListForRegionSqlDto extends AbstractSqlDto {

    private final String bandSlug;
    private final String bandName;
    private final String regionSlug;
    private final String regionName;
    private final String countryCode;
    private final Integer resultCount;
    private final Integer bandStatus;
    private final String bandSectionSlug;
    private final String bandSectionTranslationKey;

    public BandListForRegionSqlDto(Object[] columnList) {
        this.bandName = this.getString(columnList, 0);
        this.bandSlug = this.getString(columnList, 1);
        this.regionName = this.getString(columnList, 2);
        this.regionSlug = this.getString(columnList, 3);
        this.countryCode = this.getString(columnList, 4);
        if (columnList[5] != null) {
            this.resultCount = this.getInteger(columnList, 5);
        }
        else {
            this.resultCount = 0;
        }
        if (columnList[6] != null) {
            this.bandStatus = this.getInteger(columnList, 6);
        }
        else {
            this.bandStatus = 0;
        }
        this.bandSectionSlug = this.getString(columnList, 7);
        this.bandSectionTranslationKey = this.getString(columnList, 8);
    }

    public BandDao asBand() {
        BandDao returnBand = new BandDao();
        returnBand.setName(this.bandName);
        returnBand.setSlug(this.bandSlug);
        returnBand.setResultsCount(this.resultCount);
        returnBand.setStatus(BandStatus.fromCode(this.bandStatus));

        if (this.regionSlug != null && this.regionSlug.length() > 0) {
            returnBand.setRegion(new RegionDao());
            returnBand.getRegion().setSlug(this.regionSlug);
            returnBand.getRegion().setName(this.regionName);
            returnBand.getRegion().setCountryCode(this.countryCode);
        }

        if (this.bandSectionSlug != null && this.bandSectionSlug.length() > 0) {
            returnBand.setSection(new SectionDao());
            returnBand.getSection().setSlug(this.bandSectionSlug);
            returnBand.getSection().setTranslationKey(this.bandSectionTranslationKey);
        }

        return returnBand;
    }
}
