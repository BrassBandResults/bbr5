package uk.co.bbr.web.home;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class BlogController {

    @GetMapping("/blog/{year:\\d{4}}/{month:\\d{2}}/{day:\\d{2}}/{topicSlug:[-a-z]+}")
    public String blogEntry(@PathVariable("year") String year, @PathVariable("month") String month, @PathVariable("day") String day, @PathVariable("topicSlug") String topicSlug) {
        String blogTemplate = year + "-" + month + "-" + day + "_" + topicSlug;

        return "blog/" + blogTemplate;
    }
}
