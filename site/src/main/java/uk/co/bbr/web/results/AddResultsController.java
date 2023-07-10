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
import uk.co.bbr.services.contests.ContestTypeService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dao.ContestTypeDao;
import uk.co.bbr.services.events.ContestEventService;
import uk.co.bbr.services.events.ResultService;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.events.types.ContestEventDateResolution;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.UserService;
import uk.co.bbr.services.security.dao.SiteUserDao;
import uk.co.bbr.web.Tools;
import uk.co.bbr.web.events.forms.EventEditForm;
import uk.co.bbr.web.results.forms.AddResultsContestForm;
import uk.co.bbr.web.results.forms.AddResultsContestTypeForm;
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
    private final SecurityService securityService;
    private final ContestTypeService contestTypeService;
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
                return "redirect:/add-results/2/" + matchingContest.get().getSlug();
            }
        }

        Optional<ContestDao> matchingContest = this.contestService.fetchByNameUpper(submittedForm.getContestName());
        if (matchingContest.isPresent()) {
            return "redirect:/add-results/2/" + matchingContest.get().getSlug();
        }

        ContestDao savedContest = this.contestService.create(submittedForm.getContestName());
        return "redirect:/add-results/2/" + savedContest.getSlug();
    }

    @IsBbrMember
    @GetMapping("/add-results/2/{contestSlug:[\\-a-z\\d]{2,}}")
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

    @IsBbrMember
    @PostMapping("/add-results/2/{contestSlug:[\\-a-z\\d]{2,}}")
    public String addResultsDateStagePost(@Valid @ModelAttribute("Form") AddResultsDateForm submittedForm, BindingResult bindingResult, @PathVariable("contestSlug") String contestSlug) {
        Optional<ContestDao> matchingContest = this.contestService.fetchBySlug(contestSlug);
        if (matchingContest.isEmpty()) {
            throw NotFoundException.contestNotFoundBySlug(contestSlug);
        }

        submittedForm.validate(bindingResult);

        if (bindingResult.hasErrors()) {
            return "results/add-results-2-event-date";
        }

        ContestEventDateResolution eventDateResolution = null;
        LocalDate eventDate = null;
        int slashCount = (int) submittedForm.getEventDate().chars().filter(ch -> ch == '/').count();
        switch (slashCount) {
            case 0 -> {
                eventDateResolution = ContestEventDateResolution.YEAR;
                int year1 = Integer.parseInt(submittedForm.getEventDate());
                eventDate = LocalDate.of(year1, 1, 1);
            }
            case 1 -> {
                eventDateResolution = ContestEventDateResolution.MONTH_AND_YEAR;
                String[] dateSections2 = submittedForm.getEventDate().split("/");
                int month2 = Integer.parseInt(dateSections2[0]);
                int year2 = Integer.parseInt(dateSections2[1]);
                eventDate = LocalDate.of(year2, month2, 1);
            }
            case 2 -> {
                eventDateResolution = ContestEventDateResolution.EXACT_DATE;
                String[] dateSections3 = submittedForm.getEventDate().split("/");
                int day3 = Integer.parseInt(dateSections3[0]);
                int month3 = Integer.parseInt(dateSections3[1]);
                int year3 = Integer.parseInt(dateSections3[2]);
                eventDate = LocalDate.of(year3, month3, day3);
            }
        }

        // does event already exist?
        ContestEventDao contestEvent;
        Optional<ContestEventDao> existingEvent = this.contestEventService.fetchEvent(matchingContest.get(), eventDate);
        if (existingEvent.isPresent()) {
            // does it have results?
            List<ContestResultDao> existingResults = this.contestResultService.fetchForEvent(existingEvent.get());
            if (!existingResults.isEmpty()) {
                return "redirect:/contests/" + existingEvent.get().getContest().getSlug() + "/" +  existingEvent.get().getEventDateForUrl();
            }
            contestEvent = existingEvent.get();
        } else {
            contestEvent = new ContestEventDao();
            contestEvent.setContest(matchingContest.get());
            contestEvent.setName(matchingContest.get().getName());
            contestEvent.setEventDate(eventDate);
            contestEvent.setEventDateResolution(eventDateResolution);
            contestEvent.setContestType(matchingContest.get().getDefaultContestType());
            contestEvent.setOwner(this.securityService.getCurrentUsername());

            this.contestEventService.create(matchingContest.get(), contestEvent);
        }

        return "redirect:/add-results/3/{contestSlug}/" + contestEvent.getEventDateForUrl();
    }

    @IsBbrMember
    @GetMapping("/add-results/3/{contestSlug:[\\-a-z\\d]{2,}}/{contestEventDate:\\d{4}-\\d{2}-\\d{2}}")
    public String addResultsContestTypeStageGet(Model model, @PathVariable("contestSlug") String contestSlug, @PathVariable String contestEventDate) {
        LocalDate eventDate = Tools.parseEventDate(contestEventDate);
        Optional<ContestEventDao> event = this.contestEventService.fetchEvent(contestSlug, eventDate);
        if (event.isEmpty()) {
            throw NotFoundException.eventNotFound(contestSlug, contestEventDate);
        }

        AddResultsContestTypeForm form = new AddResultsContestTypeForm();
        form.setContestType(event.get().getContestType().getId());

        List<ContestTypeDao> contestTypes = this.contestTypeService.fetchAll();

        model.addAttribute("ContestEvent", event.get());
        model.addAttribute("ContestTypes", contestTypes);
        model.addAttribute("Form", form);

        return "results/add-results-3-event-type";
    }

    @IsBbrMember
    @PostMapping("/add-results/3/{contestSlug:[\\-a-z\\d]{2,}}/{contestEventDate:\\d{4}-\\d{2}-\\d{2}}")
    public String addResultsContestTypeStagePost(Model model, @Valid @ModelAttribute("Form") AddResultsContestTypeForm submittedForm, BindingResult bindingResult, @PathVariable("contestSlug") String contestSlug, @PathVariable String contestEventDate) {
        LocalDate eventDate = Tools.parseEventDate(contestEventDate);
        Optional<ContestEventDao> event = this.contestEventService.fetchEvent(contestSlug, eventDate);
        if (event.isEmpty()) {
            throw NotFoundException.eventNotFound(contestSlug, contestEventDate);
        }

        submittedForm.validate(bindingResult);

        if (bindingResult.hasErrors()) {
            List<ContestTypeDao> contestTypes = this.contestTypeService.fetchAll();
            model.addAttribute("ContestEvent", event.get());
            model.addAttribute("ContestTypes", contestTypes);
            return "results/add-results-3-event-type";
        }

        Optional<ContestTypeDao> contestType = this.contestTypeService.fetchById(submittedForm.getContestType());
        if (contestType.isEmpty()) {
            throw NotFoundException.contestTypeNotFoundForId(submittedForm.getContestType());
        }

        event.get().setContestType(contestType.get());
        this.contestEventService.update(event.get());

        return "redirect:/add-results/4/{contestSlug}/{contestEventDate}";
    }

}
