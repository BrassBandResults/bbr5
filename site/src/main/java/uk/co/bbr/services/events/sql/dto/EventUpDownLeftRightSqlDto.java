package uk.co.bbr.services.events.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;

import java.sql.Date;
import java.time.LocalDate;

@Getter
public class EventUpDownLeftRightSqlDto  extends AbstractSqlDto {

    private final String contestSlug;
    private final String contestName;
    private final LocalDate contestDate;

    public EventUpDownLeftRightSqlDto(Object[] columnList) {
        this.contestSlug = (String) columnList[0];
        this.contestName = (String) columnList[1];
        this.contestDate = this.getLocalDate(columnList, 2);
    }

    public ContestEventDao getEvent() {
        ContestEventDao event = new ContestEventDao();
        event.setEventDate(this.contestDate);
        event.setContest(new ContestDao());
        event.getContest().setSlug(this.contestSlug);
        event.getContest().setName(this.contestName);
        return event;
    }
}
