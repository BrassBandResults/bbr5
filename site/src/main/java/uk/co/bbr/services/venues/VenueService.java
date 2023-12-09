package uk.co.bbr.services.venues;

import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.venues.dao.VenueAliasDao;
import uk.co.bbr.services.venues.dao.VenueDao;
import uk.co.bbr.services.venues.dto.VenueContestDto;
import uk.co.bbr.services.venues.dto.VenueContestYearDto;
import uk.co.bbr.services.venues.dto.VenueListDto;

import java.util.List;
import java.util.Optional;

public interface VenueService {
    VenueDao create(String name);

    VenueDao create(VenueDao venue);

    Optional<VenueAliasDao> aliasExists(VenueDao venue, String name);

    VenueAliasDao createAlias(VenueDao venue, VenueAliasDao previousName);

    Optional<VenueDao> fetchBySlug(String slug);

    VenueListDto listVenuesStartingWith(String prefix);

    VenueListDto listUnusedVenues();

    List<VenueAliasDao> fetchAliases(VenueDao venue);

    VenueDao update(VenueDao venue);

    List<VenueContestDto> fetchVenueContests(VenueDao venue);

    List<ContestEventDao> fetchVenueContestEvents(VenueDao venue, ContestDao contest);

    List<VenueContestYearDto> fetchVenueContestYears(VenueDao venue);

    List<ContestEventDao> fetchVenueContestYear(VenueDao venue, int year);

    void delete(VenueDao venue);
}
