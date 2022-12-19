package uk.co.bbr.web.regions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;
import uk.co.bbr.services.band.dao.BandDao;
import uk.co.bbr.services.region.RegionService;
import uk.co.bbr.services.region.dao.RegionDao;
import uk.co.bbr.services.region.dto.LinkSectionDto;
import uk.co.bbr.services.region.dto.RegionPageDto;

import java.util.List;
import java.util.Locale;

@Controller
@RequiredArgsConstructor
public class RegionController {

    private final RegionService regionService;
    private final ObjectMapper objectMapper;

    @GetMapping("/regions")
    public String regionList(Model model) {
        List<RegionDao> regions = this.regionService.fetchAll();

        model.addAttribute("Regions", regions);
        return "regions/regions";
    }

    @GetMapping("/regions/{regionSlug}")
    public String region(Model model, @PathVariable("regionSlug") String regionSlug) {
        RegionPageDto region = this.regionService.findBySlugForPage(regionSlug);
        List<RegionDao> subRegions = this.regionService.fetchSubRegions(region.getRegion());

        model.addAttribute("Region", region);
        model.addAttribute("SubRegions", subRegions);
        return "regions/region";
    }

    @GetMapping("/regions/{regionSlug}/links")
    public String regionLinks(Model model, Locale locale, @PathVariable("regionSlug") String regionSlug) {
        RegionDao region = this.regionService.findBySlug(regionSlug);

        List<LinkSectionDto> bandsBySection = this.regionService.fetchBandsBySection(region, "section.ungraded");

        model.addAttribute("Region", region);
        model.addAttribute("Sections", bandsBySection);
        return "regions/regionLinks";
    }

    @GetMapping(value="/regions/{regionSlug}/bands.json", produces="application/json")
    public ResponseEntity<JsonNode> regionBandsJson(@PathVariable("regionSlug") String regionSlug) {
        RegionDao region = this.regionService.findBySlug(regionSlug);

        List<BandDao> bandsForMap = this.regionService.fetchBandsWithMapLocation(region);

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type", "FeatureCollection");
        ArrayNode features = objectNode.putArray("features");

        for (BandDao eachBand : bandsForMap) {
            ObjectNode bandGeometry = objectMapper.createObjectNode();
            bandGeometry.put("type", "Point");
            bandGeometry.putArray("coordinates").add(Float.parseFloat(eachBand.getLongitude())).add(Float.parseFloat(eachBand.getLatitude()));

            ObjectNode bandProperties = objectMapper.createObjectNode();
            bandProperties.put("name", eachBand.getName());
            bandProperties.put("slug", eachBand.getSlug());
            bandProperties.put("type", eachBand.getSectionType());

            ObjectNode bandNode = objectMapper.createObjectNode();
            bandNode.put("type", "Feature");
            bandNode.put("geometry", bandGeometry);
            bandNode.put("properties", bandProperties);
            features.add(bandNode);
        }

        return ResponseEntity.ok(objectNode);

    }
}
