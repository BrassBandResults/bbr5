package uk.co.bbr.web.events;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.events.ContestEventService;
import uk.co.bbr.services.events.ResultService;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.events.types.ResultAwardType;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.web.events.forms.ResultEditForm;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class EditResultController {

    private final ResultService resultService;
    private final ContestEventService contestEventService;
    private final PersonService personService;
    private final BandService bandService;

    @IsBbrMember
    @GetMapping("/contests/{contestSlug:[\\-a-z\\d]{2,}}/{contestEventDate:\\d{4}-\\d{2}-\\d{2}}/result/{resultId:\\d+}/edit")
    public String editContestResultForm(Model model, @PathVariable("contestSlug") String contestSlug, @PathVariable("contestEventDate") String contestEventDate, @PathVariable("resultId") Long resultId) {
        String[] dateSplit = contestEventDate.split("-");
        LocalDate eventDate = LocalDate.of(Integer.parseInt(dateSplit[0]), Integer.parseInt(dateSplit[1]), Integer.parseInt(dateSplit[2]));
        Optional<ContestEventDao> contestEvent = this.contestEventService.fetchEvent(contestSlug, eventDate);

        if (contestEvent.isEmpty()) {
            throw NotFoundException.eventNotFound(contestSlug, contestEventDate);
        }

        Optional <ContestResultDao> result = this.resultService.fetchById(resultId);
        if (result.isEmpty()) {
            throw NotFoundException.resultNotFoundById(resultId);
        }

        if (!result.get().getContestEvent().getId().equals(contestEvent.get().getId())) {
            throw NotFoundException.resultNotOnCorrectContest(contestSlug, contestEventDate);
        }

        ResultEditForm editForm = new ResultEditForm(result.get());

        model.addAttribute("ContestEvent", contestEvent.get());
        model.addAttribute("Result", result.get());
        model.addAttribute("Form", editForm);

        return "events/edit-result";
    }

    @IsBbrMember
    @PostMapping("/contests/{contestSlug:[\\-a-z\\d]{2,}}/{contestEventDate:\\d{4}-\\d{2}-\\d{2}}/result/{resultId:\\d+}/edit")
    public String editContestResultSave(Model model, @Valid @ModelAttribute("Form") ResultEditForm submittedResult, BindingResult bindingResult, @PathVariable("contestSlug") String contestSlug, @PathVariable("contestEventDate") String contestEventDate, @PathVariable("resultId") Long resultId) {
        String[] dateSplit = contestEventDate.split("-");
        LocalDate eventDate = LocalDate.of(Integer.parseInt(dateSplit[0]), Integer.parseInt(dateSplit[1]), Integer.parseInt(dateSplit[2]));
        Optional<ContestEventDao> contestEvent = this.contestEventService.fetchEvent(contestSlug, eventDate);

        if (contestEvent.isEmpty()) {
            throw NotFoundException.eventNotFound(contestSlug, contestEventDate);
        }

        Optional< ContestResultDao> result = this.resultService.fetchById(resultId);
        if (result.isEmpty()) {
            throw NotFoundException.resultNotFoundById(resultId);
        }

        if (!result.get().getContestEvent().getId().equals(contestEvent.get().getId())) {
            throw NotFoundException.resultNotOnCorrectContest(contestSlug, contestEventDate);
        }

        submittedResult.validate(bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("ContestEvent", contestEvent.get());
            model.addAttribute("Result", result.get());
            return "events/edit-result";
        }

        ContestResultDao existingResult = result.get();

        if (submittedResult.isWithdrawn()) {
            existingResult.setPosition("W");
        } else if (submittedResult.isDisqualified()) {
            existingResult.setPosition("D");
        } else {
            existingResult.setPosition(String.valueOf(submittedResult.getPosition()));
        }

        existingResult.setResultAward(ResultAwardType.fromCode(submittedResult.getResultAwardCode()));

        existingResult.setBandName(submittedResult.getCompetedAs());
        existingResult.setNotes(submittedResult.getNotes());

        existingResult.setDraw(submittedResult.getDraw());
        existingResult.setDrawSecond(submittedResult.getDrawTwo());
        existingResult.setDrawThird(submittedResult.getDrawThree());

        existingResult.setPointsFirst(submittedResult.getPointsOne());
        existingResult.setPointsSecond(submittedResult.getPointsTwo());
        existingResult.setPointsThird(submittedResult.getPointsThree());
        existingResult.setPointsFourth(submittedResult.getPointsFour());
        existingResult.setPointsPenalty(submittedResult.getPointsPenalty());
        existingResult.setPointsTotal(submittedResult.getPointsTotal());

        if (submittedResult.getBandSlug() != null) {
            Optional<BandDao> band = this.bandService.fetchBySlug(submittedResult.getBandSlug());
            band.ifPresent(existingResult::setBand);
        } else {
            throw NotFoundException.bandNotFoundBySlug(submittedResult.getBandName());
        }

        if (submittedResult.getConductorSlug() != null) {
            Optional<PersonDao> conductor = this.personService.fetchBySlug(submittedResult.getConductorSlug());
            conductor.ifPresent(existingResult::setConductor);
        } else {
            existingResult.setConductor(null);
        }

        if (submittedResult.getConductorTwoSlug() != null) {
            Optional<PersonDao> conductor = this.personService.fetchBySlug(submittedResult.getConductorTwoSlug());
            conductor.ifPresent(existingResult::setConductorSecond);
        } else {
            existingResult.setConductorSecond(null);
        }

        if (submittedResult.getConductorThreeSlug() != null) {
            Optional<PersonDao> conductor = this.personService.fetchBySlug(submittedResult.getConductorThreeSlug());
            conductor.ifPresent(existingResult::setConductorThird);
        } else {
            existingResult.setConductorThird(null);
        }

        this.resultService.update(existingResult);

        return "redirect:/contests/{contestSlug}/" + contestEvent.get().getEventDateForUrl();
    }
}
