package uk.co.bbr.web.bands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.bands.BandAliasService;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.dao.BandAliasDao;
import uk.co.bbr.services.bands.dto.BandDetailsDto;
import uk.co.bbr.services.contests.ContestGroupService;
import uk.co.bbr.services.contests.ContestResultService;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.ContestTagService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dao.ContestGroupDao;
import uk.co.bbr.services.contests.dao.ContestTagDao;
import uk.co.bbr.services.framework.NotFoundException;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class BandController {

    private final BandService bandService;
    private final BandAliasService bandAliasService;
    private final ContestService contestService;
    private final ContestTagService contestTagService;
    private final ContestGroupService contestGroupService;
    private final ContestResultService contestResultService;

    @GetMapping("/bands/{bandSlug:[\\-a-z\\d]{2,}}")
    public String bandDetail(Model model, @PathVariable("bandSlug") String bandSlug) {
        Optional<BandDao> band = this.bandService.fetchBySlug(bandSlug);
        if (band.isEmpty()) {
            throw NotFoundException.bandNotFoundBySlug(bandSlug);
        }

        List<BandAliasDao> previousNames = this.bandAliasService.findVisibleAliases(band.get());
        BandDetailsDto bandResults = this.contestResultService.findResultsForBand(band.get());

        if (bandResults.getBandResults().isEmpty() && !bandResults.getBandWhitResults().isEmpty()) {
            return "redirect:/bands/{bandSlug}/whits";
        }

        model.addAttribute("Band", band.get());
        model.addAttribute("PreviousNames", previousNames);
        model.addAttribute("BandResults", bandResults.getBandResults());
        model.addAttribute("ResultsCount", bandResults.getBandResults().size());
        model.addAttribute("WhitCount", bandResults.getBandWhitResults().size());
        return "bands/band";
    }

    @GetMapping("/bands/{bandSlug:[\\-a-z\\d]{2,}}/whits")
    public String bandWhitFridayDetail(Model model, @PathVariable("bandSlug") String bandSlug) {
        Optional<BandDao> band = this.bandService.fetchBySlug(bandSlug);
        if (band.isEmpty()) {
            throw NotFoundException.bandNotFoundBySlug(bandSlug);
        }

        List<BandAliasDao> previousNames = this.bandAliasService.findVisibleAliases(band.get());
        BandDetailsDto bandResults = this.contestResultService.findResultsForBand(band.get());

        if (bandResults.getBandWhitResults().isEmpty()) {
            return "redirect:/bands/{bandSlug}";
        }

        model.addAttribute("Band", band.get());
        model.addAttribute("PreviousNames", previousNames);
        model.addAttribute("BandResults", bandResults.getBandWhitResults());
        model.addAttribute("ResultsCount", bandResults.getBandResults().size());
        model.addAttribute("WhitCount", bandResults.getBandWhitResults().size());
        return "bands/band-whits";
    }

    @GetMapping("/bands/{bandSlug:[\\-a-z\\d]{2,}}/{contestSlug:[\\-a-z\\d]{2,}}")
    public String bandFilterToContest(Model model, @PathVariable("bandSlug") String bandSlug, @PathVariable("contestSlug") String contestSlug) {
        Optional<BandDao> band = this.bandService.fetchBySlug(bandSlug);
        if (band.isEmpty()) {
            throw NotFoundException.bandNotFoundBySlug(bandSlug);
        }
        Optional<ContestDao> contest = this.contestService.fetchBySlug(contestSlug);
        if (contest.isEmpty()) {
            throw NotFoundException.contestNotFoundBySlug(contestSlug);
        }

        List<BandAliasDao> previousNames = this.bandAliasService.findVisibleAliases(band.get());
        BandDetailsDto bandResults = this.contestResultService.findResultsForBand(band.get(), contest.get());

        model.addAttribute("Band", band.get());
        model.addAttribute("PreviousNames", previousNames);
        model.addAttribute("BandResults", bandResults.getBandResults());
        model.addAttribute("ResultsCount", bandResults.getBandResults().size());
        model.addAttribute("WhitCount", bandResults.getBandWhitResults().size());
        return "bands/band";
    }

    @GetMapping("/bands/{bandSlug:[\\-a-z\\d]{2,}}/{groupSlug:[\\-A-Z\\d]{2,}}")
    public String bandFilterToContestGroup(Model model, @PathVariable("bandSlug") String bandSlug, @PathVariable("groupSlug") String groupSlug) {
        Optional<BandDao> band = this.bandService.fetchBySlug(bandSlug);
        if (band.isEmpty()) {
            throw NotFoundException.bandNotFoundBySlug(bandSlug);
        }
        Optional<ContestGroupDao> group = this.contestGroupService.fetchBySlug(groupSlug);
        if (group.isEmpty()) {
            throw NotFoundException.groupNotFoundBySlug(groupSlug);
        }

        List<BandAliasDao> previousNames = this.bandAliasService.findVisibleAliases(band.get());
        BandDetailsDto bandResults = this.contestResultService.findResultsForBand(band.get(), group.get());

        model.addAttribute("Band", band.get());
        model.addAttribute("PreviousNames", previousNames);
        model.addAttribute("BandResults", bandResults.getBandResults());
        model.addAttribute("ResultsCount", bandResults.getBandResults().size());
        model.addAttribute("WhitCount", bandResults.getBandWhitResults().size());
        return "bands/band";
    }

    @GetMapping("/bands/{bandSlug:[\\-a-z\\d]{2,}}/tag/{tagSlug:[\\-a-z\\d]{2,}}")
    public String bandFilterToTag(Model model, @PathVariable("bandSlug") String bandSlug, @PathVariable("tagSlug") String tagSlug) {
        Optional<BandDao> band = this.bandService.fetchBySlug(bandSlug);
        if (band.isEmpty()) {
            throw NotFoundException.bandNotFoundBySlug(bandSlug);
        }
        Optional<ContestTagDao> tag = this.contestTagService.fetchBySlug(tagSlug);
        if (tag.isEmpty()) {
            throw NotFoundException.tagNotFoundBySlug(tagSlug);
        }

        List<BandAliasDao> previousNames = this.bandAliasService.findVisibleAliases(band.get());
        BandDetailsDto bandResults = this.contestResultService.findResultsForBand(band.get(), tag.get());

        model.addAttribute("Band", band.get());
        model.addAttribute("PreviousNames", previousNames);
        model.addAttribute("BandResults", bandResults.getBandResults());
        model.addAttribute("ResultsCount", bandResults.getBandResults().size());
        model.addAttribute("WhitCount", bandResults.getBandWhitResults().size());
        return "bands/band";
    }
}

