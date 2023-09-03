package uk.co.bbr.web.contests;

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
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.sections.SectionService;
import uk.co.bbr.services.sections.dao.SectionDao;
import uk.co.bbr.web.contests.forms.ContestEditForm;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class EditContestController {

    private final ContestService contestService;
    private final ContestGroupService contestGroupService;
    private final ContestTypeService contestTypeService;
    private final RegionService regionService;
    private final SectionService sectionService;

    @IsBbrMember
    @GetMapping("/contests/{contestSlug:[\\-a-z\\d]{2,}}/edit")
    public String editContestForm(Model model, @PathVariable("contestSlug") String contestSlug) {
        Optional<ContestDao> contest = this.contestService.fetchBySlug(contestSlug);
        if (contest.isEmpty()) {
            throw NotFoundException.contestNotFoundBySlug(contestSlug);
        }

        ContestEditForm editForm = new ContestEditForm(contest.get());

        List<RegionDao> regions = this.regionService.findAll();
        List<SectionDao> sections = this.sectionService.fetchAll();
        List<ContestTypeDao> contestTypes = this.contestTypeService.fetchAll();

        model.addAttribute("Contest", contest.get());
        model.addAttribute("Form", editForm);
        model.addAttribute("Regions", regions);
        model.addAttribute("Sections", sections);
        model.addAttribute("ContestTypes", contestTypes);

        return "contests/edit";
    }

    @IsBbrMember
    @PostMapping("/contests/{contestSlug:[\\-a-z\\d]{2,}}/edit")
    public String editContestSave(Model model, @Valid @ModelAttribute("Form") ContestEditForm submittedContest, BindingResult bindingResult, @PathVariable("contestSlug") String contestSlug) {
        Optional<ContestDao> existingContestOptional = this.contestService.fetchBySlug(contestSlug);
        if (existingContestOptional.isEmpty()) {
            throw NotFoundException.contestNotFoundBySlug(contestSlug);
        }

        submittedContest.validate(bindingResult);

        if (bindingResult.hasErrors()) {
            List<RegionDao> regions = this.regionService.findAll();

            model.addAttribute("Contest", existingContestOptional.get());
            model.addAttribute("Regions", regions);

            return "contests/edit";
        }

        ContestDao existingContest = existingContestOptional.get();

        existingContest.setName(submittedContest.getName());
        existingContest.setNotes(submittedContest.getNotes());
        existingContest.setExtinct(submittedContest.isExtinct());
        existingContest.setExcludeFromGroupResults(submittedContest.isExcludeFromGroupResults());
        existingContest.setOrdering(submittedContest.getOrdering());
        existingContest.setDescription(submittedContest.getDescription());
        if (submittedContest.getQualifiesForSlug() != null) {
            Optional<ContestDao> qualifiesFor = this.contestService.fetchBySlug(submittedContest.getQualifiesForSlug());
            if (qualifiesFor.isPresent()) {
                existingContest.setQualifiesFor(qualifiesFor.get());
            } else {
                existingContest.setQualifiesFor(null);
            }
        }
        if (submittedContest.getContestGroupSlug() != null) {
            Optional<ContestGroupDao> contestGroup = this.contestGroupService.fetchBySlug(submittedContest.getContestGroupSlug());
            if (contestGroup.isPresent()) {
                existingContest.setContestGroup(contestGroup.get());
            } else {
                existingContest.setContestGroup(null);
            }
        }

        Optional<RegionDao> newRegion = this.regionService.fetchById(submittedContest.getRegion());
        if (newRegion.isPresent()) {
            existingContest.setRegion(newRegion.get());
        } else {
            existingContest.setRegion(null);
        }

        Optional<SectionDao> newSection = this.sectionService.fetchById(submittedContest.getSection());
        if (newSection.isPresent()) {
            existingContest.setSection(newSection.get());
        } else {
            existingContest.setSection(null);
        }

        Optional<ContestTypeDao> newContestType = this.contestTypeService.fetchById(submittedContest.getContestType());
        if (newContestType.isPresent()) {
            existingContest.setDefaultContestType(newContestType.get());
        } else {
            existingContest.setDefaultContestType(null);
        }

        this.contestService.update(existingContest);

        return "redirect:/contests/{contestSlug}";
    }
}
