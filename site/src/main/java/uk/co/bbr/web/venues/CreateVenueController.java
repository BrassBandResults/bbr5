package uk.co.bbr.web.venues;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import uk.co.bbr.map.LocationService;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.venues.VenueService;
import uk.co.bbr.services.venues.dao.VenueDao;
import uk.co.bbr.web.people.forms.PersonEditForm;
import uk.co.bbr.web.security.annotations.IsBbrMember;
import uk.co.bbr.web.venues.forms.VenueEditForm;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class CreateVenueController {
    private final VenueService venueService;
    private final RegionService regionService;
    private final LocationService locationService;


    @IsBbrMember
    @GetMapping("/create/venue")
    public String createGet(Model model) {

        List<RegionDao> regions = this.regionService.findAll();

        VenueEditForm editForm = new VenueEditForm();
        editForm.setRegion(this.regionService.fetchUnknownRegion().getId());

        model.addAttribute("Form", editForm);
        model.addAttribute("Regions", regions);

        return "venues/create";
    }

    @IsBbrMember
    @PostMapping("/create/venue")
    public String createPost(Model model, @Valid @ModelAttribute("Form") VenueEditForm submittedForm, BindingResult bindingResult) {

        submittedForm.validate(bindingResult);

        if (bindingResult.hasErrors()) {
            return "venues/create";
        }

        VenueDao newVenue = new VenueDao();

        newVenue.setName(submittedForm.getName());
        if (submittedForm.getRegion() != null) {
            Optional<RegionDao> region = this.regionService.fetchById(submittedForm.getRegion());
            region.ifPresent(newVenue::setRegion);
        }
        newVenue.setLatitude(submittedForm.getLatitude());
        newVenue.setLongitude(submittedForm.getLongitude());
        newVenue.setNotes(submittedForm.getNotes());

        Optional<VenueDao> parent = this.venueService.fetchBySlug(submittedForm.getParentVenueSlug());
        if (parent.isPresent()) {
            newVenue.setParent(parent.get());
        }

        this.venueService.create(newVenue);
        this.locationService.updateVenueLocation(newVenue);

        return "redirect:/venues";
    }
}
