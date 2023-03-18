package uk.co.bbr.web.bands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.dao.BandPreviousNameDao;
import uk.co.bbr.services.bands.dto.BandDetailsDto;
import uk.co.bbr.services.contests.ContestResultService;
import uk.co.bbr.services.contests.dao.ContestResultDao;
import uk.co.bbr.services.framework.NotFoundException;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class BandController {

    private final BandService bandService;
    private final ContestResultService contestResultService;

    @GetMapping("/bands/{slug:[\\-a-z\\d]{2,}}")
    public String bandDetail(Model model, @PathVariable("slug") String slug) {
        Optional<BandDao> band = this.bandService.fetchBySlug(slug);
        if (band.isEmpty()) {
            throw new NotFoundException("Band with slug " + slug + " not found");
        }

        List<BandPreviousNameDao> previousNames = this.bandService.findVisiblePreviousNames(band.get());
        BandDetailsDto bandResults = this.contestResultService.findResultsForBand(band.get());

        if (bandResults.getBandResults().size() == 0 && bandResults.getBandWhitResults().size() > 0) {
            return "redirect:/bands/" + slug + "/whits";
        }

        model.addAttribute("Band", band.get());
        model.addAttribute("PreviousNames", previousNames);
        model.addAttribute("BandResults", bandResults.getBandResults());
        model.addAttribute("ResultsCount", bandResults.getBandResults().size());
        model.addAttribute("WhitCount", bandResults.getBandWhitResults().size());
        return "bands/band";
    }

    @GetMapping("/bands/{slug:[\\-a-z\\d]{2,}}/whits")
    public String bandWhitFridayDetail(Model model, @PathVariable("slug") String slug) {
        Optional<BandDao> band = this.bandService.fetchBySlug(slug);
        if (band.isEmpty()) {
            throw new NotFoundException("Band with slug " + slug + " not found");
        }

        List<BandPreviousNameDao> previousNames = this.bandService.findVisiblePreviousNames(band.get());
        BandDetailsDto bandResults = this.contestResultService.findResultsForBand(band.get());

        if (bandResults.getBandWhitResults().size() == 0) {
            return "redirect:/bands/" + slug;
        }

        model.addAttribute("Band", band.get());
        model.addAttribute("PreviousNames", previousNames);
        model.addAttribute("BandResults", bandResults.getBandWhitResults());
        model.addAttribute("ResultsCount", bandResults.getBandResults().size());
        model.addAttribute("WhitCount", bandResults.getBandWhitResults().size());
        return "bands/band-whits";
    }

}
