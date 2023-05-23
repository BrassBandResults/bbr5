package uk.co.bbr.web.bands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.types.BandStatus;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.web.bands.forms.BandEditForm;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class EditBandController {

    private final BandService bandService;
    private final RegionService regionService;

    @IsBbrMember
    @GetMapping("/bands/{bandSlug:[\\-a-z\\d]{2,}}/edit")
    public String editBandForm(Model model, @PathVariable("bandSlug") String bandSlug) {
        Optional<BandDao> band = this.bandService.fetchBySlug(bandSlug);
        if (band.isEmpty()) {
            throw NotFoundException.bandNotFoundBySlug(bandSlug);
        }

        BandEditForm bandEditDto = new BandEditForm(band.get());

        List<RegionDao> regions = this.regionService.findAll();

        model.addAttribute("Band", band.get());
        model.addAttribute("BandForm", bandEditDto);
        model.addAttribute("Regions", regions);

        return "bands/edit";
    }

    @IsBbrMember
    @PostMapping("/bands/{bandSlug:[\\-a-z\\d]{2,}}/edit")
    public String editBandSave(Model model, @Valid @ModelAttribute("BandForm") BandEditForm submittedBand, BindingResult bindingResult, @PathVariable("bandSlug") String bandSlug) {
        Optional<BandDao> existingBandOptional = this.bandService.fetchBySlug(bandSlug);
        if (existingBandOptional.isEmpty()) {
            throw NotFoundException.bandNotFoundBySlug(bandSlug);
        }

        submittedBand.validate(bindingResult);

        if (bindingResult.hasErrors()) {
            List<RegionDao> regions = this.regionService.findAll();

            model.addAttribute("Band", existingBandOptional.get());
            model.addAttribute("Regions", regions);

            return "/bands/edit";
        }

        BandDao existingBand = existingBandOptional.get();

        existingBand.setName(submittedBand.getName());
        existingBand.setLatitude(submittedBand.getLatitude());
        existingBand.setLongitude(submittedBand.getLongitude());
        existingBand.setWebsite(submittedBand.getWebsite());
        existingBand.setTwitterName(submittedBand.getTwitter());
        existingBand.setStartDate(submittedBand.getStartDate());
        existingBand.setEndDate(submittedBand.getEndDate());
        existingBand.setNotes(submittedBand.getNotes());

        // region
        Optional<RegionDao> newRegion = this.regionService.fetchById(submittedBand.getRegion());
        if (newRegion.isPresent()) {
            existingBand.setRegion(newRegion.get());
        }

        // status
        BandStatus newStatus = BandStatus.fromCode(submittedBand.getStatus());
        existingBand.setStatus(newStatus);

        this.bandService.update(existingBand);

        return "redirect:/bands/{bandSlug}";
    }
}
