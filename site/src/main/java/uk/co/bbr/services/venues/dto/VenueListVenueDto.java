package uk.co.bbr.services.venues.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.co.bbr.services.regions.dao.RegionDao;

@Getter
@RequiredArgsConstructor
public class VenueListVenueDto {
    private final String slug;
    private final String name;
    private final RegionDao region;
    private final int eventCount;
}
