package uk.co.bbr.web.bands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class BandMapController {

    private final BandService bandService;
    private final ObjectMapper objectMapper;

    @GetMapping("/bands/MAP")
    public String bandMap() {
        return "/bands/map/map-by-rehearsal";
    }

    @GetMapping(value="/bands/MAP/for-day/bands.json", produces="application/json")
    public ResponseEntity<JsonNode> bandsByRehearsalDayMapJson() {

        List<BandDao> bandsForMap = new ArrayList<>(this.bandService.findBandsWithMapLocationAndRehearsals());

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type", "FeatureCollection");
        ArrayNode features = objectNode.putArray("features");

        for (BandDao eachBand : bandsForMap) {
            features.add(eachBand.asGeoJson(this.objectMapper));
        }

        return ResponseEntity.ok(objectNode);
    }
}


