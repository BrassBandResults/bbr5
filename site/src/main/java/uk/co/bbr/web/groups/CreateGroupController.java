package uk.co.bbr.web.groups;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.ContestTypeService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dao.ContestTypeDao;
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

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

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
    public String createPost(Model model, @Valid @ModelAttribute("Form") GroupEditForm submittedForm, BindingResult bindingResult) {

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
