package uk.co.bbr.web.venues;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.venues.VenueAliasService;
import uk.co.bbr.services.venues.VenueService;
import uk.co.bbr.services.venues.dao.VenueAliasDao;
import uk.co.bbr.services.venues.dao.VenueDao;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class VenueAliasController {

    private final VenueService venueService;
    private final VenueAliasService venueAliasService;

    private static final String REDIRECT_TO_VENUE_ALIASES = "redirect:/venues/{venueSlug}/edit-aliases";

    @IsBbrMember
    @GetMapping("/venues/{venueSlug:[\\-_a-z\\d]{2,}}/edit-aliases")
    public String venueAliasEdit(Model model, @PathVariable("venueSlug") String venueSlug) {
        Optional<VenueDao> venue = this.venueService.fetchBySlug(venueSlug);
        if (venue.isEmpty()) {
            throw NotFoundException.venueNotFoundBySlug(venueSlug);
        }

        List<VenueAliasDao> previousNames = this.venueAliasService.findAllAliases(venue.get());

        model.addAttribute("Venue", venue.get());
        model.addAttribute("PreviousNames", previousNames);
        return "venues/venue-aliases";
    }

    @IsBbrMember
    @GetMapping("/venues/{venueSlug:[\\-_a-z\\d]{2,}}/edit-aliases/{aliasId:\\d+}/delete")
    public String venueAliasDelete(@PathVariable("venueSlug") String venueSlug, @PathVariable("aliasId") Long aliasId) {
        Optional<VenueDao> venue = this.venueService.fetchBySlug(venueSlug);
        if (venue.isEmpty()) {
            throw NotFoundException.personNotFoundBySlug(venueSlug);
        }

        this.venueAliasService.deleteAlias(venue.get(), aliasId);

        return REDIRECT_TO_VENUE_ALIASES;
    }

    @IsBbrMember
    @PostMapping("/venues/{venueSlug:[\\-_a-z\\d]{2,}}/edit-aliases/add")
    public String venueAliasShow(@PathVariable("venueSlug") String venueSlug, @RequestParam("name") String oldName) {
        Optional<VenueDao> venue = this.venueService.fetchBySlug(venueSlug);
        if (venue.isEmpty()) {
            throw NotFoundException.personNotFoundBySlug(venueSlug);
        }

        VenueAliasDao previousName = new VenueAliasDao();
        previousName.setName(oldName);
        this.venueAliasService.createAlias(venue.get(), previousName);

        return REDIRECT_TO_VENUE_ALIASES;
    }
}

