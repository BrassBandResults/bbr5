package uk.co.bbr.web.venues;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.map.LocationService;
import uk.co.bbr.services.venues.VenueService;
import uk.co.bbr.services.venues.dao.VenueDao;
import uk.co.bbr.services.venues.dto.VenueListDto;
import uk.co.bbr.web.security.annotations.IsBbrAdmin;
import uk.co.bbr.web.security.annotations.IsBbrMember;
import uk.co.bbr.web.security.annotations.IsBbrSuperuser;

@Controller
@RequiredArgsConstructor
public class VenueListController {

    private final VenueService venueService;
    private final LocationService locationService;

    @GetMapping("/venues")
    public String venueListHome(Model model) {
        return venueListLetter(model, "A");
    }

    @GetMapping("/venues/{letter:[0A-Z]}")
    public String venueListLetter(Model model, @PathVariable("letter") String letter) {
        VenueListDto venues = this.venueService.listVenuesStartingWith(letter);

        model.addAttribute("VenuePrefixLetter", letter);
        model.addAttribute("Venues", venues);
        return "venues/venues";
    }

    @IsBbrAdmin
    @GetMapping("/venues/{letter:[0A-Z]}/upload-locations")
    public String uploadVenueLocations( @PathVariable("letter") String letter) {
        VenueListDto venues = this.venueService.listVenuesStartingWith(letter);

        for (VenueDao venue : venues.getReturnedVenues()) {
            if (venue.hasLocation()) {
                this.locationService.updateVenueLocation(venue);
            }
        }

        return "redirect:/venues";
    }



    @IsBbrMember
    @GetMapping("/venues/ALL")
    public String venuesListAll(Model model) {
        VenueListDto venues = this.venueService.listVenuesStartingWith("ALL");

        model.addAttribute("VenuePrefixLetter", "ALL");
        model.addAttribute("Venues", venues);
        return "venues/venues";
    }

    @IsBbrSuperuser
    @GetMapping("/venues/UNUSED")
    public String venuesListUnused(Model model) {
        VenueListDto venues = this.venueService.listUnusedVenues();
        model.addAttribute("VenuePrefixLetter", "UNUSED");
        model.addAttribute("Venues", venues);
        return "venues/venues";
    }

    @IsBbrSuperuser
    @GetMapping("/venues/NOLOCATION")
    public String venuesListNoLocation(Model model) {
        VenueListDto venues = this.venueService.listVenuesWithNoLocation();
        model.addAttribute("VenuePrefixLetter", "NOLOCATION");
        model.addAttribute("Venues", venues);
        return "venues/venues-no-location";
    }

    @IsBbrAdmin
    @GetMapping("/venues/ALL/upload-locations")
    public String uploadAllVenueLocations() {
        VenueListDto venues = this.venueService.listVenuesStartingWith("ALL");

        for (VenueDao venue : venues.getReturnedVenues()) {
            if (venue.hasLocation()) {
                this.locationService.updateVenueLocation(venue);
            }
        }

        return "redirect:/venues";
    }
}
