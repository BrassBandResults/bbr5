package uk.co.bbr.web.venues;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class VenueMapController {

    @GetMapping("/venues/MAP")
    public String bandMap() {
        return "venues/map/map";
    }
}

