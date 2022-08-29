package uk.co.bbr.web.regions;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.region.RegionService;
import uk.co.bbr.services.region.dto.RegionPageDto;
import uk.co.bbr.services.region.dto.RegionListDto;


import java.util.List;

@Controller
@RequiredArgsConstructor
public class RegionController {

    private final RegionService regionService;

    @GetMapping("/regions")
    public String regionList(Model model) {
        List<RegionListDto> regions = this.regionService.fetchRegionsForListPage();

        model.addAttribute("Regions", regions);
        return "regions/regions";
    }

    @GetMapping("/regions/{regionSlug}")
    public String regionList(Model model, @PathVariable("regionSlug") String regionSlug) {
        RegionPageDto region = this.regionService.findBySlugForPage(regionSlug);

        model.addAttribute("Region", region);
        return "regions/region";
    }
}
