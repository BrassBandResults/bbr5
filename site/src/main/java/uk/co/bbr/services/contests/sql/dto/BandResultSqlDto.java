package uk.co.bbr.services.contests.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;

import java.math.BigInteger;
import java.sql.Date;
import java.time.LocalDate;

@Getter
public class BandResultSqlDto extends AbstractSqlDto {

    private final BigInteger contestResultId;
    private final LocalDate eventDate;
    private final String eventDateResolution;
    private final String contestSlug;
    private final String contestName;
    private final Integer resultPosition;
    private final String resultPositionType;
    private final String resultAward;
    private final String bandName;
    private final Integer draw;
    private final BigInteger contestEventId;
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
        this.contestResultId = (BigInteger)columnList[0];
        Date tempEventDate = (Date)columnList[1];
        this.eventDate = tempEventDate.toLocalDate();
        this.eventDateResolution = (String)columnList[2];
        this.contestSlug = (String)columnList[3];
        this.contestName = (String)columnList[4];
        this.resultPosition = (Integer)columnList[5];
        this.resultPositionType = (String)columnList[6];
        this.resultAward = (String)columnList[7];
        this.bandName = (String)columnList[8];
        this.draw = (Integer)columnList[9];
        this.contestEventId = (BigInteger)columnList[10];
        this.groupSlug = (String)columnList[11];
        this.groupName = (String)columnList[12];
        this.conductor1Slug = (String)columnList[13];
        this.conductor1FirstNames = (String)columnList[14];
        this.conductor1Surname = (String)columnList[15];
        this.conductor2Slug = (String)columnList[16];
        this.conductor2FirstNames = (String)columnList[17];
        this.conductor2Surname = (String)columnList[18];
        this.conductor3Slug = (String)columnList[19];
        this.conductor3FirstNames = (String)columnList[20];
        this.conductor3Surname = (String)columnList[21];
    }
}
