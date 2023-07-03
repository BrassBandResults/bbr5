package uk.co.bbr.services.statistics.dto;

import lombok.Getter;
import lombok.Setter;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.venues.dao.VenueDao;

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


        public int getMondayPercent() {
                return (this.bandsRehearseOnMonday * 100) / (bandsWithRehearsalNight * 100) * 100;
        }

        public int getTuesdayPercent() {
                return (this.bandsRehearseOnTuesday * 100) / (bandsWithRehearsalNight * 100) * 100;
        }

        public int getWednesdayPercent() {
                return (this.bandsRehearseOnWednesday * 100) / (bandsWithRehearsalNight * 100) * 100;
        }

        public int getThursdayPercent() {
                return (this.bandsRehearseOnThursday * 100) / (bandsWithRehearsalNight * 100) * 100;
        }

        public int getFridayPercent() {
                return (this.bandsRehearseOnFriday * 100) / (bandsWithRehearsalNight * 100) * 100;
        }

        public int getSaturdayPercent() {
                return (this.bandsRehearseOnSaturday * 100) / (bandsWithRehearsalNight * 100) * 100;
        }

        public int getSundayPercent() {
                return (this.bandsRehearseOnSunday * 100) / (bandsWithRehearsalNight * 100) * 100;
        }
}
