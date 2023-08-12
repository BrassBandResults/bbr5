package uk.co.bbr.web.results;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.ContestTypeService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dao.ContestTypeDao;
import uk.co.bbr.services.events.ContestEventService;
import uk.co.bbr.services.events.ResultService;
import uk.co.bbr.services.events.dao.ContestAdjudicatorDao;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.dao.ContestEventTestPieceDao;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.events.types.ContestEventDateResolution;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.pieces.PieceService;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.results.ParseResultService;
import uk.co.bbr.services.results.dto.ParseResultDto;
import uk.co.bbr.services.results.dto.ParsedResultsDto;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.venues.VenueService;
import uk.co.bbr.services.venues.dao.VenueDao;
import uk.co.bbr.web.Tools;
import uk.co.bbr.web.framework.AbstractEventController;
import uk.co.bbr.web.results.forms.*;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class AddResultsController extends AbstractEventController {

    private final ContestService contestService;
    private final SecurityService securityService;
    private final ContestTypeService contestTypeService;
    private final ContestEventService contestEventService;
    private final ResultService resultService;
    private final PieceService pieceService;
    private final PersonService personService;
    private final BandService bandService;
    private final VenueService venueService;
    private final ParseResultService parseResultService;

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

        if (submittedForm.getContestSlug() != null && submittedForm.getContestSlug().strip().length() > 0) {
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
        LocalDate lastMonth = eventDate.minus(2, ChronoUnit.MONTHS);
        LocalDate nextMonth = eventDate.plus(2, ChronoUnit.MONTHS);
        Optional<ContestEventDao> existingEvent = this.contestEventService.fetchEventBetweenDates(matchingContest.get(), lastMonth, nextMonth);
        if (existingEvent.isPresent()) {
            // does it have results?
            List<ContestResultDao> existingResults = this.resultService.fetchForEvent(existingEvent.get());
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
        ContestEventDao contestEvent = this.contestEventFromUrlParameters(this.contestEventService, contestSlug, contestEventDate);

        AddResultsContestTypeForm form = new AddResultsContestTypeForm();
        form.setContestType(contestEvent.getContestType().getId());

        List<ContestTypeDao> contestTypes = this.contestTypeService.fetchAll();

        model.addAttribute("ContestEvent", contestEvent);
        model.addAttribute("ContestTypes", contestTypes);
        model.addAttribute("Form", form);

        return "results/add-results-3-event-type";
    }

    @IsBbrMember
    @PostMapping("/add-results/3/{contestSlug:[\\-a-z\\d]{2,}}/{contestEventDate:\\d{4}-\\d{2}-\\d{2}}")
    public String addResultsContestTypeStagePost(Model model, @Valid @ModelAttribute("Form") AddResultsContestTypeForm submittedForm, BindingResult bindingResult, @PathVariable("contestSlug") String contestSlug, @PathVariable String contestEventDate) {
        ContestEventDao contestEvent = this.contestEventFromUrlParameters(this.contestEventService, contestSlug, contestEventDate);

        submittedForm.validate(bindingResult);

        if (bindingResult.hasErrors()) {
            List<ContestTypeDao> contestTypes = this.contestTypeService.fetchAll();
            model.addAttribute("ContestEvent", contestEvent);
            model.addAttribute("ContestTypes", contestTypes);
            return "results/add-results-3-event-type";
        }

        Optional<ContestTypeDao> contestType = this.contestTypeService.fetchById(submittedForm.getContestType());
        if (contestType.isEmpty()) {
            throw NotFoundException.contestTypeNotFoundForId(submittedForm.getContestType());
        }

        contestEvent.setContestType(contestType.get());
        this.contestEventService.update(contestEvent);

        return "redirect:/add-results/4/{contestSlug}/{contestEventDate}";
    }

    @IsBbrMember
    @GetMapping("/add-results/4/{contestSlug:[\\-a-z\\d]{2,}}/{contestEventDate:\\d{4}-\\d{2}-\\d{2}}")
    public String addTestPieceStageGet(Model model, @PathVariable("contestSlug") String contestSlug, @PathVariable String contestEventDate) {
        ContestEventDao contestEvent = this.contestEventFromUrlParameters(this.contestEventService, contestSlug, contestEventDate);

        if (!contestEvent.getContestType().isTestPiece()) {
            return "redirect:/add-results/5/{contestSlug}/{contestEventDate}";
        }

        AddResultsTestPieceForm form = new AddResultsTestPieceForm();
        model.addAttribute("ContestEvent", contestEvent);
        model.addAttribute("Form", form);

        return "results/add-results-4-test-piece";
    }

    @IsBbrMember
    @PostMapping("/add-results/4/{contestSlug:[\\-a-z\\d]{2,}}/{contestEventDate:\\d{4}-\\d{2}-\\d{2}}")
    public String addTestPieceStagePost(Model model, @Valid @ModelAttribute("Form") AddResultsTestPieceForm submittedForm, BindingResult bindingResult, @PathVariable("contestSlug") String contestSlug, @PathVariable String contestEventDate) {
        ContestEventDao contestEvent = this.contestEventFromUrlParameters(this.contestEventService, contestSlug, contestEventDate);

        submittedForm.validate(bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("ContestEvent", contestEvent);
            return "results/add-results-4-test-piece";
        }

        if (submittedForm.getTestPieceName() != null && submittedForm.getTestPieceName().strip().length() > 0) {
            PieceDao pieceToLink = null;
            Optional<PieceDao> testPiece = this.pieceService.fetchBySlug(submittedForm.getTestPieceSlug());
            if (testPiece.isEmpty()) {
                pieceToLink = this.pieceService.create(submittedForm.getTestPieceName());
            } else {
                pieceToLink = testPiece.get();
            }

            if (pieceToLink != null) {
                this.contestEventService.addTestPieceToContest(contestEvent, pieceToLink);
            }
        }

        return "redirect:/add-results/5/{contestSlug}/{contestEventDate}";
    }

    @IsBbrMember
    @GetMapping("/add-results/5/{contestSlug:[\\-a-z\\d]{2,}}/{contestEventDate:\\d{4}-\\d{2}-\\d{2}}")
    public String addVenueStageGet(Model model, @PathVariable("contestSlug") String contestSlug, @PathVariable String contestEventDate) {
        ContestEventDao contestEvent = this.contestEventFromUrlParameters(this.contestEventService, contestSlug, contestEventDate);

        List<ContestEventTestPieceDao> pieces = this.contestEventService.listTestPieces(contestEvent);

        AddResultsVenueForm form = new AddResultsVenueForm();
        model.addAttribute("TestPieces", pieces);
        model.addAttribute("ContestEvent", contestEvent);
        model.addAttribute("Form", form);

        return "results/add-results-5-venue";
    }

    @IsBbrMember
    @PostMapping("/add-results/5/{contestSlug:[\\-a-z\\d]{2,}}/{contestEventDate:\\d{4}-\\d{2}-\\d{2}}")
    public String addVenueStagePost(Model model, @Valid @ModelAttribute("Form") AddResultsVenueForm submittedForm, BindingResult bindingResult, @PathVariable("contestSlug") String contestSlug, @PathVariable String contestEventDate) {
        ContestEventDao contestEvent = this.contestEventFromUrlParameters(this.contestEventService, contestSlug, contestEventDate);

        submittedForm.validate(bindingResult);

        if (bindingResult.hasErrors()) {
            List<ContestEventTestPieceDao> pieces = this.contestEventService.listTestPieces(contestEvent);

            model.addAttribute("TestPieces", pieces);
            model.addAttribute("ContestEvent", contestEvent);
            return "results/add-results-5-venue";
        }

        if (submittedForm.getVenueName() != null && submittedForm.getVenueName().strip().length() > 0) {
            VenueDao venueToLink = null;
            Optional<VenueDao> venue = this.venueService.fetchBySlug(submittedForm.getVenueSlug());
            if (venue.isEmpty()) {
                venueToLink = this.venueService.create(submittedForm.getVenueName());
            } else {
                venueToLink = venue.get();
            }

            if (venueToLink != null) {
                contestEvent.setVenue(venueToLink);
                this.contestEventService.update(contestEvent);
            }
        }

        return "redirect:/add-results/6/{contestSlug}/{contestEventDate}";
    }

    @IsBbrMember
    @GetMapping("/add-results/6/{contestSlug:[\\-a-z\\d]{2,}}/{contestEventDate:\\d{4}-\\d{2}-\\d{2}}")
    public String addBandsStageGet(Model model, @PathVariable("contestSlug") String contestSlug, @PathVariable String contestEventDate) {
        ContestEventDao contestEvent = this.contestEventFromUrlParameters(this.contestEventService, contestSlug, contestEventDate);

        List<ContestEventTestPieceDao> pieces = this.contestEventService.listTestPieces(contestEvent);

        AddResultsBandsForm form = new AddResultsBandsForm();
        model.addAttribute("TestPieces", pieces);
        model.addAttribute("ContestEvent", contestEvent);
        model.addAttribute("ParsedResults", Collections.emptyList());
        model.addAttribute("Form", form);

        return "results/add-results-6-bands";
    }

    @IsBbrMember
    @PostMapping("/add-results/6/{contestSlug:[\\-a-z\\d]{2,}}/{contestEventDate:\\d{4}-\\d{2}-\\d{2}}")
    public String addBandsStagePost(Model model, @Valid @ModelAttribute("Form") AddResultsBandsForm submittedForm, BindingResult bindingResult, @PathVariable("contestSlug") String contestSlug, @PathVariable String contestEventDate) {
        ContestEventDao contestEvent = this.contestEventFromUrlParameters(this.contestEventService, contestSlug, contestEventDate);

        submittedForm.validate(bindingResult);

        if (bindingResult.hasErrors()) {
            List<ContestEventTestPieceDao> pieces = this.contestEventService.listTestPieces(contestEvent);

            model.addAttribute("TestPieces", pieces);
            model.addAttribute("ContestEvent", contestEvent);
            model.addAttribute("ParsedResults", Collections.emptyList());
            return "results/add-results-6-bands";
        }

        String resultsBlock = submittedForm.getResultBlock();
        ParsedResultsDto parsedResults = this.parseResultService.parseBlock(resultsBlock, contestEvent.getEventDate());
        if (parsedResults.allGreen()) {
            for (ParseResultDto eachParsedResult : parsedResults.getResultLines()) {
                ContestResultDao eachResult = eachParsedResult.buildContestResult(contestEvent, this.bandService, this.personService);
                this.resultService.addResult(contestEvent, eachResult);
                this.securityService.addOnePoint(this.securityService.getCurrentUsername());
            }

            return "redirect:/add-results/7/{contestSlug}/{contestEventDate}";
        }

        List<ContestEventTestPieceDao> pieces = this.contestEventService.listTestPieces(contestEvent);
        model.addAttribute("TestPieces", pieces);
        model.addAttribute("ContestEvent", contestEvent);
        model.addAttribute("ParsedResults", parsedResults.getResultLines());
        return "results/add-results-6-bands";
    }

    @IsBbrMember
    @GetMapping("/add-results/7/{contestSlug:[\\-a-z\\d]{2,}}/{contestEventDate:\\d{4}-\\d{2}-\\d{2}}")
    public String addAdjudicatorStageGet(Model model, @PathVariable("contestSlug") String contestSlug, @PathVariable String contestEventDate) {
        ContestEventDao contestEvent = this.contestEventFromUrlParameters(this.contestEventService, contestSlug, contestEventDate);

        List<ContestEventTestPieceDao> pieces = this.contestEventService.listTestPieces(contestEvent);
        List<ContestResultDao> eventResults = this.resultService.fetchForEvent(contestEvent);
        List<ContestAdjudicatorDao> adjudicators = this.contestEventService.fetchAdjudicators(contestEvent);

        AddResultsAdjudicatorForm form = new AddResultsAdjudicatorForm();
        model.addAttribute("TestPieces", pieces);
        model.addAttribute("ContestEvent", contestEvent);
        model.addAttribute("EventResults", eventResults);
        model.addAttribute("Adjudicators", adjudicators);
        model.addAttribute("Form", form);

        return "results/add-results-7-adjudicators";
    }

    @IsBbrMember
    @GetMapping("/add-results/7/{contestSlug:[\\-a-z\\d]{2,}}/{contestEventDate:\\d{4}-\\d{2}-\\d{2}}/delete/{adjudicatorId:\\d+}")
    public String deleteAdjudicator(Model model, @PathVariable("contestSlug") String contestSlug, @PathVariable String contestEventDate, @PathVariable Long adjudicatorId) {
        ContestEventDao contestEvent = this.contestEventFromUrlParameters(this.contestEventService, contestSlug, contestEventDate);

        this.contestEventService.removeAdjudicator(contestEvent, adjudicatorId);

        return "redirect:/add-results/7/{contestSlug}/{contestEventDate}";
    }

    @IsBbrMember
    @PostMapping("/add-results/7/{contestSlug:[\\-a-z\\d]{2,}}/{contestEventDate:\\d{4}-\\d{2}-\\d{2}}")
    public String addAdjudicatorStagePost(Model model, @Valid @ModelAttribute("Form") AddResultsAdjudicatorForm submittedForm, BindingResult bindingResult, @PathVariable("contestSlug") String contestSlug, @PathVariable String contestEventDate) {
        ContestEventDao contestEvent = this.contestEventFromUrlParameters(this.contestEventService, contestSlug, contestEventDate);

        submittedForm.validate(bindingResult);

        if (submittedForm.getAdjudicatorSlug() != null && submittedForm.getAdjudicatorSlug().length() > 0) {
            Optional<PersonDao> adjudicatorPerson = this.personService.fetchBySlug(submittedForm.getAdjudicatorSlug());

            if (adjudicatorPerson.isPresent()) {
                this.contestEventService.addAdjudicator(contestEvent, adjudicatorPerson.get());
            }
        }

        ContestAdjudicatorDao eventAdjudicator = new ContestAdjudicatorDao();
        eventAdjudicator.setContestEvent(contestEvent);

        List<ContestEventTestPieceDao> pieces = this.contestEventService.listTestPieces(contestEvent);
        List<ContestResultDao> eventResults = this.resultService.fetchForEvent(contestEvent);
        List<ContestAdjudicatorDao> adjudicators = this.contestEventService.fetchAdjudicators(contestEvent);

        model.addAttribute("TestPieces", pieces);
        model.addAttribute("ContestEvent", contestEvent);
        model.addAttribute("EventResults", eventResults);
        model.addAttribute("Adjudicators", adjudicators);
        return "results/add-results-7-adjudicators";
    }

    @IsBbrMember
    @GetMapping("/add-results/8/{contestSlug:[\\-a-z\\d]{2,}}/{contestEventDate:\\d{4}-\\d{2}-\\d{2}}")
    public String addNotesStageGet(Model model, @PathVariable("contestSlug") String contestSlug, @PathVariable String contestEventDate) {
        ContestEventDao contestEvent = this.contestEventFromUrlParameters(this.contestEventService, contestSlug, contestEventDate);

        List<ContestEventTestPieceDao> pieces = this.contestEventService.listTestPieces(contestEvent);
        List<ContestResultDao> eventResults = this.resultService.fetchForEvent(contestEvent);
        List<ContestAdjudicatorDao> adjudicators = this.contestEventService.fetchAdjudicators(contestEvent);

        AddResultsNotesForm form = new AddResultsNotesForm();
        model.addAttribute("TestPieces", pieces);
        model.addAttribute("ContestEvent", contestEvent);
        model.addAttribute("EventResults", eventResults);
        model.addAttribute("Adjudicators", adjudicators);
        model.addAttribute("Form", form);

        return "results/add-results-8-notes";
    }

    @IsBbrMember
    @PostMapping("/add-results/8/{contestSlug:[\\-a-z\\d]{2,}}/{contestEventDate:\\d{4}-\\d{2}-\\d{2}}")
    public String addNotesStagePost(Model model, @Valid @ModelAttribute("Form") AddResultsNotesForm submittedForm, BindingResult bindingResult, @PathVariable("contestSlug") String contestSlug, @PathVariable String contestEventDate) {
        ContestEventDao contestEvent = this.contestEventFromUrlParameters(this.contestEventService, contestSlug, contestEventDate);

        submittedForm.validate(bindingResult);

        if (submittedForm.getNotes() != null && submittedForm.getNotes().strip().length() > 0) {
            if (contestEvent.getNotes() == null || contestEvent.getNotes().strip().length() == 0) {
                contestEvent.setNotes(submittedForm.getNotes());
            } else {
                contestEvent.setNotes(contestEvent.getNotes() + "\n" + submittedForm.getNotes());
            }

            this.contestEventService.update(contestEvent);
        }

        return "redirect:/contests/{contestSlug}/{contestEventDate}";
    }
}
