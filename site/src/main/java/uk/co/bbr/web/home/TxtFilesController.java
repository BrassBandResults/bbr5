package uk.co.bbr.web.home;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class TxtFilesController {

    @GetMapping("/ads.txt")
    @ResponseBody
    public String adsTxt() {
        return "google.com, pub-5087204743199130, DIRECT, f08c47fec0942fa0";
    }
}

