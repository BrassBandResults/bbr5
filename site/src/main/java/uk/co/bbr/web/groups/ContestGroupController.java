package uk.co.bbr.web.groups;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.groups.ContestGroupService;
import uk.co.bbr.services.groups.dao.ContestGroupAliasDao;
import uk.co.bbr.services.groups.dao.ContestGroupDao;
import uk.co.bbr.services.groups.dto.ContestGroupDetailsDto;
import uk.co.bbr.services.groups.dto.ContestGroupYearDto;
import uk.co.bbr.services.groups.dto.ContestGroupYearsDetailsDto;
import uk.co.bbr.services.groups.dto.WhitFridayOverallResultsDto;
import uk.co.bbr.services.groups.types.ContestGroupType;
import uk.co.bbr.web.Tools;
import uk.co.bbr.web.security.annotations.IsBbrMember;
import uk.co.bbr.web.security.annotations.IsBbrPro;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ContestGroupController {

    private final ContestGroupService contestGroupService;

    @GetMapping("/contest-groups/{groupSlug:[\\-A-Z\\d]{2,}}")
    public String contestGroupRedirectUpperCase(@PathVariable("groupSlug") String groupSlug) {
        return "redirect:/contests/" + groupSlug;
    }

    @GetMapping("/contest-groups/{groupSlug:[\\-_a-z\\d]{2,}}")
    public String contestGroupRedirectLowerCase(@PathVariable("groupSlug") String groupSlug) {
        return "redirect:/contests/" + groupSlug.toUpperCase();
    }

    @GetMapping("/contests/{slug:[\\-A-Z\\d]{2,}}")
    public String contestGroupDetails(Model model, @PathVariable("slug") String groupSlug) {
        Optional<ContestGroupDao> contestGroup = this.contestGroupService.fetchBySlug(groupSlug);
        if (contestGroup.isEmpty()) {
            throw NotFoundException.groupNotFoundBySlug(groupSlug);
        }

        ContestGroupDetailsDto contestGroupDetails = this.contestGroupService.fetchDetail(contestGroup.get());
        List<ContestGroupAliasDao> contestGroupAliases = this.contestGroupService.fetchAliases(contestGroup.get());

        model.addAttribute("Group", contestGroupDetails);
        model.addAttribute("PreviousNames", contestGroupAliases);
        model.addAttribute("Notes", Tools.markdownToHTML(contestGroupDetails.getContestGroup().getNotes()));
        return "groups/group";
    }

    @IsBbrMember
    @GetMapping("/contests/{slug:[\\-A-Z\\d]{2,}}/years")
    public String contestGroupYearDetails(Model model, @PathVariable("slug") String groupSlug) {
        Optional<ContestGroupDao> contestGroup = this.contestGroupService.fetchBySlug(groupSlug);
        if (contestGroup.isEmpty()) {
            throw NotFoundException.groupNotFoundBySlug(groupSlug);
        }

        ContestGroupYearsDetailsDto contestGroupDetails = this.contestGroupService.fetchYearsBySlug(groupSlug);
        List<ContestGroupAliasDao> contestGroupAliases = this.contestGroupService.fetchAliases(contestGroup.get());

        model.addAttribute("Group", contestGroupDetails);
        model.addAttribute("PreviousNames", contestGroupAliases);
        model.addAttribute("Notes", Tools.markdownToHTML(contestGroupDetails.getContestGroup().getNotes()));
        return "groups/years";
    }

    @GetMapping("/contests/{slug:[\\-A-Z\\d]{2,}}/{year:[0-9]{4}}")
    public String contestGroupYearDetails(Model model, @PathVariable("slug") String groupSlug, @PathVariable("year") Integer year) {
        ContestGroupYearDto eventsForGroupAndYear = this.contestGroupService.fetchEventsByGroupSlugAndYear(groupSlug, year);

        model.addAttribute("GroupYearEvents", eventsForGroupAndYear);
        return "groups/year";
    }

    @IsBbrPro
    @GetMapping("/contests/{slug:[\\-A-Z\\d]{2,}}/{year:[0-9]{4}}/overall-results")
    public String contestGroupOverallResultsDetails(Model model, @PathVariable("slug") String groupSlug, @PathVariable("year") Integer year) {
        Optional<ContestGroupDao> contestGroup = this.contestGroupService.fetchBySlug(groupSlug);
        if (contestGroup.isEmpty()) {
            throw NotFoundException.groupNotFoundBySlug(groupSlug);
        }

        if (contestGroup.get().getGroupType() == ContestGroupType.NORMAL) {
            throw NotFoundException.groupNotFoundBySlug(groupSlug);
        }

        WhitFridayOverallResultsDto whitFridayOverallResults = this.contestGroupService.fetchWhitFridayOverallResults(contestGroup.get(), year);

        model.addAttribute("OverallResults", whitFridayOverallResults);
        return "groups/year-overall-results";
    }

    @IsBbrPro
    @GetMapping("/contests/{slug:[\\-A-Z\\d]{2,}}/{year:[0-9]{4}}/overall-results-median")
    public String contestGroupOverallResultsMEdianDetails(Model model, @PathVariable("slug") String groupSlug, @PathVariable("year") Integer year) {
        Optional<ContestGroupDao> contestGroup = this.contestGroupService.fetchBySlug(groupSlug);
        if (contestGroup.isEmpty()) {
            throw NotFoundException.groupNotFoundBySlug(groupSlug);
        }

        if (contestGroup.get().getGroupType() == ContestGroupType.NORMAL) {
            throw NotFoundException.groupNotFoundBySlug(groupSlug);
        }

        WhitFridayOverallResultsDto whitFridayOverallResults = this.contestGroupService.fetchWhitFridayOverallResultsMedian(contestGroup.get(), year);

        model.addAttribute("OverallResults", whitFridayOverallResults);
        return "groups/year-overall-results-median";
    }

}
