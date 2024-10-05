package uk.co.bbr.web.bands;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.co.bbr.services.bands.BandAliasService;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandAliasDao;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.web.bands.forms.BandAliasEditForm;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class BandAliasController {

    private final BandService bandService;
    private final BandAliasService bandAliasService;

    private static final String REDIRECT_TO_BAND_ALIASES = "redirect:/bands/{bandSlug}/edit-aliases";

    @IsBbrMember
    @GetMapping("/bands/{bandSlug:[\\-_a-z\\d]{2,}}/edit-aliases")
    public String bandAliasEdit(Model model, @PathVariable("bandSlug") String bandSlug) {
        Optional<BandDao> band = this.bandService.fetchBySlug(bandSlug);
        if (band.isEmpty()) {
            throw NotFoundException.bandNotFoundBySlug(bandSlug);
        }

        List<BandAliasDao> previousNames = this.bandAliasService.findAllAliases(band.get());

        model.addAttribute("Band", band.get());
        model.addAttribute("PreviousNames", previousNames);
        return "bands/band-aliases";
    }

    @IsBbrMember
    @GetMapping("/bands/{bandSlug:[\\-_a-z\\d]{2,}}/edit-aliases/{aliasId:\\d+}/hide")
    public String bandAliasHide(@PathVariable("bandSlug") String bandSlug, @PathVariable("aliasId") Long aliasId) {
        Optional<BandDao> band = this.bandService.fetchBySlug(bandSlug);
        if (band.isEmpty()) {
            throw NotFoundException.bandNotFoundBySlug(bandSlug);
        }

        this.bandAliasService.hideAlias(band.get(), aliasId);

        return REDIRECT_TO_BAND_ALIASES;
    }

    @IsBbrMember
    @GetMapping("/bands/{bandSlug:[\\-_a-z\\d]{2,}}/edit-aliases/{aliasId:\\d+}/show")
    public String bandAliasShow(@PathVariable("bandSlug") String bandSlug, @PathVariable("aliasId") Long aliasId) {
        Optional<BandDao> band = this.bandService.fetchBySlug(bandSlug);
        if (band.isEmpty()) {
            throw NotFoundException.bandNotFoundBySlug(bandSlug);
        }

        this.bandAliasService.showAlias(band.get(), aliasId);

        return REDIRECT_TO_BAND_ALIASES;
    }

    @IsBbrMember
    @GetMapping("/bands/{bandSlug:[\\-_a-z\\d]{2,}}/edit-aliases/{aliasId:\\d+}/delete")
    public String bandAliasDelete(@PathVariable("bandSlug") String bandSlug, @PathVariable("aliasId") Long aliasId) {
        Optional<BandDao> band = this.bandService.fetchBySlug(bandSlug);
        if (band.isEmpty()) {
            throw NotFoundException.bandNotFoundBySlug(bandSlug);
        }

        this.bandAliasService.deleteAlias(band.get(), aliasId);

        return REDIRECT_TO_BAND_ALIASES;
    }

    @IsBbrMember
    @PostMapping("/bands/{bandSlug:[\\-_a-z\\d]{2,}}/edit-aliases/add")
    public String bandAliasAdd(@PathVariable("bandSlug") String bandSlug, @RequestParam("oldName") String oldName) {
        Optional<BandDao> band = this.bandService.fetchBySlug(bandSlug);
        if (band.isEmpty()) {
            throw NotFoundException.bandNotFoundBySlug(bandSlug);
        }

        BandAliasDao previousName = new BandAliasDao();
        previousName.setOldName(oldName);
        previousName.setHidden(false);
        previousName.setStartDate(null);
        previousName.setEndDate(null);
        this.bandAliasService.createAlias(band.get(), previousName);

        return REDIRECT_TO_BAND_ALIASES;
    }

    @IsBbrMember
    @GetMapping("/bands/{bandSlug:[\\-_a-z\\d]{2,}}/edit-aliases/{aliasId:\\d+}/edit-dates")
    public String bandAliasEditDates(Model model, @PathVariable("bandSlug") String bandSlug, @PathVariable("aliasId") Long aliasId) {
        Optional<BandDao> band = this.bandService.fetchBySlug(bandSlug);
        if (band.isEmpty()) {
            throw NotFoundException.bandNotFoundBySlug(bandSlug);
        }
        Optional<BandAliasDao> bandAlias = this.bandAliasService.fetchAliasByBandAndId(band.get(), aliasId);
        if (bandAlias.isEmpty()) {
            throw NotFoundException.bandAliasNotFoundByIds(bandSlug, aliasId);
        }

        BandAliasEditForm bandAliasForm = new BandAliasEditForm(bandAlias.get());

        model.addAttribute("Band", band.get());
        model.addAttribute("BandAliasForm", bandAliasForm);
        model.addAttribute("BandAlias", bandAlias.get());

        return "bands/edit-alias";
    }

    @IsBbrMember
    @PostMapping("/bands/{bandSlug:[\\-_a-z\\d]{2,}}/edit-aliases/{aliasId:\\d+}/edit-dates")
    public String bandAliasEditDatesPost(Model model, @Valid @ModelAttribute("BandAliasForm") BandAliasEditForm aliasForm, BindingResult bindingResult, @PathVariable("bandSlug") String bandSlug, @PathVariable("aliasId") Long aliasId) {
        Optional<BandDao> band = this.bandService.fetchBySlug(bandSlug);
        if (band.isEmpty()) {
            throw NotFoundException.bandNotFoundBySlug(bandSlug);
        }
        Optional<BandAliasDao> bandAlias = this.bandAliasService.fetchAliasByBandAndId(band.get(), aliasId);
        if (bandAlias.isEmpty()) {
            throw NotFoundException.bandAliasNotFoundByIds(bandSlug, aliasId);
        }

        aliasForm.validate(bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("Band", band.get());
            model.addAttribute("BandAlias", bandAlias.get());

            return "bands/edit-alias";
        }

        BandAliasDao previousName = bandAlias.get();
        previousName.setStartDate(aliasForm.getStartDate());
        previousName.setEndDate(aliasForm.getEndDate());
        this.bandAliasService.updateAlias(band.get(), previousName);

        return REDIRECT_TO_BAND_ALIASES;
    }
}

