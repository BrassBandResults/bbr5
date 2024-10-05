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
import org.springframework.web.bind.annotation.RequestParam;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.map.LocationService;
import uk.co.bbr.services.map.dto.Location;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class BandMapController {

    private final BandService bandService;
    private final LocationService locationService;
    private final ObjectMapper objectMapper;

    @IsBbrMember
    @GetMapping("/bands/MAP")
    public String bandMap() {
        return "bands/map-by-rehearsal";
    }

    @IsBbrMember
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

    @IsBbrMember
    @GetMapping("/bands/{bandSlug:[\\-_a-z\\d]{2,}}/map/nearby.json")
    public ResponseEntity<JsonNode> nearbyLocationsOnMap(@PathVariable("bandSlug") String bandSlug, @RequestParam(value= "distance", required=false) Integer distance) {
        Optional<BandDao> band = this.bandService.fetchBySlug(bandSlug);
        if (band.isEmpty()) {
            throw NotFoundException.bandNotFoundBySlug(bandSlug);
        }

        Integer distanceKm = distance;
        if (distanceKm == null) {
            distanceKm = 25; // km
        }

        List<Location> locationsForMap = this.locationService.fetchLocationsNear(band.get().getLatitude(), band.get().getLongitude(), distanceKm);

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type", "FeatureCollection");
        ArrayNode features = objectNode.putArray("features");

        for (Location eachLocation : locationsForMap) {
            features.add(eachLocation.asGeoJson(this.objectMapper));
        }

        return ResponseEntity.ok(objectNode);
    }
}


