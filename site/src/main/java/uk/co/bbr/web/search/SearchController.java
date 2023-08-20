package uk.co.bbr.web.search;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.co.bbr.services.lookup.LookupService;
import uk.co.bbr.services.lookup.sql.dto.LookupSqlDto;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class SearchController {

    private final LookupService lookupService;

    @GetMapping("/search")
    public String search(Model model, @RequestParam("q") String searchString) {
        List<LookupSqlDto> matches = new ArrayList<>();

        matches.addAll(this.lookupService.lookupBands(searchString));
        matches.addAll(this.lookupService.lookupBandAlias(searchString));
        matches.addAll(this.lookupService.lookupContests(searchString));
        matches.addAll(this.lookupService.lookupContestAlias(searchString));
        matches.addAll(this.lookupService.lookupGroups(searchString));
        matches.addAll(this.lookupService.lookupGroupAlias(searchString));
        matches.addAll(this.lookupService.lookupTags(searchString));
        matches.addAll(this.lookupService.lookupPeople(searchString));
        matches.addAll(this.lookupService.lookupPeopleAlias(searchString));
        matches.addAll(this.lookupService.lookupPieces(searchString));
        matches.addAll(this.lookupService.lookupPieceAlias(searchString));
        matches.addAll(this.lookupService.lookupVenues(searchString));
        matches.addAll(this.lookupService.lookupVenueAlias(searchString));

        List<LookupSqlDto> orderedMatches = matches.stream().sorted(Comparator.comparing(LookupSqlDto::getName)).toList();

        model.addAttribute("SearchString", searchString);
        model.addAttribute("SearchResults", orderedMatches);

        if (matches.isEmpty()) {
            return "search/results-no-matches";
        }

        return "search/results";
    }
}
