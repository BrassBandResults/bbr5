package uk.co.bbr.web.venues;

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
import org.springframework.web.bind.annotation.RequestParam;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.map.LocationService;
import uk.co.bbr.services.map.dto.Location;
import uk.co.bbr.services.venues.VenueService;
import uk.co.bbr.services.venues.dao.VenueAliasDao;
import uk.co.bbr.services.venues.dao.VenueDao;
import uk.co.bbr.services.venues.dto.VenueContestDto;
import uk.co.bbr.services.venues.dto.VenueContestYearDto;
import uk.co.bbr.web.Tools;
import uk.co.bbr.web.security.annotations.IsBbrMember;
import uk.co.bbr.web.security.annotations.IsBbrPro;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class VenueController {

    private final VenueService venueService;
    private final ContestService contestService;
    private final LocationService locationService;
    private final ObjectMapper objectMapper;

    @GetMapping("/venues/{venueSlug:[\\-a-z\\d]{2,}}")
    public String venue(Model model, @PathVariable("venueSlug") String venueSlug) {
        Optional<VenueDao> venue = this.venueService.fetchBySlug(venueSlug);
        if (venue.isEmpty()) {
            throw NotFoundException.venueNotFoundBySlug(venueSlug);
        }
        List<VenueAliasDao> previousNames = this.venueService.fetchAliases(venue.get());

        List<VenueContestDto> venueContests = this.venueService.fetchVenueContests(venue.get());

        model.addAttribute("Venue", venue.get());
        model.addAttribute("PreviousNames", previousNames);
        model.addAttribute("VenueContests", venueContests);
        model.addAttribute("Notes", Tools.markdownToHTML(venue.get().getNotes()));

        return "venues/venue";
    }

    @IsBbrPro
    @GetMapping("/venues/{venueSlug:[\\-a-z\\d]{2,}}/{contestSlug:[\\-a-z\\d]{2,}}")
    public String venueContest(Model model, @PathVariable("venueSlug") String venueSlug, @PathVariable("contestSlug") String contestSlug) {
        Optional<VenueDao> venue = this.venueService.fetchBySlug(venueSlug);
        if (venue.isEmpty()) {
            throw NotFoundException.venueNotFoundBySlug(venueSlug);
        }
        Optional<ContestDao> contest = this.contestService.fetchBySlug(contestSlug);
        if (contest.isEmpty()) {
            throw NotFoundException.contestNotFoundBySlug(contestSlug);
        }

        List<ContestEventDao> venueContests = this.venueService.fetchVenueContestEvents(venue.get(), contest.get());

        model.addAttribute("Venue", venue.get());
        model.addAttribute("VenueContests", venueContests);
        model.addAttribute("Notes", Tools.markdownToHTML(venue.get().getNotes()));

        return "venues/contest";
    }

    @IsBbrPro
    @GetMapping("/venues/{venueSlug:[\\-a-z\\d]{2,}}/years")
    public String venueYears(Model model, @PathVariable("venueSlug") String venueSlug) {
        Optional<VenueDao> venue = this.venueService.fetchBySlug(venueSlug);
        if (venue.isEmpty()) {
            throw NotFoundException.venueNotFoundBySlug(venueSlug);
        }
        List<VenueAliasDao> previousNames = this.venueService.fetchAliases(venue.get());

        List<VenueContestYearDto> venueYears = this.venueService.fetchVenueContestYears(venue.get());

        model.addAttribute("Venue", venue.get());
        model.addAttribute("PreviousNames", previousNames);
        model.addAttribute("VenueYears", venueYears);
        model.addAttribute("Notes", Tools.markdownToHTML(venue.get().getNotes()));

        return "venues/years";
    }

    @IsBbrMember
    @GetMapping("/venues/{venueSlug:[\\-a-z\\d]{2,}}/map")
    public String venueMap(Model model, @PathVariable("venueSlug") String venueSlug) {
        Optional<VenueDao> venue = this.venueService.fetchBySlug(venueSlug);
        if (venue.isEmpty()) {
            throw NotFoundException.venueNotFoundBySlug(venueSlug);
        }
        List<VenueAliasDao> previousNames = this.venueService.fetchAliases(venue.get());
        boolean venueHasLocation = venue.get().hasLocation();
        int zoomLevel = 6;
        if (!venueHasLocation){
            zoomLevel = 2;
        }

        model.addAttribute("Venue", venue.get());
        model.addAttribute("PreviousNames", previousNames);
        model.addAttribute("ZoomLevel", zoomLevel);
        model.addAttribute("HasNoLocation", !venueHasLocation);
        model.addAttribute("Notes", Tools.markdownToHTML(venue.get().getNotes()));

        return "venues/map";
    }

    @IsBbrMember
    @GetMapping("/venues/{venueSlug:[\\-a-z\\d]{2,}}/map/nearby.json")
    public ResponseEntity<JsonNode> venueMapIcons(@PathVariable("venueSlug") String venueSlug, @RequestParam(value= "distance", required=false) Integer distance) {
        Optional<VenueDao> venue = this.venueService.fetchBySlug(venueSlug);
        if (venue.isEmpty()) {
            throw NotFoundException.venueNotFoundBySlug(venueSlug);
        }

        Integer distanceKm = distance;
        if (distanceKm == null) {
            distanceKm = 25; // km
        }

        List<Location> locationsForMap = this.locationService.fetchLocationsNear(venue.get().getLatitude(), venue.get().getLongitude(), distanceKm);

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type", "FeatureCollection");
        ArrayNode features = objectNode.putArray("features");

        for (Location eachLocation : locationsForMap) {
            features.add(eachLocation.asGeoJson(this.objectMapper));
        }

        return ResponseEntity.ok(objectNode);
    }

    @IsBbrPro
    @GetMapping("/venues/{venueSlug:[\\-a-z\\d]{2,}}/years/{year:\\d{4}}")
    public String venueYearEvents(Model model, @PathVariable("venueSlug") String venueSlug, @PathVariable("year") int year) {
        Optional<VenueDao> venue = this.venueService.fetchBySlug(venueSlug);
        if (venue.isEmpty()) {
            throw NotFoundException.venueNotFoundBySlug(venueSlug);
        }

        List<ContestEventDao> events = this.venueService.fetchVenueContestYear(venue.get(), year);

        model.addAttribute("Venue", venue.get());
        model.addAttribute("Year", Integer.toString(year));
        model.addAttribute("Events", events);
        model.addAttribute("Notes", Tools.markdownToHTML(venue.get().getNotes()));

        return "venues/year";
    }
}
