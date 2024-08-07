package uk.co.bbr.web.bands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.dto.BandListDto;
import uk.co.bbr.services.map.LocationService;
import uk.co.bbr.web.security.annotations.IsBbrAdmin;
import uk.co.bbr.web.security.annotations.IsBbrMember;
import uk.co.bbr.web.security.annotations.IsBbrSuperuser;

@Controller
@RequiredArgsConstructor
public class BandListController {

    private final BandService bandService;
    private final LocationService locationService;

    @GetMapping("/bands")
    public String bandListHome(Model model) {
        return bandListLetter(model, "A");
    }

    @GetMapping("/bands/{letter:[0A-Z]}")
    public String bandListLetter(Model model, @PathVariable("letter") String letter) {
        BandListDto bands = this.bandService.listBandsStartingWith(letter);

        model.addAttribute("BandPrefixLetter", letter);
        model.addAttribute("Bands", bands);
        return "bands/bands";
    }

    @IsBbrAdmin
    @GetMapping("/bands/{letter:[0A-Z]}/upload-locations")
    public String uploadBandLocations(@PathVariable("letter") String letter) {
        BandListDto bands = this.bandService.listBandsStartingWith(letter);

        for (BandDao band : bands.getReturnedBands()) {
            if (band.hasLocation()) {
                this.locationService.updateBandLocation(band);
            }
        }

        return "redirect:/bands";
    }

    @IsBbrMember
    @GetMapping("/bands/ALL")
    public String bandListAll(Model model) {
        BandListDto bands = this.bandService.listBandsStartingWith("ALL");

        model.addAttribute("BandPrefixLetter", "ALL");
        model.addAttribute("Bands", bands);
        return "bands/bands";
    }

    @IsBbrAdmin
    @GetMapping("/bands/ALL/upload-locations")
    public String uploadAllBandLocations() {
        BandListDto bands = this.bandService.listBandsStartingWith("ALL");

        for (BandDao band : bands.getReturnedBands()) {
            if (band.hasLocation()) {
                this.locationService.updateBandLocation(band);
            }
        }

        return "redirect:/bands";
    }

    @IsBbrSuperuser
    @GetMapping("/bands/UNUSED")
    public String bandListUnused(Model model) {
        BandListDto bands = this.bandService.listUnusedBands();

        model.addAttribute("BandPrefixLetter", "UNUSED");
        model.addAttribute("Bands", bands);
        return "bands/bands";
    }
}
