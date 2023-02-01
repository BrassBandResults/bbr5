package uk.co.bbr.web.venues;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.venues.VenueService;
import uk.co.bbr.services.venues.dto.VenueListDto;

@Controller
@RequiredArgsConstructor
public class VenueListController {

    private final VenueService venueService;

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

    @GetMapping("/venues/ALL")
    public String venuesListAll(Model model) {
        VenueListDto venues = this.venueService.listVenuesStartingWith("ALL");

        model.addAttribute("VenuePrefixLetter", "ALL");
        model.addAttribute("Venues", venues);
        return "venues/venues";
    }
}
