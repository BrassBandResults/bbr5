package uk.co.bbr.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;

import java.util.Optional;

@RestController
@RequestMapping("/api/regions")
@RequiredArgsConstructor
public class RegionApiController {

    private final RegionService regionService;

    @GetMapping("/{slug}")
    public Optional<RegionDao> getRegion(@PathVariable("slug") String slug) {
        return this.regionService.fetchBySlug(slug);
    }
}
