package uk.co.bbr.services.people.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.types.ContestEventDateResolution;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;
import uk.co.bbr.services.regions.dao.RegionDao;

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
        this.leftResult = this.getInteger(columnList,0);
        this.leftResultType = this.getString(columnList, 1);
        this.rightResult = this.getInteger(columnList,2);
        this.rightResultType = this.getString(columnList, 3);
        this.eventDate = this.getLocalDate(columnList, 4);
        this.contestSlug = this.getString(columnList, 5);
        this.contestName = this.getString(columnList, 6);
        this.eventDateResolution = this.getString(columnList, 7);
        this.leftBandName = this.getString(columnList, 8);
        this.rightBandName = this.getString(columnList, 9);
        this.leftBandSlug = this.getString(columnList, 10);
        this.rightBandSlug = this.getString(columnList, 11);
        this.leftBandCurrentName = this.getString(columnList, 12);
        this.rightBandCurrentName = this.getString(columnList, 13);
        this.leftBandRegionSlug = this.getString(columnList, 14);
        this.leftBandRegionName = this.getString(columnList, 15);
        this.leftBandRegionCountryCode = this.getString(columnList, 16);
        this.rightBandRegionSlug = this.getString(columnList, 17);
        this.rightBandRegionName = this.getString(columnList, 18);
        this.rightBandRegionCountryCode = this.getString(columnList, 19);
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
        if (this.leftResult != null && this.rightResult != null && this.leftResult < this.rightResult) {
            return "bg-warning-subtle";
        }
        return "";
    }

    public String getRightClass() {
        if (this.rightResult != null && this.leftResult != null && this.rightResult < this.leftResult) {
            return "bg-warning-subtle";
        }
        return "";
    }
}
