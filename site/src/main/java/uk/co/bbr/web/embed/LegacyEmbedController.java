package uk.co.bbr.web.embed;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.dto.BandDetailsDto;
import uk.co.bbr.services.bands.types.ResultSetCategory;
import uk.co.bbr.services.events.ContestResultService;
import uk.co.bbr.services.framework.NotFoundException;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class LegacyEmbedController {

    private final BandService bandService;
    private final ContestResultService contestResultService;

    @GetMapping("/embed/band/{bandSlug:[\\-a-z\\d]{2,}}/results/{version:\\d}")
    public String embedBandResults(Model model, @PathVariable("bandSlug") String bandSlug, @PathVariable("version") int version) {
        Optional<BandDao> band = this.bandService.fetchBySlug(bandSlug);
        if (band.isEmpty()) {
            throw NotFoundException.bandNotFoundBySlug(bandSlug);
        }

        BandDetailsDto bandResults = this.contestResultService.findResultsForBand(band.get(), ResultSetCategory.PAST);

        model.addAttribute("Band", band.get());
        model.addAttribute("BandSlugUnderscores", band.get().getSlug().replace("-", "_"));
        model.addAttribute("Results", bandResults.getBandAllResults());

        return "embed/band-legacy";
    }
}

