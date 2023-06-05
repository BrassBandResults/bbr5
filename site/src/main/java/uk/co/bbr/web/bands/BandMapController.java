package uk.co.bbr.web.bands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class BandMapController {

    @GetMapping("/bands/MAP")
    public String bandMap() {
        return "bands/map/map";
    }
}

