package uk.co.bbr.web.venues;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.venues.VenueService;
import uk.co.bbr.services.venues.dao.VenueDao;
import uk.co.bbr.web.security.annotations.IsBbrMember;
import uk.co.bbr.web.venues.forms.VenueEditForm;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class EditVenueController {

    private final VenueService venueService;
    private final RegionService regionService;

    @IsBbrMember
    @GetMapping("/venues/{venueSlug:[\\-a-z\\d]{2,}}/edit")
    public String editContestGroupForm(Model model, @PathVariable("venueSlug") String venueSlug) {
        Optional<VenueDao> venue = this.venueService.fetchBySlug(venueSlug);
        if (venue.isEmpty()) {
            throw NotFoundException.venueNotFoundBySlug(venueSlug);
        }

        List<RegionDao> regions = this.regionService.findAll();

        VenueEditForm editForm = new VenueEditForm(venue.get());

        model.addAttribute("Venue", venue.get());
        model.addAttribute("Regions", regions);
        model.addAttribute("Form", editForm);

        return "venues/edit";
    }

    @IsBbrMember
    @PostMapping("/venues/{venueSlug:[\\-a-z\\d]{2,}}/edit")
    public String editContestGroupSave(Model model, @Valid @ModelAttribute("Form") VenueEditForm submittedVenue, BindingResult bindingResult, @PathVariable("venueSlug") String venueSlug) {
        Optional<VenueDao> venue = this.venueService.fetchBySlug(venueSlug);
        if (venue.isEmpty()) {
            throw NotFoundException.venueNotFoundBySlug(venueSlug);
        }

        submittedVenue.validate(bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("Venue", venue.get());
            return "venues/edit";
        }

        VenueDao existingVenue = venue.get();

        existingVenue.setName(submittedVenue.getName());
        existingVenue.setNotes(submittedVenue.getNotes());
        existingVenue.setLatitude(submittedVenue.getLatitude());
        existingVenue.setLongitude(submittedVenue.getLongitude());
        if (submittedVenue.getRegion() != null) {
            Optional<RegionDao> region = this.regionService.fetchById(submittedVenue.getRegion());
            region.ifPresent(existingVenue::setRegion);
        } else {
            existingVenue.setRegion(null);
        }
        if (submittedVenue.getParentVenueSlug() != null) {
            Optional<VenueDao> parentVenue = this.venueService.fetchBySlug(submittedVenue.getParentVenueSlug());
            parentVenue.ifPresent(existingVenue::setParent);
        } else {
            existingVenue.setParent(null);
        }

        this.venueService.update(existingVenue);

        return "redirect:/venues/{venueSlug}";
    }
}
