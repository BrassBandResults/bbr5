package uk.co.bbr.web.bands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.sql.dto.BandWinnersSqlDto;
import uk.co.bbr.web.security.annotations.IsBbrMember;
import uk.co.bbr.web.security.annotations.IsBbrPro;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BandWinnersController {
    private final BandService bandService;

    @IsBbrPro
    @GetMapping("/bands/WINNERS")
    public String showBandWinners(Model model) {
        List<BandWinnersSqlDto> bandWinners = this.bandService.fetchContestWinningBands();

        model.addAttribute("WinningBands", bandWinners);

        return "bands/winners";
    }
}
