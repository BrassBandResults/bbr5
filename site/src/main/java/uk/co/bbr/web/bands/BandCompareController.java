package uk.co.bbr.web.bands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.dto.BandCompareDto;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.people.dto.ConductorCompareDto;
import uk.co.bbr.web.security.annotations.IsBbrMember;
import uk.co.bbr.web.security.annotations.IsBbrPro;


import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class BandCompareController {
    private final BandService bandService;
    private final ContestService contestService;

    @IsBbrPro
    @GetMapping("/bands/COMPARE")
    public String compareBandsHome(Model model) {
        return "bands/compare/select";
    }

    @IsBbrPro
    @GetMapping("/bands/COMPARE/{leftSlug:[\\-a-z\\d]{2,}}")
    public String compareConductorToAnother(Model model, @PathVariable("leftSlug") String leftSlug) {
        Optional<BandDao> leftBand = this.bandService.fetchBySlug(leftSlug);
        if (leftBand.isEmpty()) {
            throw NotFoundException.bandNotFoundBySlug(leftSlug);
        }

        model.addAttribute("LeftBand", leftBand.get());

        return "bands/compare/select-one";
    }

    @IsBbrPro
    @GetMapping("/bands/COMPARE/{leftSlug:[\\-a-z\\d]{2,}}/{rightSlug:[\\-a-z\\d]{2,}}")
    public String compareBandsDisplay(Model model, @PathVariable("leftSlug") String leftSlug, @PathVariable("rightSlug") String rightSlug) {
        Optional<BandDao> leftBand = this.bandService.fetchBySlug(leftSlug);
        if (leftBand.isEmpty()) {
            throw NotFoundException.bandNotFoundBySlug(leftSlug);
        }

        Optional<BandDao> rightBand = this.bandService.fetchBySlug(rightSlug);
        if (rightBand.isEmpty()) {
            throw NotFoundException.bandNotFoundBySlug(rightSlug);
        }

        BandCompareDto compareBands = this.bandService.compareBands(leftBand.get(), rightBand.get());

        model.addAttribute("LeftBand", leftBand.get());
        model.addAttribute("RightBand", rightBand.get());
        model.addAttribute("LeftBandWins", compareBands.getLeftBandWins());
        model.addAttribute("RightBandWins", compareBands.getRightBandWins());
        model.addAttribute("LeftBandPercent", compareBands.getLeftBandPercent());
        model.addAttribute("RightBandPercent", compareBands.getRightBandPercent());
        model.addAttribute("Results", compareBands.getResults());

        return "bands/compare/comparison";
    }

    @IsBbrPro
    @GetMapping("/bands/COMPARE/{leftSlug:[\\-a-z\\d]{2,}}/{rightSlug:[\\-a-z\\d]{2,}}/{contestSlug:[\\-a-z\\d]{2,}}")
    public String compareBandsDisplayFilterToContest(Model model, @PathVariable("leftSlug") String leftSlug, @PathVariable("rightSlug") String rightSlug, @PathVariable("contestSlug") String contestSlug) {
        Optional<BandDao> leftBand = this.bandService.fetchBySlug(leftSlug);
        if (leftBand.isEmpty()) {
            throw NotFoundException.bandNotFoundBySlug(leftSlug);
        }

        Optional<BandDao> rightBand = this.bandService.fetchBySlug(rightSlug);
        if (rightBand.isEmpty()) {
            throw NotFoundException.bandNotFoundBySlug(rightSlug);
        }

        Optional<ContestDao> contest = this.contestService.fetchBySlug(contestSlug);
        if (contest.isEmpty()) {
            throw NotFoundException.contestNotFoundBySlug(contestSlug);
        }

        BandCompareDto compareBands = this.bandService.compareBands(leftBand.get(), rightBand.get());
        BandCompareDto filteredResults = new BandCompareDto(compareBands.getResults(), contestSlug);

        model.addAttribute("LeftBand", leftBand.get());
        model.addAttribute("RightBand", rightBand.get());
        model.addAttribute("LeftBandWins", filteredResults.getLeftBandWins());
        model.addAttribute("RightBandWins", filteredResults.getRightBandWins());
        model.addAttribute("LeftBandPercent", filteredResults.getLeftBandPercent());
        model.addAttribute("RightBandPercent", filteredResults.getRightBandPercent());
        model.addAttribute("Results", filteredResults.getResults());
        model.addAttribute("FilteredTo", contest.get().getName());

        return "bands/compare/comparison";
    }
}
