package uk.co.bbr.services.contests.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;

import java.time.LocalDate;

@Getter
public class PersonConductingResultSqlDto extends AbstractSqlDto {
    private final LocalDate eventDate;
    private final String eventDateResolution;
    private final String contestSlug;
    private final String contestName;
    private final String bandCompetedAs;
    private final String bandName;
    private final String bandSlug;
    private final Integer resultPosition;
    private final String resultPositionType;
    private final String resultAward;
    private final String points;
    private final Integer draw;
    private final Long contestResultId;
    private final Long contestEventId;
    private final String regionName;
    private final String regionCountryCode;
    private final String groupName;
    private final String groupSlug;
    private final String resultNotes;

    public PersonConductingResultSqlDto(Object[] columnList) {
        this.eventDate = this.getLocalDate(columnList, 0);
        this.eventDateResolution = this.getString(columnList, 1);
        this.contestSlug = this.getString(columnList, 2);
        this.contestName = this.getString(columnList, 3);
        this.bandCompetedAs = this.getString(columnList, 4);
        this.bandName = this.getString(columnList, 5);
        this.bandSlug = this.getString(columnList, 6);
        this.resultPosition = this.getInteger(columnList,7);
        this.resultPositionType = this.getString(columnList, 8);
        this.resultAward = this.getString(columnList, 9);
        this.points = this.getString(columnList, 10);
        this.draw = this.getInteger(columnList,11);
        this.contestResultId = this.getLong(columnList,12);
        this.contestEventId = this.getLong(columnList,13);
        this.regionName = this.getString(columnList, 14);
        this.regionCountryCode = this.getString(columnList, 15);
        this.groupName = this.getString(columnList, 16);
        this.groupSlug = this.getString(columnList, 17);
        this.resultNotes = this.getString(columnList, 18);
    }
}
