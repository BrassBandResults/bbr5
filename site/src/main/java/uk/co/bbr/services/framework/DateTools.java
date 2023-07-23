package uk.co.bbr.services.framework;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

@UtilityClass
public class DateTools {

    public static LocalDate today() {
        return LocalDate.now();
    }

    public static LocalDate previousSundayDate() {
        LocalDate date = DateTools.today();
        if (date.get(ChronoField.DAY_OF_WEEK) == 7) {
            return null;
        }
        if (date.get(ChronoField.DAY_OF_WEEK) == 6) {
            return null;
        }
        while (date.get(ChronoField.DAY_OF_WEEK) != 7) {
            date = date.minus(1, ChronoUnit.DAYS);
        }
        return date;
    }

    public static LocalDate thisWeekendSundayDate() {
        LocalDate date = DateTools.today();
        if (date.get(ChronoField.DAY_OF_WEEK) == 7) {
            return date;
        }
        if (date.get(ChronoField.DAY_OF_WEEK) == 6) {
            return date.plus(1, ChronoUnit.DAYS);
        }
        if (date.get(ChronoField.DAY_OF_WEEK) == 5) {
            return date.plus(2, ChronoUnit.DAYS);
        }
        return null;
    }

    public static LocalDate nextSundayDate() {
        LocalDate date = DateTools.today();
        if (date.get(ChronoField.DAY_OF_WEEK) >= 5) {
            // friday, we need to get the weekend after
            date = date.plus(3, ChronoUnit.DAYS);
        }
        while (date.get(ChronoField.DAY_OF_WEEK) != 7) {
            date = date.plus(1, ChronoUnit.DAYS);
        }
        return date;
    }
}
