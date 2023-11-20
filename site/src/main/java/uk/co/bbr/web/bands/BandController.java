package uk.co.bbr.web.bands;

import com.azure.cosmos.implementation.guava25.collect.Lists;
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
import uk.co.bbr.services.bands.types.BandStatus;
import uk.co.bbr.services.bands.types.ResultSetCategory;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.BandResultService;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.events.dto.ResultDetailsDto;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.groups.ContestGroupService;
import uk.co.bbr.services.groups.dao.ContestGroupDao;
import uk.co.bbr.services.sections.SectionService;
import uk.co.bbr.services.sections.dao.SectionDao;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.dao.SiteUserDao;
import uk.co.bbr.services.tags.ContestTagService;
import uk.co.bbr.services.tags.dao.ContestTagDao;
import uk.co.bbr.web.Tools;
import uk.co.bbr.web.security.annotations.IsBbrMember;
import uk.co.bbr.web.security.annotations.IsBbrPro;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
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
    private final SectionService sectionService;
    private final SecurityService securityService;

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

        SiteUserDao currentUser = this.securityService.getCurrentUser();
        if (currentUser != null) {
            this.updateBandSection(band.get(), bandResults.getBandNonWhitResults());
        }

        model.addAttribute("Band", band.get());
        model.addAttribute("PreviousNames", previousNames);
        model.addAttribute("BandResults", bandResults.getBandNonWhitResults());
        model.addAttribute("ResultsCount", bandResults.getBandNonWhitResults().size());
        model.addAttribute("WhitCount", bandResults.getBandWhitResults().size());
        model.addAttribute("BandChampions", bandResults.getCurrentChampions());
        model.addAttribute("SpecialAwards", bandResults.getSpecialAwards());
        model.addAttribute("BandRehearsalDays", bandRehearsalDays);
        model.addAttribute("BandRelationships", bandRelationships);
        model.addAttribute("Notes", Tools.markdownToHTML(band.get().getNotes()));
        return "bands/band";
    }

    private void updateBandSection(BandDao band, List<ContestResultDao> results) {
        if (band.getStatus().equals(BandStatus.COMPETING)) {
            LocalDate thirteenMonthsAgo = LocalDate.now().minus(13, ChronoUnit.MONTHS);
            LocalDate fourMonthsInFuture = LocalDate.now().plus(4, ChronoUnit.MONTHS);
            List<ContestResultDao> lastYearResults = results.stream()
                .filter(p -> p.getContestEvent().getEventDate().isAfter(thirteenMonthsAgo))
                .filter(p -> p.getContestEvent().getEventDate().isBefore(fourMonthsInFuture))
                .filter(p -> p.getContestEvent().getContest().getSection() != null)
                .sorted(Comparator.comparing(o -> o.getContestEvent().getEventDate()))
                .toList();

            // sort list latest first
            List<ContestResultDao> reverseList = Lists.reverse(lastYearResults);

            for (ContestResultDao result : reverseList) {
                if (result.getContestEvent().getContest().getSection() != null) {
                    Optional<SectionDao> bandSection = this.sectionService.fetchById(result.getContestEvent().getContest().getSection().getId());
                    if (bandSection.isPresent()) {
                        band.setSection(bandSection.get());
                        this.bandService.update(band);
                        break;
                    }
                }
            }
        }
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
        model.addAttribute("BandChampions", bandResults.getCurrentChampions());
        model.addAttribute("SpecialAwards", bandResults.getSpecialAwards());
        model.addAttribute("BandRehearsalDays", bandRehearsalDays);
        model.addAttribute("BandRelationships", bandRelationships);
        model.addAttribute("Notes", Tools.markdownToHTML(band.get().getNotes()));
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
        model.addAttribute("BandChampions", bandResults.getCurrentChampions());
        model.addAttribute("SpecialAwards", bandResults.getSpecialAwards());
        model.addAttribute("BandRehearsalDays", bandRehearsalDays);
        model.addAttribute("BandRelationships", bandRelationships);
        model.addAttribute("Notes", Tools.markdownToHTML(band.get().getNotes()));

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
        model.addAttribute("BandChampions", bandResults.getCurrentChampions());
        model.addAttribute("SpecialAwards", bandResults.getSpecialAwards());
        model.addAttribute("BandRehearsalDays", bandRehearsalDays);
        model.addAttribute("BandRelationships", bandRelationships);
        model.addAttribute("FilteredTo", contest.get().getName());
        model.addAttribute("Notes", Tools.markdownToHTML(band.get().getNotes()));
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
        model.addAttribute("BandChampions", bandResults.getCurrentChampions());
        model.addAttribute("SpecialAwards", bandResults.getSpecialAwards());
        model.addAttribute("BandRehearsalDays", bandRehearsalDays);
        model.addAttribute("BandRelationships", bandRelationships);
        model.addAttribute("FilteredTo", group.get().getName());
        model.addAttribute("Notes", Tools.markdownToHTML(band.get().getNotes()));
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
        model.addAttribute("BandChampions", bandResults.getCurrentChampions());
        model.addAttribute("SpecialAwards", bandResults.getSpecialAwards());
        model.addAttribute("BandRehearsalDays", bandRehearsalDays);
        model.addAttribute("BandRelationships", bandRelationships);
        model.addAttribute("FilteredTo", tag.get().getName());
        model.addAttribute("Notes", Tools.markdownToHTML(band.get().getNotes()));
        return "bands/band";
    }
}

