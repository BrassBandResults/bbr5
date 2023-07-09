package uk.co.bbr.web.search;

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
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.groups.ContestGroupService;
import uk.co.bbr.services.lookup.LookupService;
import uk.co.bbr.services.lookup.sql.dto.LookupSqlDto;
import uk.co.bbr.services.venues.VenueService;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class SearchController {

    private final ObjectMapper objectMapper;
    private final LookupService lookupService;

    private static final String MATCH_TAG_NAME = "matches";

    @IsBbrMember
    @GetMapping("/search")
    public String search(Model model, @RequestParam("q") String searchString) {
        List<LookupSqlDto> matches = new ArrayList<>();

        matches.addAll(this.lookupService.lookupBands(searchString));
        matches.addAll(this.lookupService.lookupPeople(searchString));
        matches.addAll(this.lookupService.lookupGroups(searchString));
        matches.addAll(this.lookupService.lookupContests(searchString));
        matches.addAll(this.lookupService.lookupVenues(searchString));

        model.addAttribute("SearchString", searchString);
        model.addAttribute("SearchResults", matches);

        if (matches.isEmpty()) {
            return "search/results-no-matches";
        }

        return "search/results";
    }
}
