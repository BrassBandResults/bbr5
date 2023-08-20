package uk.co.bbr.web.calendar;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.events.ContestEventService;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.web.calendar.dto.WeekDto;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CalendarController {

    private final ContestEventService contestEventService;

    @GetMapping("/calendar")
    public String calendarHome() {
        LocalDate today = LocalDate.now();

        return "redirect:/calendar/" + today.getYear() + "/" + today.getMonthValue();
    }

    @GetMapping("/calendar/{year:\\d{4}}/{month:\\d{1,2}}")
    public String calendarForMonth(Model model, @PathVariable("year") int year, @PathVariable("month") int month) {
        if (year < 1800  || year > 2100) {
            throw NotFoundException.yearOutsideRange(year);
        }

        LocalDate displayMonth = LocalDate.of(year, month, 1);

        LocalDate oneYearAgo = null;
        LocalDate oneYearAhead = null;
        LocalDate oneMonthAgo = null;
        LocalDate oneMonthAhead = null;
        if (year > 1800 && year < 2100) {
            oneYearAgo = displayMonth.minus(1, ChronoUnit.YEARS);
            oneYearAhead = displayMonth.plus(1, ChronoUnit.YEARS);
            oneMonthAgo = displayMonth.minus(1, ChronoUnit.MONTHS);
            oneMonthAhead = displayMonth.plus(1, ChronoUnit.MONTHS);
        }

        List<WeekDto> weekData = this.buildWeekData(displayMonth);
        List<ContestResultDao> eventsForThisMonth = this.contestEventService.fetchEventsForMonth(displayMonth);
        for (ContestResultDao event : eventsForThisMonth) {
            for (WeekDto eachWeek : weekData) {
                if (eachWeek.contains(event.getContestEvent())) {
                    eachWeek.assignEvent(event.getContestEvent());
                }
            }
        }

        model.addAttribute("ActualMonth", LocalDate.now());
        model.addAttribute("DisplayMonth", displayMonth);
        model.addAttribute("PreviousYear", oneYearAgo);
        model.addAttribute("NextYear", oneYearAhead);
        model.addAttribute("PreviousMonth", oneMonthAgo);
        model.addAttribute("NextMonth", oneMonthAhead);
        model.addAttribute("Weeks", weekData);

        return "calendar/month";
    }

    private List<WeekDto> buildWeekData(LocalDate displayMonth) {
        List<WeekDto> returnData = new ArrayList<>();

        returnData.add(new WeekDto());
        returnData.get(0).setFirstDayOfMonth(displayMonth);

        LocalDate nextMonday = displayMonth.plus(1, ChronoUnit.DAYS);
        while (nextMonday.getDayOfWeek() != DayOfWeek.MONDAY) {
            nextMonday = nextMonday.plus(1, ChronoUnit.DAYS);
        }
        returnData.add(new WeekDto());
        returnData.get(1).setMonday(nextMonday);

        nextMonday = nextMonday.plus(1, ChronoUnit.DAYS);
        while (nextMonday.getDayOfWeek() != DayOfWeek.MONDAY) {
            nextMonday = nextMonday.plus(1, ChronoUnit.DAYS);
        }
        returnData.add(new WeekDto());
        returnData.get(2).setMonday(nextMonday);

        nextMonday = nextMonday.plus(1, ChronoUnit.DAYS);
        while (nextMonday.getDayOfWeek() != DayOfWeek.MONDAY) {
            nextMonday = nextMonday.plus(1, ChronoUnit.DAYS);
        }
        returnData.add(new WeekDto());
        returnData.get(3).setMonday(nextMonday);

        // we're getting into the territory of not needing to add more weeks - shortest month is four weeks, if that started on a Monday, we're done.
        nextMonday = nextMonday.plus(1, ChronoUnit.DAYS);
        while (nextMonday.getDayOfWeek() != DayOfWeek.MONDAY) {
            nextMonday = nextMonday.plus(1, ChronoUnit.DAYS);
        }

        if (nextMonday.getMonthValue() == displayMonth.getMonthValue()) {
            returnData.add(new WeekDto());
            returnData.get(4).setMonday(nextMonday);
        }

        nextMonday = nextMonday.plus(1, ChronoUnit.DAYS);
        while (nextMonday.getDayOfWeek() != DayOfWeek.MONDAY) {
            nextMonday = nextMonday.plus(1, ChronoUnit.DAYS);
        }

        if (nextMonday.getMonthValue() == displayMonth.getMonthValue()) {
            returnData.add(new WeekDto());
            returnData.get(5).setMonday(nextMonday);
        }




        return returnData;
    }


}


