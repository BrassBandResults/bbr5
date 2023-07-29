package uk.co.bbr.web.calendar.dto;

import lombok.Getter;
import lombok.Setter;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.types.ContestEventDateResolution;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class WeekDto {
    private final List<DayDto> days;

    public WeekDto() {
        this.days = new ArrayList<>();
        for (int i=0; i<7; i++) {
            days.add(new DayDto());
        }
    }

    public DayDto get(int position) {
        return this.days.get(position);
    }

    public void setFirstDayOfMonth(LocalDate firstOfMonth) {
        switch (firstOfMonth.getDayOfWeek()) {

            case MONDAY -> {
                LocalDate nextDay = firstOfMonth;
                this.days.get(0).setDay(nextDay);
                nextDay = nextDay.plus(1, ChronoUnit.DAYS);
                this.days.get(1).setDay(nextDay);
                nextDay = nextDay.plus(1, ChronoUnit.DAYS);
                this.days.get(2).setDay(nextDay);
                nextDay = nextDay.plus(1, ChronoUnit.DAYS);
                this.days.get(3).setDay(nextDay);
                nextDay = nextDay.plus(1, ChronoUnit.DAYS);
                this.days.get(4).setDay(nextDay);
                nextDay = nextDay.plus(1, ChronoUnit.DAYS);
                this.days.get(5).setDay(nextDay);
                nextDay = nextDay.plus(1, ChronoUnit.DAYS);
                this.days.get(6).setDay(nextDay);
            }
            case TUESDAY -> {
                LocalDate nextDay = firstOfMonth;
                this.days.get(1).setDay(nextDay);
                nextDay = nextDay.plus(1, ChronoUnit.DAYS);
                this.days.get(2).setDay(nextDay);
                nextDay = nextDay.plus(1, ChronoUnit.DAYS);
                this.days.get(3).setDay(nextDay);
                nextDay = nextDay.plus(1, ChronoUnit.DAYS);
                this.days.get(4).setDay(nextDay);
                nextDay = nextDay.plus(1, ChronoUnit.DAYS);
                this.days.get(5).setDay(nextDay);
                nextDay = nextDay.plus(1, ChronoUnit.DAYS);
                this.days.get(6).setDay(nextDay);
            }
            case WEDNESDAY -> {
                LocalDate nextDay = firstOfMonth;
                this.days.get(2).setDay(nextDay);
                nextDay = nextDay.plus(1, ChronoUnit.DAYS);
                this.days.get(3).setDay(nextDay);
                nextDay = nextDay.plus(1, ChronoUnit.DAYS);
                this.days.get(4).setDay(nextDay);
                nextDay = nextDay.plus(1, ChronoUnit.DAYS);
                this.days.get(5).setDay(nextDay);
                nextDay = nextDay.plus(1, ChronoUnit.DAYS);
                this.days.get(6).setDay(nextDay);
            }
            case THURSDAY -> {
                LocalDate nextDay = firstOfMonth;
                this.days.get(3).setDay(nextDay);
                nextDay = nextDay.plus(1, ChronoUnit.DAYS);
                this.days.get(4).setDay(nextDay);
                nextDay = nextDay.plus(1, ChronoUnit.DAYS);
                this.days.get(5).setDay(nextDay);
                nextDay = nextDay.plus(1, ChronoUnit.DAYS);
                this.days.get(6).setDay(nextDay);
            }
            case FRIDAY -> {
                LocalDate nextDay = firstOfMonth;
                this.days.get(4).setDay(nextDay);
                nextDay = nextDay.plus(1, ChronoUnit.DAYS);
                this.days.get(5).setDay(nextDay);
                nextDay = nextDay.plus(1, ChronoUnit.DAYS);
                this.days.get(6).setDay(nextDay);
            }
            case SATURDAY -> {
                LocalDate nextDay = firstOfMonth;
                this.days.get(5).setDay(nextDay);
                nextDay = nextDay.plus(1, ChronoUnit.DAYS);
                this.days.get(6).setDay(nextDay);
            }
            case SUNDAY -> {
                LocalDate nextDay = firstOfMonth;
                this.days.get(6).setDay(nextDay);
            }
        }
    }

    public void setMonday(LocalDate dayOfMonth) {
        LocalDate nextDay = dayOfMonth;
        this.days.get(0).setDay(nextDay);
        nextDay = nextDay.plus(1, ChronoUnit.DAYS);
        if (nextDay.getMonthValue() == dayOfMonth.getMonthValue()) {
            this.days.get(1).setDay(nextDay);
        }
        nextDay = nextDay.plus(1, ChronoUnit.DAYS);
        if (nextDay.getMonthValue() == dayOfMonth.getMonthValue()) {
            this.days.get(2).setDay(nextDay);
        }
        nextDay = nextDay.plus(1, ChronoUnit.DAYS);
        if (nextDay.getMonthValue() == dayOfMonth.getMonthValue()) {
            this.days.get(3).setDay(nextDay);
        }
        nextDay = nextDay.plus(1, ChronoUnit.DAYS);
        if (nextDay.getMonthValue() == dayOfMonth.getMonthValue()) {
            this.days.get(4).setDay(nextDay);
        }
        nextDay = nextDay.plus(1, ChronoUnit.DAYS);
        if (nextDay.getMonthValue() == dayOfMonth.getMonthValue()) {
            this.days.get(5).setDay(nextDay);
        }
        nextDay = nextDay.plus(1, ChronoUnit.DAYS);
        if (nextDay.getMonthValue() == dayOfMonth.getMonthValue()) {
            this.days.get(6).setDay(nextDay);
        }
    }

    public LocalDate getWeekStartDate() {
        for (DayDto day: this.days) {
            if (day.getDay() != null) {
                return day.getDay();
            }
        }
        throw new UnsupportedOperationException("Ran out of days");
    }

    public LocalDate getWeekEndDate() {
        LocalDate lastDay = null;
        for (DayDto day: this.days) {
            if (day.getDay() != null) {
                lastDay = day.getDay();
            }
        }
        return lastDay;
    }


    public boolean contains(ContestEventDao contestEvent) {
        if (contestEvent.getEventDateResolution().equals(ContestEventDateResolution.EXACT_DATE)) {
            LocalDate eventDate = contestEvent.getEventDate();
            LocalDate dayBeforeWeek = this.getWeekStartDate().minus(1, ChronoUnit.DAYS);
            LocalDate dayAfterWeek = this.getWeekEndDate().plus(1, ChronoUnit.DAYS);

            return eventDate.isAfter(dayBeforeWeek) && eventDate.isBefore(dayAfterWeek);
        }
        return false;
    }

    public void assignEvent(ContestEventDao contestEvent) {
        for (DayDto day : this.days) {
            if (contestEvent.getEventDate().equals(day.getDay())) {
                day.addEvent(contestEvent);
            }
        }
    }
}
