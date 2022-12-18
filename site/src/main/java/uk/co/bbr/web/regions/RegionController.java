package uk.co.bbr.web.regions;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final MessageSource messageSource;

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

        String ungradedDescription = this.messageSource.getMessage("section.ungraded", null, locale);
        List<LinkSectionDto> bandsBySection = this.regionService.fetchBandsBySection(region, ungradedDescription);

        model.addAttribute("Region", region);
        model.addAttribute("Sections", bandsBySection);
        return "regions/regionLinks";
    }
}
