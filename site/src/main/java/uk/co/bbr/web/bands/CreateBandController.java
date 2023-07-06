package uk.co.bbr.web.bands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.types.BandStatus;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.web.bands.forms.BandEditForm;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class CreateBandController {
    private final BandService bandService;
    private final RegionService regionService;


    @IsBbrMember
    @GetMapping("/create/band")
    public String createGet(Model model) {

        List<RegionDao> regions = this.regionService.findAll();
        BandEditForm bandEditForm = new BandEditForm();
        bandEditForm.setRegion(this.regionService.fetchUnknownRegion().getId());

        model.addAttribute("BandForm", bandEditForm);
        model.addAttribute("Regions", regions);

        return "bands/create";
    }

    @IsBbrMember
    @PostMapping("/create/band")
    public String createPost(Model model, @Valid @ModelAttribute("BandForm") BandEditForm submittedForm, BindingResult bindingResult) {

        List<RegionDao> regions = this.regionService.findAll();
        model.addAttribute("Regions", regions);

        submittedForm.validate(bindingResult);

        if (bindingResult.hasErrors()) {
            return "bands/create";
        }

        BandDao newBand = new BandDao();

        if (submittedForm.getRegion() != null) {
            Optional<RegionDao> region = this.regionService.fetchById(submittedForm.getRegion());
            region.ifPresent(newBand::setRegion);
        }

        newBand.setName(submittedForm.getName());
        newBand.setLatitude(submittedForm.getLatitude());
        newBand.setLongitude(submittedForm.getLongitude());
        newBand.setWebsite(submittedForm.getWebsite());
        if (submittedForm.getStatus() != null) {
            newBand.setStatus(BandStatus.fromCode(submittedForm.getStatus()));
        }
        newBand.setStartDate(submittedForm.getStartDate());
        newBand.setEndDate(submittedForm.getEndDate());
        newBand.setNotes(submittedForm.getNotes());

        this.bandService.create(newBand);

        return "redirect:/bands";
    }
}
