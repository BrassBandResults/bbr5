package uk.co.bbr.services.people.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dao.ContestEventDao;
import uk.co.bbr.services.contests.types.ContestEventDateResolution;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;

import java.sql.Date;
import java.time.LocalDate;

@Getter
public class CompareConductorsSqlDto extends AbstractSqlDto {

    private final Integer leftResult;
    private final String leftResultType;
    private final Integer rightResult;
    private final String rightResultType;
    private final LocalDate eventDate;
    private final String contestSlug;
    private final String contestName;
    private final String eventDateResolution;
    private final String leftBandName;
    private final String rightBandName;
    private final String leftBandSlug;
    private final String rightBandSlug;

    public CompareConductorsSqlDto(Object[] columnList) {
        this.leftResult = (Integer)columnList[0];
        this.leftResultType = (String)columnList[1];
        this.rightResult = (Integer)columnList[2];
        this.rightResultType = (String)columnList[3];
        Date tempEventDate = (Date)columnList[4];
        this.eventDate = tempEventDate.toLocalDate();
        this.contestSlug = (String)columnList[5];
        this.contestName = (String)columnList[6];
        this.eventDateResolution = (String)columnList[7];
        this.leftBandName = (String)columnList[8];
        this.rightBandName = (String)columnList[9];
        this.leftBandSlug = (String)columnList[10];
        this.rightBandSlug = (String)columnList[11];
    }

    public ContestEventDao getEvent() {
        ContestDao contest = new ContestDao();
        contest.setSlug(this.contestSlug);
        contest.setName(this.contestName);

        ContestEventDao event = new ContestEventDao();
        event.setEventDate(this.eventDate);
        event.setEventDateResolution(ContestEventDateResolution.fromCode(this.eventDateResolution));
        event.setContest(contest);

        return event;
    }
}
