package uk.co.bbr.web.groups;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.events.dto.GroupListDto;
import uk.co.bbr.services.groups.ContestGroupService;
import uk.co.bbr.web.security.annotations.IsBbrMember;

@Controller
@RequiredArgsConstructor
public class GroupListController {

    private final ContestGroupService contestGroupService;

    @GetMapping("/contest-groups")
    public String contestGroupsListHome(Model model) {
        return contestGroupsListLetter(model, "A");
    }

    @GetMapping("/contest-groups/{letter:[A-Z0-9]}")
    public String contestGroupsListLetter(Model model, @PathVariable("letter") String letter) {
        GroupListDto groups = this.contestGroupService.listGroupsStartingWith(letter);

        model.addAttribute("GroupPrefixLetter", letter);
        model.addAttribute("Groups", groups);
        return "groups/groups";
    }

    @IsBbrMember
    @GetMapping("/contest-groups/ALL")
    public String contestGroupsListAll(Model model) {
        GroupListDto groups = this.contestGroupService.listGroupsStartingWith("ALL");

        model.addAttribute("GroupPrefixLetter", "ALL");
        model.addAttribute("Groups", groups);
        return "groups/groups";
    }
}
