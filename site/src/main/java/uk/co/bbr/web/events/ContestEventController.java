package uk.co.bbr.web.events;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.events.ContestEventService;
import uk.co.bbr.services.events.ResultService;
import uk.co.bbr.services.events.dao.ContestAdjudicatorDao;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.dao.ContestEventTestPieceDao;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.performances.PerformanceService;
import uk.co.bbr.services.performances.dto.CompetitorBandDto;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.UserService;
import uk.co.bbr.services.security.dao.SiteUserDao;
import uk.co.bbr.web.Tools;
import uk.co.bbr.web.security.annotations.IsBbrMember;
import uk.co.bbr.web.security.annotations.IsBbrPro;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ContestEventController {

    private final ContestEventService contestEventService;
    private final ResultService resultService;
    private final PerformanceService performanceService;
    private final SecurityService securityService;
    private final UserService userService;

    @GetMapping("/contests/{contestSlug:[\\-a-z\\d]{2,}}/{contestEventDate:\\d{4}-\\d{2}-\\d{2}}")
    public String contestEventDetails(Model model, @PathVariable String contestSlug, @PathVariable String contestEventDate) {
        LocalDate eventDate = Tools.parseEventDate(contestEventDate);
        Optional<ContestEventDao> contestEvent = this.contestEventService.fetchEvent(contestSlug, eventDate);

        if (contestEvent.isEmpty()) {
          contestEvent = this.contestEventService.fetchEventWithinWiderDateRange(contestSlug, eventDate);
          if (contestEvent.isEmpty()) {
            throw NotFoundException.eventNotFound(contestSlug, contestEventDate);
          }
        }

        List<ContestResultDao> eventResults = this.resultService.fetchForEvent(contestEvent.get());

        ContestEventDao nextEvent = this.contestEventService.fetchEventLinkNext(contestEvent.get());
        ContestEventDao previousEvent = this.contestEventService.fetchEventLinkPrevious(contestEvent.get());
        ContestEventDao upEvent = this.contestEventService.fetchEventLinkUp(contestEvent.get());
        ContestEventDao downEvent = this.contestEventService.fetchEventLinkDown(contestEvent.get());

        SiteUserDao contestOwner = null;
        Optional<SiteUserDao> contestOwnerOptional = this.userService.fetchUserByUsercode(contestEvent.get().getOwner());
        if (contestOwnerOptional.isPresent()) {
            contestOwner = contestOwnerOptional.get();
        } else {
            Optional<SiteUserDao> tjsOwnerOptional = this.userService.fetchUserByUsercode("tjs");
            if (tjsOwnerOptional.isPresent()) {
                contestOwner = tjsOwnerOptional.get();
            }
        }

        List<ContestEventTestPieceDao> eventTestPieces = this.contestEventService.listTestPieces(contestEvent.get());
        List<ContestAdjudicatorDao> adjudicators = this.contestEventService.fetchAdjudicators(contestEvent.get());

        SiteUserDao currentUser = this.securityService.getCurrentUser();

        contestEvent.get().setCanEdit(currentUser != null);
        for (ContestResultDao result : eventResults){
            result.setCanEdit(currentUser != null);
        }

        model.addAttribute("ContestEvent", contestEvent.get());
        model.addAttribute("EventTestPieces", eventTestPieces);
        model.addAttribute("Adjudicators", adjudicators);
        model.addAttribute("ContestOwner", contestOwner);
        model.addAttribute("EventResults", eventResults);
        model.addAttribute("NextEvent", nextEvent);
        model.addAttribute("PreviousEvent", previousEvent);
        model.addAttribute("SectionUp", upEvent);
        model.addAttribute("SectionDown", downEvent);

        return "events/event";
    }

    @IsBbrPro
    @GetMapping("/contests/{contestSlug:[\\-a-z\\d]{2,}}/{contestEventDate:\\d{4}-\\d{2}-\\d{2}}/competitors")
    public String contestEventCompetitors(Model model, @PathVariable String contestSlug, @PathVariable String contestEventDate) {
        LocalDate eventDate = Tools.parseEventDate(contestEventDate);
        Optional<ContestEventDao> contestEvent = this.contestEventService.fetchEvent(contestSlug, eventDate);

        if (contestEvent.isEmpty()) {
            contestEvent = this.contestEventService.fetchEventWithinWiderDateRange(contestSlug, eventDate);
            if (contestEvent.isEmpty()) {
                throw NotFoundException.eventNotFound(contestSlug, contestEventDate);
            }
        }

        List<CompetitorBandDto> competitors = this.performanceService.fetchPerformancesForEvent(contestEvent.get());

        model.addAttribute("ContestEvent", contestEvent.get());
        model.addAttribute("Competitors", competitors);

        return "events/competitors";
    }


    @GetMapping("/contests/{contestSlug:[\\-a-z\\d]{2,}}/{contestEventDate:\\d{4}-\\d{2}-\\d{2}}/performer")
    public String eventPerformerSelectBand(Model model, @PathVariable("contestSlug")  String contestSlug, @PathVariable("contestEventDate") String contestEventDate) {
        LocalDate eventDate = Tools.parseEventDate(contestEventDate);
        Optional<ContestEventDao> contestEvent = this.contestEventService.fetchEvent(contestSlug, eventDate);
        if (contestEvent.isEmpty()) {
            throw NotFoundException.eventNotFound(contestSlug, contestEventDate);
        }

        SiteUserDao currentUser = this.securityService.getCurrentUser();

        List<ContestResultDao> eventResults = this.resultService.fetchForEvent(contestEvent.get());

        model.addAttribute("ContestEvent", contestEvent.get());
        model.addAttribute("EventResults", eventResults);
        model.addAttribute("User", currentUser);

        return "events/performer-bands";
    }

    @IsBbrMember
    @GetMapping("/contests/{contestSlug:[\\-a-z\\d]{2,}}/{contestEventDate:\\d{4}-\\d{2}-\\d{2}}/performer/{resultId:\\d+}")
    public String eventPerformerSelectBand(Model model, @PathVariable("contestSlug") String contestSlug, @PathVariable("contestEventDate") String contestEventDate, @PathVariable("resultId") Long resultId) {
        LocalDate eventDate = Tools.parseEventDate(contestEventDate);
        Optional<ContestEventDao> contestEvent = this.contestEventService.fetchEvent(contestSlug, eventDate);
        if (contestEvent.isEmpty()) {
            throw NotFoundException.eventNotFound(contestSlug, contestEventDate);
        }

        Optional<ContestResultDao> result = this.resultService.fetchById(resultId);
        if (result.isEmpty()) {
            throw NotFoundException.resultNotFoundById(resultId);
        }

        SiteUserDao currentUser = this.securityService.getCurrentUser();
        this.performanceService.linkUserPerformance(currentUser, result.get());

        return "redirect:/profile/performances";
    }
}
