package uk.co.bbr.web.years;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.years.YearService;
import uk.co.bbr.services.years.sql.dto.YearListEntrySqlDto;
import uk.co.bbr.web.security.annotations.IsBbrMember;
import uk.co.bbr.web.security.annotations.IsBbrPro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class YearController {

    private final YearService yearService;

    @IsBbrMember
    @GetMapping("/years")
    public String contestYearHome(Model model){
        List<YearListEntrySqlDto> allYears = this.yearService.fetchFullYearList();

        List<YearListEntrySqlDto> reverseYears = new ArrayList<>(allYears);
        Collections.reverse(reverseYears);

        model.addAttribute("Years", allYears);
        model.addAttribute("ReverseYears", reverseYears);

        return "years/home";
    }

    @IsBbrPro
    @GetMapping("/years/{year:\\d{4}}")
    public String contestSingleYear(Model model, @PathVariable String year){
        List<ContestResultDao> yearEvents = this.yearService.fetchEventsForYear(year);

        model.addAttribute("Year", year);
        model.addAttribute("YearContests", yearEvents);

        return "years/year";
    }

}
