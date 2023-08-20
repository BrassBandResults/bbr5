package uk.co.bbr.web.bands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.types.ResultSetCategory;
import uk.co.bbr.services.events.BandResultService;
import uk.co.bbr.services.events.dto.ResultDetailsDto;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class DeleteBandController {

    private final BandService bandService;
    private final BandResultService bandResultService;

    @IsBbrMember
    @GetMapping("/bands/{bandSlug:[\\-a-z\\d]{2,}}/delete")
    public String deleteBand(Model model, @PathVariable("bandSlug") String bandSlug) {
        Optional<BandDao> band = this.bandService.fetchBySlug(bandSlug);
        if (band.isEmpty()) {
            throw NotFoundException.bandNotFoundBySlug(bandSlug);
        }

        ResultDetailsDto bandResults = this.bandResultService.findResultsForBand(band.get(), ResultSetCategory.ALL);

        boolean blocked = !bandResults.getBandAllResults().isEmpty();

        if (blocked) {
            model.addAttribute("Band", band.get());
            model.addAttribute("Results", bandResults.getBandAllResults());

            return "bands/delete-band-blocked";
        }

        this.bandService.delete(band.get());

        return "redirect:/bands";
    }
}
