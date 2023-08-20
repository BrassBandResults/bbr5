package uk.co.bbr.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.bbr.services.venues.VenueService;
import uk.co.bbr.services.venues.dao.VenueDao;

import java.util.Optional;

@RestController
@RequestMapping("/api/venues")
@RequiredArgsConstructor
public class VenueApiController {

    private final VenueService venueService;

    @GetMapping("/{slug}")
    public Optional<VenueDao> getVenue(@PathVariable("slug") String slug) {
        return this.venueService.fetchBySlug(slug);
    }
}
