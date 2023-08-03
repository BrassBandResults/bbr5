package uk.co.bbr.services.bands.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.types.BandStatus;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.sections.dao.SectionDao;

import java.math.BigInteger;

@Getter
public class RegionBandSqlDto extends AbstractSqlDto {

    private final String bandSlug;
    private final String bandName;
    private final Integer bandStatus;
    private final String sectionSlug;
    private final String sectionName;
    private final String sectionTranslationKey;
    private final String sectionMapShortCode;
    private final String bandLongitude;
    private final String bandLatitude;
    private final String bandWebsite;
    private final boolean sunRehearsal;
    private final boolean monRehearsal;
    private final boolean tueRehearsal;
    private final boolean wedRehearsal;
    private final boolean thuRehearsal;
    private final boolean friRehearsal;
    private final boolean satRehearsal;


    public RegionBandSqlDto(Object[] columnList) {
        this.bandSlug = (String)columnList[0];
        this.bandName = (String)columnList[1];
        this.bandStatus =  this.getInteger(columnList, 2);
        this.sectionSlug = (String)columnList[3];
        this.sectionName = (String)columnList[4];
        this.sectionTranslationKey = (String)columnList[5];
        this.sectionMapShortCode = (String)columnList[6];
        this.bandLongitude = (String)columnList[7];
        this.bandLatitude = (String)columnList[8];
        this.bandWebsite = (String)columnList[9];

        this.sunRehearsal = this.fetchRehearsal(columnList[10]);
        this.monRehearsal = this.fetchRehearsal(columnList[11]);
        this.tueRehearsal = this.fetchRehearsal(columnList[12]);
        this.wedRehearsal = this.fetchRehearsal(columnList[13]);
        this.thuRehearsal = this.fetchRehearsal(columnList[14]);
        this.friRehearsal = this.fetchRehearsal(columnList[15]);
        this.satRehearsal = this.fetchRehearsal(columnList[16]);
    }

    private boolean fetchRehearsal(Object column) {
        if (column == null) {
            return false;
        }
        int value = column instanceof BigInteger ? ((BigInteger)column).intValue() : (Integer)column;
        return value > 0;
    }

    public BandDao getBand() {
        BandDao returnBand = new BandDao();
        returnBand.setName(this.bandName);
        returnBand.setSlug(this.bandSlug);
        returnBand.setStatus(BandStatus.fromCode(this.bandStatus));
        returnBand.setLongitude(this.bandLongitude);
        returnBand.setLatitude(this.bandLatitude);
        returnBand.setWebsite(this.bandWebsite);
        if (this.sectionSlug != null) {
            returnBand.setSection(new SectionDao());
            returnBand.getSection().setSlug(this.sectionSlug);
            returnBand.getSection().setName(this.sectionName);
            returnBand.getSection().setTranslationKey(this.sectionTranslationKey);
            returnBand.getSection().setMapShortCode(this.sectionMapShortCode);
        }
        StringBuilder rehearsalsBinary = new StringBuilder();
        rehearsalsBinary.append(this.monRehearsal ? "1" : "0");
        rehearsalsBinary.append(this.tueRehearsal ? "1" : "0");
        rehearsalsBinary.append(this.wedRehearsal ? "1" : "0");
        rehearsalsBinary.append(this.thuRehearsal ? "1" : "0");
        rehearsalsBinary.append(this.friRehearsal ? "1" : "0");
        rehearsalsBinary.append(this.satRehearsal ? "1" : "0");
        rehearsalsBinary.append(this.sunRehearsal ? "1" : "0");
        returnBand.setRehearsalsBinary(rehearsalsBinary.toString());
        return returnBand;
    }
}
