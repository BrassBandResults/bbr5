package uk.co.bbr.web.venues;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.venues.VenueService;
import uk.co.bbr.services.venues.dao.VenueAliasDao;
import uk.co.bbr.services.venues.dao.VenueDao;
import uk.co.bbr.services.venues.dto.VenueListDto;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class VenueController {

    private final VenueService venueService;

    @GetMapping("/venues/{venueSlug:[\\-a-z\\d]{2,}}")
    public String venue(Model model, @PathVariable("venueSlug") String venueSlug) {
        Optional<VenueDao> venue = this.venueService.fetchBySlug(venueSlug);
        if (venue.isEmpty()) {
            throw new NotFoundException("Venue with slug " + venueSlug + " not found");
        }
        List<VenueAliasDao> previousNames = this.venueService.fetchAliases(venue.get());

        model.addAttribute("Venue", venue.get());
        model.addAttribute("PreviousNames", previousNames);

        return "venues/venue";
    }

}
