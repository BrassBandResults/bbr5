package uk.co.bbr.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.venues.VenueService;
import uk.co.bbr.services.venues.dao.VenueDao;

import java.util.Optional;

@RestController
@RequestMapping("/api/contests")
@RequiredArgsConstructor
public class ContestApiController {

    private final ContestService contestService;

    @GetMapping("/{slug}")
    public Optional<ContestDao> getContest(@PathVariable("slug") String slug) {
        return this.contestService.fetchBySlug(slug);
    }
}
