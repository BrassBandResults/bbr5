package uk.co.bbr.services.statistics;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.repo.BandRehearsalNightRepository;
import uk.co.bbr.services.bands.repo.BandRepository;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.repo.ContestRepository;
import uk.co.bbr.services.events.repo.ContestEventRepository;
import uk.co.bbr.services.events.repo.ContestResultRepository;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.people.repo.PersonRepository;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.pieces.repo.PieceRepository;
import uk.co.bbr.services.sections.dao.SectionDao;
import uk.co.bbr.services.sections.repo.SectionRepository;
import uk.co.bbr.services.statistics.dto.StatisticsDto;
import uk.co.bbr.services.venues.dao.VenueDao;
import uk.co.bbr.services.venues.repo.VenueRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final BandService bandService;
    private final BandRepository bandRepository;
    private final BandRehearsalNightRepository bandRehearsalNightRepository;
    private final PersonRepository personRepository;
    private final ContestResultRepository contestResultRepository;
    private final ContestEventRepository contestEventRepository;
    private final ContestRepository contestRepository;
    private final PieceRepository pieceRepository;
    private final VenueRepository venueRepository;


    @Override
    public StatisticsDto fetchStatistics() {
        StatisticsDto stats = new StatisticsDto();
        
        stats.setBandCount(this.bandRepository.countBands());
        stats.setLatestBand(this.bandRepository.fetchLatestBand().get(0));
        int lastYear = LocalDate.now().minus(1, ChronoUnit.YEARS).getYear();
        stats.setBandsCompetedLastYear(this.bandService.countBandsCompetedInYear(lastYear));
        stats.setBandsWithWebsite(this.bandRepository.countBandsWithWebsite());
        stats.setBandsOnMap(this.bandRepository.countBandsOnMap());
        stats.setExtinctBandsOnMap(this.bandRepository.countExtinctBandsOnMap());
        stats.setPeopleCount(this.personRepository.countPeople());
        stats.setLatestPerson(this.personRepository.fetchLatestPerson());
        stats.setResultCount(this.contestResultRepository.countResults());
        stats.setResultsWithPlacingsCount(this.contestResultRepository.countResultsWithPlacings());
        stats.setEventCount(this.contestEventRepository.countEvents());
        stats.setLatestEvent(this.contestEventRepository.fetchLatestEvent());
        stats.setContestCount(this.contestRepository.countContests());
        stats.setLatestContest(this.contestRepository.fetchLatestContest());
        stats.setPieceCount(this.pieceRepository.countPieces());
        stats.setLatestPiece(this.pieceRepository.fetchLatestPiece());
        stats.setVenueCount(this.venueRepository.countVenues());
        stats.setVenuesOnMap(this.venueRepository.countVenuesOnMap());
        stats.setLatestVenue(this.venueRepository.fetchLatestVenue());

        stats.setBandsRehearseOnMonday(this.bandRehearsalNightRepository.countBandsOnMonday());
        stats.setBandsRehearseOnTuesday(this.bandRehearsalNightRepository.countBandsOnTuesday());
        stats.setBandsRehearseOnWednesday(this.bandRehearsalNightRepository.countBandsOnWednesday());
        stats.setBandsRehearseOnThursday(this.bandRehearsalNightRepository.countBandsOnThursday());
        stats.setBandsRehearseOnFriday(this.bandRehearsalNightRepository.countBandsOnFriday());
        stats.setBandsRehearseOnSaturday(this.bandRehearsalNightRepository.countBandsOnSaturday());
        stats.setBandsRehearseOnSunday(this.bandRehearsalNightRepository.countBandsOnSunday());

        stats.setBandsWithRehearsalNight(this.bandRehearsalNightRepository.fetchBandCount());

        return stats;
    }
}
