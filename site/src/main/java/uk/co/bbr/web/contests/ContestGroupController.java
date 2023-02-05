package uk.co.bbr.web.contests;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.contests.ContestGroupService;
import uk.co.bbr.services.contests.dto.ContestGroupDetailsDto;
import uk.co.bbr.services.contests.dto.GroupListDto;

@Controller
@RequiredArgsConstructor
public class ContestGroupController {

    private final ContestGroupService contestGroupService;

    @GetMapping("/contest-groups")
    public String contestGroupsListHome(Model model) {
        return contestGroupsListLetter(model, "A");
    }

    @GetMapping("/contest-groups/{letter:[A-Z0-9]{1}}")
    public String contestGroupsListLetter(Model model, @PathVariable("letter") String letter) {
        GroupListDto groups = this.contestGroupService.listGroupsStartingWith(letter);

        model.addAttribute("GroupPrefixLetter", letter);
        model.addAttribute("Groups", groups);
        return "contests/groups/groups";
    }

    @GetMapping("/contest-groups/ALL")
    public String contestGroupsListAll(Model model) {
        GroupListDto groups = this.contestGroupService.listGroupsStartingWith("ALL");

        model.addAttribute("GroupPrefixLetter", "ALL");
        model.addAttribute("Groups", groups);
        return "contests/groups/groups";
    }

    @GetMapping("/contests/{slug:[\\-A-Z\\d]{2,}}")
    public String contestGroupDetails(Model model, @PathVariable("slug") String groupSlug) {
        ContestGroupDetailsDto contestGroupDetails = this.contestGroupService.fetchDetailBySlug(groupSlug);

        model.addAttribute("Group", contestGroupDetails);
        return "contests/groups/group";
    }

}
