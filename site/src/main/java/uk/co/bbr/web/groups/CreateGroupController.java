package uk.co.bbr.web.groups;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import uk.co.bbr.services.groups.ContestGroupService;
import uk.co.bbr.services.groups.dao.ContestGroupDao;
import uk.co.bbr.services.groups.types.ContestGroupType;
import uk.co.bbr.web.groups.forms.GroupEditForm;
import uk.co.bbr.web.security.annotations.IsBbrMember;

@Controller
@RequiredArgsConstructor
public class CreateGroupController {
    private final ContestGroupService contestGroupService;

    @IsBbrMember
    @GetMapping("/create/group")
    public String createGet(Model model) {

        GroupEditForm editForm = new GroupEditForm();

        model.addAttribute("Form", editForm);

        return "groups/create";
    }

    @IsBbrMember
    @PostMapping("/create/group")
    public String createPost(@Valid @ModelAttribute("Form") GroupEditForm submittedForm, BindingResult bindingResult) {

        submittedForm.validate(bindingResult);

        if (bindingResult.hasErrors()) {
            return "groups/create";
        }

        ContestGroupDao newGroup = new ContestGroupDao();

        newGroup.setName(submittedForm.getName());
        newGroup.setNotes(submittedForm.getNotes());
        newGroup.setGroupType(ContestGroupType.fromCode(submittedForm.getGroupType()));

        this.contestGroupService.create(newGroup);

        return "redirect:/contest-groups";
    }
}
