package uk.co.bbr.web.groups;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.groups.ContestGroupService;
import uk.co.bbr.services.groups.dao.ContestGroupDao;
import uk.co.bbr.services.groups.dto.ContestGroupDetailsDto;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class DeleteGroupController {

    private final ContestGroupService contestGroupService;

    @IsBbrMember
    @GetMapping("/contest-groups/{groupSlug:[\\-A-Z\\d]{2,}}/delete")
    public String deleteGroup(Model model, @PathVariable("groupSlug") String groupSlug) {
        Optional<ContestGroupDao> contestGroup = this.contestGroupService.fetchBySlug(groupSlug);
        if (contestGroup.isEmpty()) {
            throw NotFoundException.groupNotFoundBySlug(groupSlug);
        }

        ContestGroupDetailsDto contestGroupDetails = this.contestGroupService.fetchDetail(contestGroup.get());

        boolean blocked = !contestGroupDetails.getActiveContests().isEmpty() || !contestGroupDetails.getOldContests().isEmpty();

        if (blocked) {
            model.addAttribute("Group", contestGroup.get());
            model.addAttribute("ActiveContests", contestGroupDetails.getActiveContests());
            model.addAttribute("OldContests", contestGroupDetails.getOldContests());

            return "groups/delete-group-blocked";
        }

        this.contestGroupService.delete(contestGroup.get());

        return "redirect:/contest-groups";
    }
}
