package uk.co.bbr.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;

import java.util.Optional;

@RestController
@RequestMapping("/api/bands")
@RequiredArgsConstructor
public class BandApiController {

    private final BandService bandService;

    @GetMapping("/{slug}")
    public Optional<BandDao> getBand(@PathVariable("slug") String slug) {
        return this.bandService.fetchBySlug(slug);
    }
}
