package uk.co.bbr.web.lookup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.groups.ContestGroupService;
import uk.co.bbr.services.groups.dao.ContestGroupDao;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.venues.VenueService;
import uk.co.bbr.services.venues.dao.VenueDao;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class LookupController {

    private final BandService bandService;
    private final PersonService personService;
    private final ContestService contestService;
    private final ContestGroupService contestGroupService;
    private final VenueService venueService;
    private final ObjectMapper objectMapper;

    private static final String MATCH_TAG_NAME = "matches";

    @IsBbrMember
    @GetMapping("/lookup/{type:[a-z]+}/data.json")
    public ResponseEntity<JsonNode>  lookupElement(@PathVariable("type") String type, @RequestParam("s") String searchString) {
        if (searchString.length() < 3) {
            throw NotFoundException.lookupNeedsThreeCharacters();
        }

        ObjectNode objectNode = switch (type) {
            case "person" -> this.lookupPerson(searchString);
            case "band" -> this.lookupBand(searchString);
            case "contest" -> this.lookupContest(searchString);
            case "venue" -> this.lookupVenue(searchString);
            case "group" -> this.lookupGroup(searchString);
            default -> throw NotFoundException.lookupTypeNotFound(type);
        };

        return ResponseEntity.ok(objectNode);
    }

    private ObjectNode lookupPerson(String searchString) {
        List<PersonDao> matchingPeople = this.personService.lookupByPrefix(searchString);

        ObjectNode rootNode = objectMapper.createObjectNode();
        ArrayNode people = rootNode.putArray(MATCH_TAG_NAME);

        for (PersonDao eachPerson : matchingPeople) {
            people.add(eachPerson.asLookup(this.objectMapper));
        }

        return rootNode;
    }

    private ObjectNode lookupBand(String searchString) {
        List<BandDao> matchingBands = this.bandService.lookupByPrefix(searchString);

        ObjectNode rootNode = objectMapper.createObjectNode();
        ArrayNode people = rootNode.putArray(MATCH_TAG_NAME);

        for (BandDao eachBand : matchingBands) {
            people.add(eachBand.asLookup(this.objectMapper));
        }

        return rootNode;
    }

    private ObjectNode lookupContest(String searchString) {
        List<ContestDao> matchingContests = this.contestService.lookupByPrefix(searchString);

        ObjectNode rootNode = objectMapper.createObjectNode();
        ArrayNode people = rootNode.putArray(MATCH_TAG_NAME);

        for (ContestDao eachContest : matchingContests) {
            people.add(eachContest.asLookup(this.objectMapper));
        }

        return rootNode;
    }

    private ObjectNode lookupVenue(String searchString) {
        List<VenueDao> matchingVenues = this.venueService.lookupByPrefix(searchString);

        ObjectNode rootNode = objectMapper.createObjectNode();
        ArrayNode people = rootNode.putArray(MATCH_TAG_NAME);

        for (VenueDao eachVenue : matchingVenues) {
            people.add(eachVenue.asLookup(this.objectMapper));
        }

        return rootNode;
    }

    private ObjectNode lookupGroup(String searchString) {
        List<ContestGroupDao> matchingGroups = this.contestGroupService.lookupByPrefix(searchString);

        ObjectNode rootNode = objectMapper.createObjectNode();
        ArrayNode groups = rootNode.putArray(MATCH_TAG_NAME);

        for (ContestGroupDao eachGroup : matchingGroups) {
            groups.add(eachGroup.asLookup(this.objectMapper));
        }

        return rootNode;
    }

}
