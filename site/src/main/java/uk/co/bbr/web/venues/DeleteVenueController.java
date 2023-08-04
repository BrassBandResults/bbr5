package uk.co.bbr.web.venues;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.ContestEventService;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.venues.VenueService;
import uk.co.bbr.services.venues.dao.VenueDao;
import uk.co.bbr.services.venues.dto.VenueContestDto;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class DeleteVenueController {

    private final VenueService venueService;

    @IsBbrMember
    @GetMapping("/venues/{venueSlug:[\\-a-z\\d]{2,}}/delete")
    public String deleteVenue(Model model, @PathVariable("venueSlug") String venueSlug) {
        Optional<VenueDao> venue = this.venueService.fetchBySlug(venueSlug);
        if (venue.isEmpty()) {
            throw NotFoundException.venueNotFoundBySlug(venueSlug);
        }

        List<VenueContestDto> venueContests = this.venueService.fetchVenueContests(venue.get());

        boolean blocked = venueContests.size() > 0;

        if (blocked) {
            model.addAttribute("Venue", venue.get());
            model.addAttribute("Contests", venueContests);

            return "venues/delete-venue-blocked";
        }

        this.venueService.delete(venue.get());

        return "redirect:/venues";
    }
}
