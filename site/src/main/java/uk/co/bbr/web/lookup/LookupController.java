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
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.lookup.LookupService;
import uk.co.bbr.services.lookup.sql.dto.LookupSqlDto;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class LookupController {

    private final ObjectMapper objectMapper;
    private final LookupService lookupService;

    private static final String MATCH_TAG_NAME = "matches";
    private static final String QUERY_PARAMETER_NAME = "s";

    @IsBbrMember
    @GetMapping("/lookup/{type:[a-z]+}/data.json")
    public ResponseEntity<JsonNode>  lookupElement(@PathVariable("type") String type, @RequestParam(QUERY_PARAMETER_NAME) String searchString) {
        if (searchString.length() < 3) {
            throw NotFoundException.lookupNeedsThreeCharacters();
        }

        List<LookupSqlDto> results = switch (type) {
            case "person" -> this.lookupService.lookupPeopleAndAlias(searchString);
            case "band" -> this.lookupService.lookupBandsAndAlias(searchString);
            case "contest" -> this.lookupService.lookupContestsAndAlias(searchString);
            case "venue" -> this.lookupService.lookupVenuesAndAlias(searchString);
            case "group" -> this.lookupService.lookupGroupsAndAlias(searchString);
            case "piece" -> this.lookupService.lookupPiecesAndAlias(searchString);
            case "tag" -> this.lookupService.lookupTags(searchString);
            default -> throw NotFoundException.lookupTypeNotFound(type);
        };

        results = results.stream().sorted(Comparator.comparing(LookupSqlDto::getName)).toList();

        ObjectNode rootNode = objectMapper.createObjectNode();
        ArrayNode people = rootNode.putArray(MATCH_TAG_NAME);

        for (LookupSqlDto eachResult : results) {
            people.add(eachResult.asLookup(this.objectMapper));
        }

        return ResponseEntity.ok(rootNode);
    }
}
