package uk.co.bbr.services.people.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dao.ContestEventDao;
import uk.co.bbr.services.contests.types.ContestEventDateResolution;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;
import uk.co.bbr.services.regions.dao.RegionDao;

import java.sql.Date;
import java.time.LocalDate;

@Getter
public class CompareConductorsSqlDto extends AbstractSqlDto {

    private final Integer leftResult;
    private final String leftResultType;
    private final Integer rightResult;
    private final String rightResultType;
    private final LocalDate eventDate;
    private final String contestSlug;
    private final String contestName;
    private final String eventDateResolution;
    private final String leftBandName;
    private final String rightBandName;
    private final String leftBandSlug;
    private final String rightBandSlug;
    private final String leftBandCurrentName;
    private final String rightBandCurrentName;
    private final String leftBandRegionSlug;
    private final String leftBandRegionName;
    private final String leftBandRegionCountryCode;
    private final String rightBandRegionSlug;
    private final String rightBandRegionName;
    private final String rightBandRegionCountryCode;

    public CompareConductorsSqlDto(Object[] columnList) {
        this.leftResult = (Integer)columnList[0];
        this.leftResultType = (String)columnList[1];
        this.rightResult = (Integer)columnList[2];
        this.rightResultType = (String)columnList[3];
        Date tempEventDate = (Date)columnList[4];
        this.eventDate = tempEventDate.toLocalDate();
        this.contestSlug = (String)columnList[5];
        this.contestName = (String)columnList[6];
        this.eventDateResolution = (String)columnList[7];
        this.leftBandName = (String)columnList[8];
        this.rightBandName = (String)columnList[9];
        this.leftBandSlug = (String)columnList[10];
        this.rightBandSlug = (String)columnList[11];
        this.leftBandCurrentName = (String)columnList[12];
        this.rightBandCurrentName = (String)columnList[13];
        this.leftBandRegionSlug = (String)columnList[14];
        this.leftBandRegionName = (String)columnList[15];
        this.leftBandRegionCountryCode = (String)columnList[16];
        this.rightBandRegionSlug = (String)columnList[17];
        this.rightBandRegionName = (String)columnList[18];
        this.rightBandRegionCountryCode = (String)columnList[19];
    }

    public ContestEventDao getEvent() {
        ContestDao contest = new ContestDao();
        contest.setSlug(this.contestSlug);
        contest.setName(this.contestName);

        ContestEventDao event = new ContestEventDao();
        event.setEventDate(this.eventDate);
        event.setEventDateResolution(ContestEventDateResolution.fromCode(this.eventDateResolution));
        event.setContest(contest);

        return event;
    }

    public BandDao getLeftBand() {
        BandDao band = new BandDao();
        band.setName(this.leftBandCurrentName);
        band.setSlug(this.leftBandSlug);
        if (this.getLeftBandRegionSlug() != null) {
            RegionDao region = new RegionDao();
            region.setSlug(this.leftBandRegionSlug);
            region.setName(this.leftBandRegionName);
            region.setCountryCode(this.leftBandRegionCountryCode);
            band.setRegion(region);
        }
        return band;
    }

    public BandDao getRightBand() {
        BandDao band = new BandDao();
        band.setName(this.rightBandCurrentName);
        band.setSlug(this.rightBandSlug);
        if (this.getLeftBandRegionSlug() != null) {
            RegionDao region = new RegionDao();
            region.setSlug(this.rightBandRegionSlug);
            region.setName(this.rightBandRegionName);
            region.setCountryCode(this.rightBandRegionCountryCode);
            band.setRegion(region);
        }
        return band;
    }

    public String getLeftClass() {
        if (this.leftResult < this.rightResult) {
            return "bg-warning";
        }
        return "";
    }

    public String getRightClass() {
        if (this.rightResult < this.leftResult) {
            return "bg-warning";
        }
        return "";
    }
}
