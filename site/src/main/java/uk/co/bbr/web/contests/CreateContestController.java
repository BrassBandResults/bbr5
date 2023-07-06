package uk.co.bbr.web.contests;

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
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.sections.SectionService;
import uk.co.bbr.services.sections.dao.SectionDao;
import uk.co.bbr.web.contests.forms.ContestEditForm;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class CreateContestController {
    private final ContestService contestService;
    private final ContestTypeService contestTypeService;
    private final ContestGroupService contestGroupService;
    private final SectionService sectionService;
    private final RegionService regionService;


    @IsBbrMember
    @GetMapping("/create/contest")
    public String createGet(Model model) {

        List<RegionDao> regions = this.regionService.findAll();
        List<ContestTypeDao> contestTypes = this.contestTypeService.fetchAll();
        List<SectionDao> sections = this.sectionService.fetchAll();

        ContestEditForm editForm = new ContestEditForm();
        editForm.setRegion(this.regionService.fetchUnknownRegion().getId());


        model.addAttribute("Form", editForm);
        model.addAttribute("Regions", regions);
        model.addAttribute("ContestTypes", contestTypes);
        model.addAttribute("Sections", sections);

        return "contests/create";
    }

    @IsBbrMember
    @PostMapping("/create/contest")
    public String createPost(Model model, @Valid @ModelAttribute("Form") ContestEditForm submittedForm, BindingResult bindingResult) {

        List<RegionDao> regions = this.regionService.findAll();
        model.addAttribute("Regions", regions);

        submittedForm.validate(bindingResult);

        if (bindingResult.hasErrors()) {
            return "contests/create";
        }

        ContestDao newContest = new ContestDao();

        if (newContest.getRegion() != null) {
            Optional<RegionDao> region = this.regionService.fetchById(submittedForm.getRegion());
            region.ifPresent(newContest::setRegion);
        }

        newContest.setName(submittedForm.getName());
        if (submittedForm.getContestGroup() != null) {
            Optional<ContestGroupDao> contestGroup = this.contestGroupService.fetchBySlug(submittedForm.getContestGroup());
            if (contestGroup.isPresent()) {
                newContest.setContestGroup(contestGroup.get());
            }
        }

        if (submittedForm.getContestType() != null) {
            Optional<ContestTypeDao> contestType = this.contestTypeService.fetchById(submittedForm.getContestType());
            if (contestType.isPresent()) {
                newContest.setDefaultContestType(contestType.get());
            }
        }
        if (submittedForm.getRegion() != null) {
            Optional<RegionDao> region = this.regionService.fetchById(submittedForm.getRegion());
            if (region.isPresent()) {
                newContest.setRegion(region.get());
            }
        }
        if (submittedForm.getSection() != null) {
            Optional<SectionDao> section = this.sectionService.fetchById(submittedForm.getSection());
            if (section.isPresent()) {
                newContest.setSection(section.get());
            }
        }
        if (submittedForm.getOrdering() != null) {
            newContest.setOrdering(submittedForm.getOrdering());
        }
        newContest.setDescription(submittedForm.getDescription());
        newContest.setNotes(submittedForm.getNotes());

        newContest.setExtinct(submittedForm.isExtinct());
        newContest.setExcludeFromGroupResults(submittedForm.isExcludeFromGroupResults());
        newContest.setAllEventsAdded(submittedForm.isAllEventsAdded());
        newContest.setPreventFutureBands(submittedForm.isPreventFutureBands());
        newContest.setRepeatPeriod(submittedForm.getRepeatPeriod());

        this.contestService.create(newContest);

        return "redirect:/contests";
    }
}
