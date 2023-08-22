package uk.co.bbr.web.contests;

import jakarta.validation.Valid;
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

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class CreateContestController {
    private final RegionService regionService;
    private final SectionService sectionService;
    private final ContestTypeService contestTypeService;
    private final ContestGroupService contestGroupService;
    private final ContestService contestService;


    @IsBbrMember
    @GetMapping("/create/contest")
    public String createGet(Model model) {

        ContestEditForm editForm = new ContestEditForm();

        List<RegionDao> regions = this.regionService.findAll();
        List<SectionDao> sections = this.sectionService.fetchAll();
        List<ContestTypeDao> contestTypes = this.contestTypeService.fetchAll();

        Optional<ContestTypeDao> testPieceContestType = this.contestTypeService.fetchBySlug("test-piece-contest");
        testPieceContestType.ifPresent(contestTypeDao -> editForm.setContestType(contestTypeDao.getId()));

        model.addAttribute("Form", editForm);
        model.addAttribute("Regions", regions);
        model.addAttribute("Sections", sections);
        model.addAttribute("ContestTypes", contestTypes);

        return "contests/create";
    }

    @IsBbrMember
    @PostMapping("/create/contest")
    public String createPost(@Valid @ModelAttribute("Form") ContestEditForm submittedForm, BindingResult bindingResult) {

        submittedForm.validate(bindingResult);

        if (bindingResult.hasErrors()) {
            return "contests/create";
        }

        ContestDao newContest = new ContestDao();

        newContest.setName(submittedForm.getName());
        newContest.setDescription(submittedForm.getDescription());
        newContest.setNotes(submittedForm.getNotes());
        newContest.setOrdering(submittedForm.getOrdering() != null ? submittedForm.getOrdering() : 0);
        newContest.setExtinct(submittedForm.isExtinct());
        newContest.setExcludeFromGroupResults(submittedForm.isExcludeFromGroupResults());
        newContest.setAllEventsAdded(submittedForm.isAllEventsAdded());
        newContest.setPreventFutureBands(submittedForm.isPreventFutureBands());
        newContest.setRepeatPeriod(submittedForm.getRepeatPeriod());

        if (submittedForm.getRegion() != null) {
            Optional<RegionDao> region = this.regionService.fetchById(submittedForm.getRegion());
            region.ifPresent(newContest::setRegion);
        }

        if (submittedForm.getSection() != null) {
            Optional<SectionDao> section = this.sectionService.fetchById(submittedForm.getSection());
            section.ifPresent(newContest::setSection);
        }

        if (submittedForm.getContestType() != null) {
            Optional<ContestTypeDao> contestType = this.contestTypeService.fetchById(submittedForm.getContestType());
            contestType.ifPresent(newContest::setDefaultContestType);
        }

        Optional<ContestGroupDao> contestGroup = this.contestGroupService.fetchBySlug(submittedForm.getContestGroupSlug());
        contestGroup.ifPresent(newContest::setContestGroup);

        Optional<ContestDao> qualifiesFor = this.contestService.fetchBySlug(submittedForm.getQualifiesForSlug());
        qualifiesFor.ifPresent(newContest::setQualifiesFor);

        this.contestService.create(newContest);

        return "redirect:/contests";
    }
}
