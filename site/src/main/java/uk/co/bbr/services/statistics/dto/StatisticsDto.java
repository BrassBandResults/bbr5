package uk.co.bbr.services.statistics.dto;

import lombok.Getter;
import lombok.Setter;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.venues.dao.VenueDao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
public class StatisticsDto {
    private int bandCount;
    private BandDao latestBand;
    private int bandsCompetedLastYear;
    private int bandsWithWebsite;
    private int bandsOnMap;
    private int extinctBandsOnMap;
    private int peopleCount;
    private PersonDao latestPerson;
    private int resultCount;
    private int resultsWithPlacingsCount;
    private int eventCount;
    private ContestEventDao latestEvent;
    private int contestCount;
    private ContestDao latestContest;
    private int pieceCount;
    private PieceDao latestPiece;
    private int venueCount;
    private int venuesOnMap;
    private VenueDao latestVenue;

    private int bandsRehearseOnMonday;
    private int bandsRehearseOnTuesday;
    private int bandsRehearseOnWednesday;
    private int bandsRehearseOnThursday;
    private int bandsRehearseOnFriday;
    private int bandsRehearseOnSaturday;
    private int bandsRehearseOnSunday;

    private int bandsWithRehearsalNight;


    public String getMondayPercent() {
        String percent = this.calcPercent(this.bandsRehearseOnMonday);
        return percent;
    }

    public String getTuesdayPercent() {
        return this.calcPercent(this.bandsRehearseOnTuesday);
    }

    public String getWednesdayPercent() {
        return this.calcPercent(this.bandsRehearseOnWednesday);
    }

    public String getThursdayPercent() {
        return this.calcPercent(this.bandsRehearseOnThursday);
    }

    public String getFridayPercent() {
        return this.calcPercent(this.bandsRehearseOnFriday);
    }

    public String getSaturdayPercent() {
        return this.calcPercent(this.bandsRehearseOnSaturday);
    }

    public String getSundayPercent() {
        return this.calcPercent(this.bandsRehearseOnSunday);
    }

    private String calcPercent(int dayCount) {
        if (this.bandsWithRehearsalNight == 0) {
            return "0.0";
        }
        BigDecimal day = new BigDecimal(dayCount);
        day = day.multiply(new BigDecimal(100));

        BigDecimal bands = new BigDecimal(this.bandsWithRehearsalNight);
        bands = bands.multiply(new BigDecimal(100));

        BigDecimal fraction = day.divide(bands);
        BigDecimal result = fraction.multiply(new BigDecimal(100));
        result = result.setScale(1, RoundingMode.HALF_UP);

        return result.toString();
    }
}
