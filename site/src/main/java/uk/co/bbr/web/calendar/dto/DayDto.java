package uk.co.bbr.web.calendar.dto;

import lombok.Getter;
import lombok.Setter;
import uk.co.bbr.services.events.dao.ContestEventDao;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DayDto {
    private LocalDate day = null;
    private List<ContestEventDao> events = new ArrayList<>();

    public Integer getDayNumber() {
        if (this.day == null) {
            return null;
        }
        return this.day.getDayOfMonth();
    }

    public boolean isToday() {
        return this.day != null && this.day.equals(LocalDate.now());
    }

    public void addEvent(ContestEventDao contestEvent) {
        this.events.add(contestEvent);
    }
}
