package uk.co.bbr.web.home;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.events.ContestEventService;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.security.UserService;
import uk.co.bbr.services.security.dao.SiteUserDao;
import uk.co.bbr.services.statistics.StatisticsService;
import uk.co.bbr.services.statistics.dto.StatisticsDto;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BlogController {

    @GetMapping("/blog/{year:\\d{4}}/{month:\\d{2}}/{day:\\d{2}}/{topicSlug:[-a-z]+}")
    public String blogEntry(@PathVariable("year") String year, @PathVariable("month") String month, @PathVariable("day") String day, @PathVariable("topicSlug") String topicSlug) {
        String blogTemplate = year + "-" + month + "-" + day + "_" + topicSlug;

        return "blog/" + blogTemplate;
    }
}
