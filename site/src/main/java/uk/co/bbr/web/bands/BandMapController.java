package uk.co.bbr.web.bands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.bands.BandRehearsalsService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.dao.BandRehearsalDayDao;
import uk.co.bbr.services.bands.types.RehearsalDay;
import uk.co.bbr.services.framework.NotFoundException;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class BandMapController {

    private final BandRehearsalsService bandRehearsalsService;
    private final ObjectMapper objectMapper;

    @GetMapping("/bands/MAP")
    public String bandMap() {
        return "bands/map/map";
    }

    @GetMapping(value="/bands/MAP/for-day/{dayCode:[a-z]{3}}/bands.json", produces="application/json")
    public ResponseEntity<JsonNode> bandsByRehearsalDayMapJson(@PathVariable("dayCode") String dayCode) {
        RehearsalDay day = switch (dayCode) {
            case "mon" -> RehearsalDay.MONDAY;
            case "tue" -> RehearsalDay.TUESDAY;
            case "wed" -> RehearsalDay.WEDNESDAY;
            case "thu" -> RehearsalDay.THURSDAY;
            case "fri" -> RehearsalDay.FRIDAY;
            case "sat" -> RehearsalDay.SATURDAY;
            case "sun" -> RehearsalDay.SUNDAY;
            default -> throw new NotFoundException("Day not found");
        };
        List<BandRehearsalDayDao> bandRehearsalDays = this.bandRehearsalsService.fetchBandsByDayForMap(day);

        List<BandDao> bandsForMap = new ArrayList<>();
        for (BandRehearsalDayDao eachRehearsalDay : bandRehearsalDays) {
            bandsForMap.add(eachRehearsalDay.getBand());
        }

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type", "FeatureCollection");
        ArrayNode features = objectNode.putArray("features");

        for (BandDao eachBand : bandsForMap) {
            features.add(eachBand.asGeoJson(this.objectMapper));
        }

        return ResponseEntity.ok(objectNode);
    }
}


