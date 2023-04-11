package uk.co.bbr.web.years;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.contests.dao.ContestEventDao;
import uk.co.bbr.services.contests.dao.ContestResultDao;
import uk.co.bbr.services.years.YearService;
import uk.co.bbr.services.years.sql.dto.ContestsForYearSqlDto;
import uk.co.bbr.services.years.sql.dto.YearListEntrySqlDto;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class YearController {

    private final YearService yearService;

    @GetMapping("/years")
    public String contestYearHome(Model model){
        List<YearListEntrySqlDto> allYears = this.yearService.fetchFullYearList();

        model.addAttribute("Years", allYears);

        return "years/home";
    }

    @GetMapping("/years/{year:\\d{4}}")
    public String contestSingleYear(Model model, @PathVariable String year){
        List<ContestResultDao> yearEvents = this.yearService.fetchEventsForYear(year);

        model.addAttribute("Year", year);
        model.addAttribute("YearContests", yearEvents);

        return "years/year";
    }

}
