package uk.co.bbr.services.venues;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.framework.mixins.SlugTools;

@Service
@RequiredArgsConstructor
public class VenueServiceImpl implements VenueService, SlugTools {
}
