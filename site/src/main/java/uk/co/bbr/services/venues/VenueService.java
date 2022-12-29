package uk.co.bbr.services.venues;

import uk.co.bbr.services.venues.dao.VenueDao;

public interface VenueService {
    VenueDao create(VenueDao newVenue);
    VenueDao create(String name);
}
