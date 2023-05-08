package uk.co.bbr.services.venues.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.co.bbr.services.venues.dao.VenueDao;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class VenueListDto {
    private final int returnedVenuesCount;
    private final long allVenuesCount;
    private final String searchPrefix;
    private final List<VenueDao> returnedVenues;
}
