package uk.co.bbr.services.contests.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;

import java.math.BigInteger;
import java.sql.Date;
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
    private final BigInteger contestResultId;
    private final BigInteger contestEventId;
    private final String regionName;
    private final String regionCountryCode;
    private final String groupName;
    private final String groupSlug;

    public PersonConductingResultSqlDto(Object[] columnList) {
        this.eventDate = this.getLocalDate(columnList, 0);
        this.eventDateResolution = (String)columnList[1];
        this.contestSlug = (String)columnList[2];
        this.contestName = (String)columnList[3];
        this.bandCompetedAs = (String)columnList[4];
        this.bandName = (String)columnList[5];
        this.bandSlug = (String)columnList[6];
        this.resultPosition = (Integer)columnList[7];
        this.resultPositionType = (String)columnList[8];
        this.resultAward = (String)columnList[9];
        this.points = (String)columnList[10];
        this.draw = (Integer)columnList[11];
        this.contestResultId = (BigInteger)columnList[12];
        this.contestEventId = (BigInteger)columnList[13];
        this.regionName = (String)columnList[14];
        this.regionCountryCode = (String)columnList[15];
        this.groupName = (String)columnList[16];
        this.groupSlug = (String)columnList[17];
    }
}
