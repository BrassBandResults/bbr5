package uk.co.bbr.web.bands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.bands.BandAliasService;
import uk.co.bbr.services.bands.BandRehearsalsService;
import uk.co.bbr.services.bands.BandRelationshipService;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandAliasDao;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.dao.BandRehearsalDayDao;
import uk.co.bbr.services.bands.dao.BandRelationshipDao;
import uk.co.bbr.services.bands.types.ResultSetCategory;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.BandResultService;
import uk.co.bbr.services.events.dto.ResultDetailsDto;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.groups.ContestGroupService;
import uk.co.bbr.services.groups.dao.ContestGroupDao;
import uk.co.bbr.services.tags.ContestTagService;
import uk.co.bbr.services.tags.dao.ContestTagDao;
import uk.co.bbr.web.security.annotations.IsBbrMember;
import uk.co.bbr.web.security.annotations.IsBbrPro;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class BandController {

    private final BandService bandService;
    private final BandRehearsalsService bandRehearsalsService;
    private final BandRelationshipService bandRelationshipService;
    private final BandAliasService bandAliasService;
    private final ContestService contestService;
    private final ContestTagService contestTagService;
    private final ContestGroupService contestGroupService;
    private final BandResultService bandResultService;

    @GetMapping("/bands/{bandSlug:[\\-a-z\\d]{2,}}")
    public String bandDetail(Model model, @PathVariable("bandSlug") String bandSlug) {
        Optional<BandDao> band = this.bandService.fetchBySlug(bandSlug);
        if (band.isEmpty()) {
            throw NotFoundException.bandNotFoundBySlug(bandSlug);
        }

        ResultDetailsDto bandResults = this.bandResultService.findResultsForBand(band.get(), ResultSetCategory.ALL);

        if (bandResults.getBandNonWhitResults().isEmpty() && !bandResults.getBandWhitResults().isEmpty()) {
            return "redirect:/bands/{bandSlug}/whits";
        }

        List<BandRehearsalDayDao> bandRehearsalDays = this.bandRehearsalsService.fetchRehearsalDays(band.get());
        List<BandAliasDao> previousNames = this.bandAliasService.findVisibleAliases(band.get());
        List<BandRelationshipDao> bandRelationships = this.bandRelationshipService.fetchRelationshipsForBand(band.get());

        model.addAttribute("Band", band.get());
        model.addAttribute("PreviousNames", previousNames);
        model.addAttribute("BandResults", bandResults.getBandNonWhitResults());
        model.addAttribute("ResultsCount", bandResults.getBandNonWhitResults().size());
        model.addAttribute("WhitCount", bandResults.getBandWhitResults().size());
        model.addAttribute("BandRehearsalDays", bandRehearsalDays);
        model.addAttribute("BandRelationships", bandRelationships);
        return "bands/band";
    }

    @GetMapping("/bands/{bandSlug:[\\-a-z\\d]{2,}}/whits")
    public String bandWhitFridayDetail(Model model, @PathVariable("bandSlug") String bandSlug) {
        Optional<BandDao> band = this.bandService.fetchBySlug(bandSlug);
        if (band.isEmpty()) {
            throw NotFoundException.bandNotFoundBySlug(bandSlug);
        }

        ResultDetailsDto bandResults = this.bandResultService.findResultsForBand(band.get(), ResultSetCategory.ALL);

        if (bandResults.getBandWhitResults().isEmpty()) {
            return "redirect:/bands/{bandSlug}";
        }

        List<BandRehearsalDayDao> bandRehearsalDays = this.bandRehearsalsService.fetchRehearsalDays(band.get());
        List<BandAliasDao> previousNames = this.bandAliasService.findVisibleAliases(band.get());
        List<BandRelationshipDao> bandRelationships = this.bandRelationshipService.fetchRelationshipsForBand(band.get());

        model.addAttribute("Band", band.get());
        model.addAttribute("PreviousNames", previousNames);
        model.addAttribute("BandResults", bandResults.getBandWhitResults());
        model.addAttribute("ResultsCount", bandResults.getBandNonWhitResults().size());
        model.addAttribute("WhitCount", bandResults.getBandWhitResults().size());
        model.addAttribute("BandRehearsalDays", bandRehearsalDays);
        model.addAttribute("BandRelationships", bandRelationships);
        return "bands/band-whits";
    }

    @IsBbrMember
    @GetMapping("/bands/{bandSlug:[\\-a-z\\d]{2,}}/map")
    public String bandNearbyMap(Model model, @PathVariable("bandSlug") String bandSlug) {
        Optional<BandDao> band = this.bandService.fetchBySlug(bandSlug);
        if (band.isEmpty()) {
            throw NotFoundException.bandNotFoundBySlug(bandSlug);
        }

        if (!band.get().hasLocation()) {
            return "redirect:/bands/{bandSlug}";
        }

        ResultDetailsDto bandResults = this.bandResultService.findResultsForBand(band.get(), ResultSetCategory.ALL);
        List<BandRehearsalDayDao> bandRehearsalDays = this.bandRehearsalsService.fetchRehearsalDays(band.get());
        List<BandAliasDao> previousNames = this.bandAliasService.findVisibleAliases(band.get());
        List<BandRelationshipDao> bandRelationships = this.bandRelationshipService.fetchRelationshipsForBand(band.get());

        model.addAttribute("Band", band.get());
        model.addAttribute("PreviousNames", previousNames);
        model.addAttribute("BandResults", bandResults.getBandWhitResults());
        model.addAttribute("ResultsCount", bandResults.getBandNonWhitResults().size());
        model.addAttribute("WhitCount", bandResults.getBandWhitResults().size());
        model.addAttribute("BandRehearsalDays", bandRehearsalDays);
        model.addAttribute("BandRelationships", bandRelationships);

        return "bands/map-nearby";
    }

    @IsBbrPro
    @GetMapping("/bands/{bandSlug:[\\-a-z\\d]{2,}}/filter/{contestSlug:[\\-a-z\\d]{2,}}")
    public String bandFilterToContest(Model model, @PathVariable("bandSlug") String bandSlug, @PathVariable("contestSlug") String contestSlug) {
        Optional<BandDao> band = this.bandService.fetchBySlug(bandSlug);
        if (band.isEmpty()) {
            throw NotFoundException.bandNotFoundBySlug(bandSlug);
        }
        Optional<ContestDao> contest = this.contestService.fetchBySlug(contestSlug);
        if (contest.isEmpty()) {
            throw NotFoundException.contestNotFoundBySlug(contestSlug);
        }

        List<BandRehearsalDayDao> bandRehearsalDays = this.bandRehearsalsService.fetchRehearsalDays(band.get());
        List<BandAliasDao> previousNames = this.bandAliasService.findVisibleAliases(band.get());
        ResultDetailsDto bandResults = this.bandResultService.findResultsForBand(band.get(), ResultSetCategory.ALL, contest.get());
        List<BandRelationshipDao> bandRelationships = this.bandRelationshipService.fetchRelationshipsForBand(band.get());

        model.addAttribute("Band", band.get());
        model.addAttribute("PreviousNames", previousNames);
        model.addAttribute("BandResults", bandResults.getBandNonWhitResults());
        model.addAttribute("ResultsCount", bandResults.getBandNonWhitResults().size());
        model.addAttribute("WhitCount", bandResults.getBandWhitResults().size());
        model.addAttribute("BandRehearsalDays", bandRehearsalDays);
        model.addAttribute("BandRelationships", bandRelationships);
        model.addAttribute("FilteredTo", contest.get().getName());
        return "bands/band";
    }

    @IsBbrPro
    @GetMapping("/bands/{bandSlug:[\\-a-z\\d]{2,}}/filter/{groupSlug:[\\-A-Z\\d]{2,}}")
    public String bandFilterToContestGroup(Model model, @PathVariable("bandSlug") String bandSlug, @PathVariable("groupSlug") String groupSlug) {
        Optional<BandDao> band = this.bandService.fetchBySlug(bandSlug);
        if (band.isEmpty()) {
            throw NotFoundException.bandNotFoundBySlug(bandSlug);
        }
        Optional<ContestGroupDao> group = this.contestGroupService.fetchBySlug(groupSlug);
        if (group.isEmpty()) {
            throw NotFoundException.groupNotFoundBySlug(groupSlug);
        }

        List<BandRehearsalDayDao> bandRehearsalDays = this.bandRehearsalsService.fetchRehearsalDays(band.get());
        List<BandAliasDao> previousNames = this.bandAliasService.findVisibleAliases(band.get());
        ResultDetailsDto bandResults = this.bandResultService.findResultsForBand(band.get(), ResultSetCategory.ALL, group.get());
        List<BandRelationshipDao> bandRelationships = this.bandRelationshipService.fetchRelationshipsForBand(band.get());

        model.addAttribute("Band", band.get());
        model.addAttribute("PreviousNames", previousNames);
        model.addAttribute("BandResults", bandResults.getBandNonWhitResults());
        model.addAttribute("ResultsCount", bandResults.getBandNonWhitResults().size());
        model.addAttribute("WhitCount", bandResults.getBandWhitResults().size());
        model.addAttribute("BandRehearsalDays", bandRehearsalDays);
        model.addAttribute("BandRelationships", bandRelationships);
        model.addAttribute("FilteredTo", group.get().getName());
        return "bands/band";
    }

    @IsBbrPro
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

        List<BandRehearsalDayDao> bandRehearsalDays = this.bandRehearsalsService.fetchRehearsalDays(band.get());
        List<BandAliasDao> previousNames = this.bandAliasService.findVisibleAliases(band.get());
        ResultDetailsDto bandResults = this.bandResultService.findResultsForBand(band.get(), ResultSetCategory.ALL, tag.get());
        List<BandRelationshipDao> bandRelationships = this.bandRelationshipService.fetchRelationshipsForBand(band.get());

        model.addAttribute("Band", band.get());
        model.addAttribute("PreviousNames", previousNames);
        model.addAttribute("BandResults", bandResults.getBandNonWhitResults());
        model.addAttribute("ResultsCount", bandResults.getBandNonWhitResults().size());
        model.addAttribute("WhitCount", bandResults.getBandWhitResults().size());
        model.addAttribute("BandRehearsalDays", bandRehearsalDays);
        model.addAttribute("BandRelationships", bandRelationships);
        model.addAttribute("FilteredTo", tag.get().getName());
        return "bands/band";
    }
}

