package uk.co.bbr.services.contests.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;

import java.math.BigInteger;
import java.sql.Date;
import java.time.LocalDate;

@Getter
public class BandResultSqlDto extends AbstractSqlDto {

    private final Long contestResultId;
    private final LocalDate eventDate;
    private final String eventDateResolution;
    private final String contestSlug;
    private final String contestName;
    private final Integer resultPosition;
    private final String resultPositionType;
    private final String resultAward;
    private final String bandName;
    private final Integer draw;
    private final Long contestEventId;
    private final String groupSlug;
    private final String groupName;
    private final String conductor1Slug;
    private final String conductor1FirstNames;
    private final String conductor1Surname;
    private final String conductor2Slug;
    private final String conductor2FirstNames;
    private final String conductor2Surname;
    private final String conductor3Slug;
    private final String conductor3FirstNames;
    private final String conductor3Surname;

    public BandResultSqlDto(Object[] columnList) {
        this.contestResultId = this.getLong(columnList,0);
        this.eventDate = this.getLocalDate(columnList, 1);
        this.eventDateResolution = this.getString(columnList, 2);
        this.contestSlug = this.getString(columnList, 3);
        this.contestName = this.getString(columnList, 4);
        this.resultPosition =this.getInteger(columnList,5);
        this.resultPositionType = this.getString(columnList, 6);
        this.resultAward = this.getString(columnList, 7);
        this.bandName = this.getString(columnList, 8);
        this.draw = this.getInteger(columnList,9);
        this.contestEventId = this.getLong(columnList,10);
        this.groupSlug = this.getString(columnList, 11);
        this.groupName = this.getString(columnList, 12);
        this.conductor1Slug = this.getString(columnList, 13);
        this.conductor1FirstNames = this.getString(columnList, 14);
        this.conductor1Surname = this.getString(columnList, 15);
        this.conductor2Slug = this.getString(columnList, 16);
        this.conductor2FirstNames = this.getString(columnList, 17);
        this.conductor2Surname = this.getString(columnList, 18);
        this.conductor3Slug = this.getString(columnList, 19);
        this.conductor3FirstNames = this.getString(columnList, 20);
        this.conductor3Surname = this.getString(columnList, 21);
    }
}
