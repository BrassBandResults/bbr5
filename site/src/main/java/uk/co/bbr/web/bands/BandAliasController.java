package uk.co.bbr.web.bands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.dao.BandPreviousNameDao;
import uk.co.bbr.services.framework.NotFoundException;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class BandAliasController {

    private final BandService bandService;

    private static final String REDIRECT_TO_BAND_ALIASES = "redirect:/bands/{bandSlug}/edit-aliases";

    @GetMapping("/bands/{bandSlug:[\\-a-z\\d]{2,}}/edit-aliases")
    public String bandAliasEdit(Model model, @PathVariable("bandSlug") String bandSlug) {
        Optional<BandDao> band = this.bandService.fetchBySlug(bandSlug);
        if (band.isEmpty()) {
            throw NotFoundException.bandNotFoundBySlug(bandSlug);
        }

        List<BandPreviousNameDao> previousNames = this.bandService.findAllPreviousNames(band.get());

        model.addAttribute("Band", band.get());
        model.addAttribute("PreviousNames", previousNames);
        return "bands/band-aliases";
    }

    @GetMapping("/bands/{bandSlug:[\\-a-z\\d]{2,}}/edit-aliases/{aliasId:\\d+}/hide")
    public String bandAliasHide(@PathVariable("bandSlug") String bandSlug, @PathVariable("aliasId") Long aliasId) {
        Optional<BandDao> band = this.bandService.fetchBySlug(bandSlug);
        if (band.isEmpty()) {
            throw NotFoundException.bandNotFoundBySlug(bandSlug);
        }

        this.bandService.hidePreviousBandName(band.get(), aliasId);

        return REDIRECT_TO_BAND_ALIASES;
    }

    @GetMapping("/bands/{bandSlug:[\\-a-z\\d]{2,}}/edit-aliases/{aliasId:\\d+}/show")
    public String bandAliasShow(@PathVariable("bandSlug") String bandSlug, @PathVariable("aliasId") Long aliasId) {
        Optional<BandDao> band = this.bandService.fetchBySlug(bandSlug);
        if (band.isEmpty()) {
            throw NotFoundException.bandNotFoundBySlug(bandSlug);
        }

        this.bandService.showPreviousBandName(band.get(), aliasId);

        return REDIRECT_TO_BAND_ALIASES;
    }

    @GetMapping("/bands/{bandSlug:[\\-a-z\\d]{2,}}/edit-aliases/{aliasId:\\d+}/delete")
    public String bandAliasDelete(@PathVariable("bandSlug") String bandSlug, @PathVariable("aliasId") Long aliasId) {
        Optional<BandDao> band = this.bandService.fetchBySlug(bandSlug);
        if (band.isEmpty()) {
            throw NotFoundException.bandNotFoundBySlug(bandSlug);
        }

        this.bandService.deletePreviousBandName(band.get(), aliasId);

        return REDIRECT_TO_BAND_ALIASES;
    }

    @PostMapping("/bands/{bandSlug:[\\-a-z\\d]{2,}}/edit-aliases/add")
    public String bandAliasShow(@PathVariable("bandSlug") String bandSlug, @RequestParam("oldName") String oldName) {
        Optional<BandDao> band = this.bandService.fetchBySlug(bandSlug);
        if (band.isEmpty()) {
            throw NotFoundException.bandNotFoundBySlug(bandSlug);
        }

        BandPreviousNameDao previousName = new BandPreviousNameDao();
        previousName.setOldName(oldName);
        previousName.setHidden(false);
        previousName.setStartDate(null);
        previousName.setEndDate(null);
        this.bandService.createPreviousName(band.get(), previousName);

        return REDIRECT_TO_BAND_ALIASES;
    }
}

