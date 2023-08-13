package uk.co.bbr.web.groups;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.ContestTypeService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dao.ContestTypeDao;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.groups.ContestGroupService;
import uk.co.bbr.services.groups.dao.ContestGroupDao;
import uk.co.bbr.services.groups.types.ContestGroupType;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.sections.SectionService;
import uk.co.bbr.services.sections.dao.SectionDao;
import uk.co.bbr.web.contests.forms.ContestEditForm;
import uk.co.bbr.web.groups.forms.GroupEditForm;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class EditGroupController {

    private final ContestService contestService;
    private final ContestGroupService contestGroupService;
    private final ContestTypeService contestTypeService;
    private final RegionService regionService;
    private final SectionService sectionService;

    @IsBbrMember
    @GetMapping("/contests/{groupSlug:[\\-A-Z\\d]{2,}}/edit")
    public String editContestGroupForm(Model model, @PathVariable("groupSlug") String groupSlug) {
        Optional<ContestGroupDao> group = this.contestGroupService.fetchBySlug(groupSlug);
        if (group.isEmpty()) {
            throw NotFoundException.groupNotFoundBySlug(groupSlug);
        }

        GroupEditForm editForm = new GroupEditForm(group.get());

        model.addAttribute("Group", group.get());
        model.addAttribute("Form", editForm);

        return "groups/edit";
    }

    @IsBbrMember
    @PostMapping("/contests/{groupSlug:[\\-A-Z\\d]{2,}}/edit")
    public String editContestGroupSave(Model model, @Valid @ModelAttribute("Form") GroupEditForm submittedGroup, BindingResult bindingResult, @PathVariable("groupSlug") String groupSlug) {
        Optional<ContestGroupDao> group = this.contestGroupService.fetchBySlug(groupSlug);
        if (group.isEmpty()) {
            throw NotFoundException.groupNotFoundBySlug(groupSlug);
        }

        submittedGroup.validate(bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("Group", group.get());
            return "groups/edit";
        }

        ContestGroupDao existingGroup = group.get();

        existingGroup.setName(submittedGroup.getName());
        existingGroup.setNotes(submittedGroup.getNotes());
        if (submittedGroup.getGroupType() != null) {
            existingGroup.setGroupType(ContestGroupType.fromCode(submittedGroup.getGroupType()));
        }

        this.contestGroupService.update(existingGroup);

        return "redirect:/contests/{groupSlug}";
    }
}
