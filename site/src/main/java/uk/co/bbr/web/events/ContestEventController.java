package uk.co.bbr.web.events;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
import uk.co.bbr.web.framework.AbstractEventController;
import uk.co.bbr.web.security.annotations.IsBbrMember;
import uk.co.bbr.web.security.annotations.IsBbrPro;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ContestEventController extends AbstractEventController {

    private final ContestEventService contestEventService;
    private final ResultService resultService;
    private final PerformanceService performanceService;
    private final SecurityService securityService;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @GetMapping("/contests/{contestSlug:[\\-a-z\\d]{2,}}/{contestEventDate:\\d{4}-\\d{2}-\\d{2}}")
    public String contestEventDetails(Model model, @PathVariable String contestSlug, @PathVariable String contestEventDate) {
        ContestEventDao contestEvent = this.contestEventFromUrlParameters(this.contestEventService, contestSlug, contestEventDate);

        List<ContestResultDao> eventResults = this.resultService.fetchForEvent(contestEvent);

        ContestEventDao nextEvent = this.contestEventService.fetchEventLinkNext(contestEvent);
        ContestEventDao previousEvent = this.contestEventService.fetchEventLinkPrevious(contestEvent);
        ContestEventDao upEvent = this.contestEventService.fetchEventLinkUp(contestEvent);
        ContestEventDao downEvent = this.contestEventService.fetchEventLinkDown(contestEvent);

        SiteUserDao contestOwner = null;
        Optional<SiteUserDao> contestOwnerOptional = this.userService.fetchUserByUsercode(contestEvent.getOwner());
        if (contestOwnerOptional.isPresent()) {
            contestOwner = contestOwnerOptional.get();
        } else {
            Optional<SiteUserDao> tjsOwnerOptional = this.userService.fetchUserByUsercode("tjs");
            if (tjsOwnerOptional.isPresent()) {
                contestOwner = tjsOwnerOptional.get();
            }
        }

        List<ContestEventTestPieceDao> eventTestPieces = this.contestEventService.listTestPieces(contestEvent);
        List<ContestAdjudicatorDao> adjudicators = this.contestEventService.fetchAdjudicators(contestEvent);

        this.resultService.workOutCanEdit(contestEvent, eventResults);

        model.addAttribute("ContestEvent", contestEvent);
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
        ContestEventDao contestEvent = this.contestEventFromUrlParameters(this.contestEventService, contestSlug, contestEventDate);

        List<CompetitorBandDto> competitors = this.performanceService.fetchPerformancesForEvent(contestEvent);

        model.addAttribute("ContestEvent", contestEvent);
        model.addAttribute("Competitors", competitors);

        return "events/competitors";
    }

    @IsBbrMember
    @GetMapping("/contests/{contestSlug:[\\-a-z\\d]{2,}}/{contestEventDate:\\d{4}-\\d{2}-\\d{2}}/geography")
    public String contestEventMap(Model model, @PathVariable String contestSlug, @PathVariable String contestEventDate) {
        ContestEventDao contestEvent = this.contestEventFromUrlParameters(this.contestEventService, contestSlug, contestEventDate);

        List<ContestResultDao> eventResults = this.resultService.fetchForEvent(contestEvent);

        model.addAttribute("ContestEvent", contestEvent);
        model.addAttribute("EventResults", eventResults);

        return "events/map-competitors";
    }

    @IsBbrMember
    @GetMapping("/contests/{contestSlug:[\\-a-z\\d]{2,}}/{contestEventDate:\\d{4}-\\d{2}-\\d{2}}/map/geography.json")
    public ResponseEntity<JsonNode> contestEventMapJson(@PathVariable String contestSlug, @PathVariable String contestEventDate) {
        ContestEventDao contestEvent = this.contestEventFromUrlParameters(this.contestEventService, contestSlug, contestEventDate);

        List<ContestResultDao> eventResults = this.resultService.fetchForEvent(contestEvent);

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type", "FeatureCollection");
        ArrayNode features = objectNode.putArray("features");

        for (ContestResultDao eachResult : eventResults) {
            if (eachResult.getBand().hasLocation()) {
                features.add(eachResult.getBand().asGeoJson(this.objectMapper));
            }
        }
        if (contestEvent.getVenue().hasLocation()) {
            features.add(contestEvent.getVenue().asGeoJson(this.objectMapper));
        }

        return ResponseEntity.ok(objectNode);
    }




    @GetMapping("/contests/{contestSlug:[\\-a-z\\d]{2,}}/{contestEventDate:\\d{4}-\\d{2}-\\d{2}}/performer")
    public String eventPerformerSelectBand(Model model, @PathVariable("contestSlug")  String contestSlug, @PathVariable("contestEventDate") String contestEventDate) {
        ContestEventDao contestEvent = this.contestEventFromUrlParameters(this.contestEventService, contestSlug, contestEventDate);

        SiteUserDao currentUser = this.securityService.getCurrentUser();

        List<ContestResultDao> eventResults = this.resultService.fetchForEvent(contestEvent);

        model.addAttribute("ContestEvent", contestEvent);
        model.addAttribute("EventResults", eventResults);
        model.addAttribute("User", currentUser);

        return "events/performer-bands";
    }

    @IsBbrMember
    @GetMapping("/contests/{contestSlug:[\\-a-z\\d]{2,}}/{contestEventDate:\\d{4}-\\d{2}-\\d{2}}/performer/{resultId:\\d+}")
    public String eventPerformerSelectBand(@PathVariable("contestSlug") String contestSlug, @PathVariable("contestEventDate") String contestEventDate, @PathVariable("resultId") Long resultId) {
        this.contestEventFromUrlParameters(this.contestEventService, contestSlug, contestEventDate);

        Optional<ContestResultDao> result = this.resultService.fetchById(resultId);
        if (result.isEmpty()) {
            throw NotFoundException.resultNotFoundById(resultId);
        }

        SiteUserDao currentUser = this.securityService.getCurrentUser();
        this.performanceService.linkUserPerformance(currentUser, result.get());

        return "redirect:/profile/performances";
    }
}
