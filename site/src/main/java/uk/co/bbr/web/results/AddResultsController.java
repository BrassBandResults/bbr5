package uk.co.bbr.web.results;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.ContestEventService;
import uk.co.bbr.services.events.ResultService;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.security.UserService;
import uk.co.bbr.services.security.dao.SiteUserDao;
import uk.co.bbr.web.events.forms.EventEditForm;
import uk.co.bbr.web.results.forms.AddResultsContestForm;
import uk.co.bbr.web.results.forms.AddResultsDateForm;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class AddResultsController {

    private final ContestService contestService;
    private final ContestEventService contestEventService;
    private final ResultService contestResultService;
    private final UserService userService;

    @IsBbrMember
    @GetMapping("/add-results")
    public String addResultsContestStageGet(Model model) {
        AddResultsContestForm form = new AddResultsContestForm();

        model.addAttribute("Form", form);

        return "results/add-results-1-contest";
    }

    @IsBbrMember
    @PostMapping("/add-results")
    public String addResultsContestStagePost(@Valid @ModelAttribute("Form") AddResultsContestForm submittedForm, BindingResult bindingResult) {
        submittedForm.validate(bindingResult);

        if (bindingResult.hasErrors()) {
            return "results/add-results-1-contest";
        }

        if (submittedForm.getContestSlug() != null && submittedForm.getContestSlug().trim().length() > 0) {
            Optional<ContestDao> matchingContest = this.contestService.fetchBySlug(submittedForm.getContestSlug());
            if (matchingContest.isPresent()){
                return "redirect:/add-results/1/" + matchingContest.get().getSlug();
            }
        }

        Optional<ContestDao> matchingContest = this.contestService.fetchByNameUpper(submittedForm.getContestName());
        if (matchingContest.isPresent()) {
            return "redirect:/add-results/1/" + matchingContest.get().getSlug();
        }

        ContestDao savedContest = this.contestService.create(submittedForm.getContestName());
        return "redirect:/add-results/1/" + savedContest.getSlug();
    }

    @IsBbrMember
    @GetMapping("/add-results/1/{contestSlug:[\\-a-z\\d]{2,}}")
    public String addResultsDateStageGet(Model model, @PathVariable("contestSlug") String contestSlug) {
        Optional<ContestDao> matchingContest = this.contestService.fetchBySlug(contestSlug);
        if (matchingContest.isEmpty()) {
            throw NotFoundException.contestNotFoundBySlug(contestSlug);
        }

        AddResultsDateForm form = new AddResultsDateForm();

        model.addAttribute("Contest", matchingContest.get());
        model.addAttribute("Form", form);

        return "results/add-results-2-event-date";
    }
}
