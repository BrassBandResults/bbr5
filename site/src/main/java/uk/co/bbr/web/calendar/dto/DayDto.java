package uk.co.bbr.web.calendar.dto;

import lombok.Getter;
import lombok.Setter;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.groups.dao.ContestGroupDao;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DayDto {
    private LocalDate day = null;
    private List<ContestEventDao> events = new ArrayList<>();
    private List<ContestGroupDao> groups = new ArrayList<>();

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

    public void addGroup(ContestGroupDao contestGroup) {
        for (ContestGroupDao group : this.groups) {
            if (group.getSlug().equals(contestGroup.getSlug())) {
                // already added
                return;
            }
        }
        this.groups.add(contestGroup);
    }

    public boolean hasEvents() {
        return this.events.size() > 0 || this.groups.size() > 0;
    }
}
