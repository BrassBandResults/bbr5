package uk.co.bbr.web.regions;

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
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.sql.dto.BandWinnersSqlDto;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.regions.dto.RegionPageDto;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.dao.SiteUserDao;
import uk.co.bbr.web.security.annotations.IsBbrPro;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class RegionController {

    private final RegionService regionService;
    private final BandService bandService;
    private final SecurityService securityService;
    private final ObjectMapper objectMapper;

    @GetMapping("/regions")
    public String regionList(Model model) {
        List<RegionDao> regions = this.regionService.findAllWithCounts();

        model.addAttribute("Regions", regions);
        return "regions/regions";
    }

    @GetMapping("/regions/{regionSlug}")
    public String region(Model model, @PathVariable("regionSlug") String regionSlug) {
        RegionPageDto region = this.regionService.fetchBySlugForPage(regionSlug);
        List<RegionDao> subRegions = this.regionService.findSubRegions(region.getRegion());

        model.addAttribute("Region", region);
        model.addAttribute("SubRegions", subRegions);

        SiteUserDao currentUser = this.securityService.getCurrentUser();

        if (currentUser != null && currentUser.isProUser()) {
            return "regions/region-pro";
        }

        return "regions/region";
    }

    @GetMapping("/regions/{regionSlug}/contests")
    public String regionContests(Model model, @PathVariable("regionSlug") String regionSlug) {
        RegionPageDto region = this.regionService.fetchBySlugForPage(regionSlug);

        List<ContestDao> contests = this.regionService.findContestsForRegion(region.getRegion());

        model.addAttribute("Region", region);
        model.addAttribute("Contests", contests);
        return "regions/region-contests";
    }

    @IsBbrPro
    @GetMapping("/regions/{regionSlug}/winners")
    public String regionWinners(Model model, @PathVariable("regionSlug") String regionSlug) {
        RegionPageDto region = this.regionService.fetchBySlugForPage(regionSlug);

        List<BandWinnersSqlDto> bandWinners = this.bandService.fetchContestWinningBands();
        List<BandWinnersSqlDto> winningBandsForRegion = new ArrayList<>();
        for (BandWinnersSqlDto eachWinner : bandWinners) {
            if (eachWinner.getRegionSlug().equals(region.getSlug())) {
                winningBandsForRegion.add(eachWinner);
            }
        }

        model.addAttribute("Region", region);
        model.addAttribute("WinningBands", winningBandsForRegion);
        return "regions/region-winners";
    }

    @GetMapping(value="/regions/{regionSlug}/{sectionType}/bands.json", produces="application/json")
    public ResponseEntity<JsonNode> regionBandsJson(@PathVariable("regionSlug") String regionSlug, @PathVariable("sectionType") String sectionType) {
        Optional<RegionDao> region = this.regionService.fetchBySlug(regionSlug);
        if (region.isEmpty()) {
            throw NotFoundException.regionNotFoundBySlug(regionSlug);
        }

        List<BandDao> bandsForMap = this.bandService.findBandsWithMapLocationAndRehearsals(region.get()).stream().filter(t -> t.getSectionType().equals(sectionType)).toList();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type", "FeatureCollection");
        ArrayNode features = objectNode.putArray("features");

        for (BandDao eachBand : bandsForMap) {
            features.add(eachBand.asGeoJson(this.objectMapper));
        }

        return ResponseEntity.ok(objectNode);
    }
}
