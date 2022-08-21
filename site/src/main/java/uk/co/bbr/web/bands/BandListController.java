package uk.co.bbr.web.bands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.band.BandService;
import uk.co.bbr.services.band.dto.BandListDto;

@Controller
@RequiredArgsConstructor
public class BandListController {

    private final BandService bandService;

    @GetMapping("/bands")
    public String bandListHome(Model model) {
        return bandListLetter(model, "A");
    }

    @GetMapping("/bands/{letter:[A-Z0-9]{1}}")
    public String bandListLetter(Model model, @PathVariable("letter") String letter) {
        BandListDto bands = bandService.listBandsStartingWith(letter);

        model.addAttribute("BandPrefixLetter", letter);
        model.addAttribute("Bands", bands);
        return "bands/bands";
    }

    @GetMapping("/bands/ALL")
    public String bandListAll(Model model) {
        BandListDto bands = bandService.listBandsStartingWith("ALL");

        model.addAttribute("BandPrefixLetter", "ALL");
        model.addAttribute("Bands", bands);
        return "bands/bands";
    }


}
