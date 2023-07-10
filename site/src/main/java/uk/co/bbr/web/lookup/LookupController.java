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
import uk.co.bbr.services.lookup.LookupService;
import uk.co.bbr.services.lookup.sql.dto.LookupSqlDto;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.venues.VenueService;
import uk.co.bbr.services.venues.dao.VenueDao;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class LookupController {

    private final ObjectMapper objectMapper;
    private final LookupService lookupService;

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
            case "piece" -> this.lookupPiece(searchString);
            case "tag" -> this.lookupTag(searchString);
            default -> throw NotFoundException.lookupTypeNotFound(type);
        };

        return ResponseEntity.ok(objectNode);
    }

    private ObjectNode lookupPerson(String searchString) {
        List<LookupSqlDto> matchingPeople = this.lookupService.lookupPeople(searchString);

        ObjectNode rootNode = objectMapper.createObjectNode();
        ArrayNode people = rootNode.putArray(MATCH_TAG_NAME);

        for (LookupSqlDto eachPerson : matchingPeople) {
            people.add(eachPerson.asLookup(this.objectMapper));
        }

        return rootNode;
    }

    private ObjectNode lookupBand(String searchString) {
        List<LookupSqlDto> matchingBands = this.lookupService.lookupBands(searchString);

        ObjectNode rootNode = objectMapper.createObjectNode();
        ArrayNode people = rootNode.putArray(MATCH_TAG_NAME);

        for (LookupSqlDto eachBand : matchingBands) {
            people.add(eachBand.asLookup(this.objectMapper));
        }

        return rootNode;
    }

    private ObjectNode lookupContest(String searchString) {
        List<LookupSqlDto> matchingContests = this.lookupService.lookupContests(searchString);

        ObjectNode rootNode = objectMapper.createObjectNode();
        ArrayNode people = rootNode.putArray(MATCH_TAG_NAME);

        for (LookupSqlDto eachContest : matchingContests) {
            people.add(eachContest.asLookup(this.objectMapper));
        }

        return rootNode;
    }

    private ObjectNode lookupVenue(String searchString) {
        List<LookupSqlDto> matchingVenues = this.lookupService.lookupVenues(searchString);

        ObjectNode rootNode = objectMapper.createObjectNode();
        ArrayNode people = rootNode.putArray(MATCH_TAG_NAME);

        for (LookupSqlDto eachVenue : matchingVenues) {
            people.add(eachVenue.asLookup(this.objectMapper));
        }

        return rootNode;
    }

    private ObjectNode lookupGroup(String searchString) {
        List<LookupSqlDto> matchingGroups = this.lookupService.lookupGroups(searchString);

        ObjectNode rootNode = objectMapper.createObjectNode();
        ArrayNode groups = rootNode.putArray(MATCH_TAG_NAME);

        for (LookupSqlDto eachGroup : matchingGroups) {
            groups.add(eachGroup.asLookup(this.objectMapper));
        }

        return rootNode;
    }

    private ObjectNode lookupPiece(String searchString) {
        List<LookupSqlDto> matchingGroups = this.lookupService.lookupPieces(searchString);

        ObjectNode rootNode = objectMapper.createObjectNode();
        ArrayNode groups = rootNode.putArray(MATCH_TAG_NAME);

        for (LookupSqlDto eachGroup : matchingGroups) {
            groups.add(eachGroup.asLookup(this.objectMapper));
        }

        return rootNode;
    }

    private ObjectNode lookupTag(String searchString) {
        List<LookupSqlDto> matchingGroups = this.lookupService.lookupTags(searchString);

        ObjectNode rootNode = objectMapper.createObjectNode();
        ArrayNode groups = rootNode.putArray(MATCH_TAG_NAME);

        for (LookupSqlDto eachGroup : matchingGroups) {
            groups.add(eachGroup.asLookup(this.objectMapper));
        }

        return rootNode;
    }

}
