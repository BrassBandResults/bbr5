package uk.co.bbr.services.contests.sql.dto;

import lombok.Getter;

import java.math.BigInteger;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Getter
public class BandResultSqlDto {

    private BigInteger contestResultId;
    private LocalDate eventDate;
    private String eventDateResolution;
    private String contestSlug;
    private String contestName;
    private Integer resultPosition;
    private String resultPositionType;
    private String resultAward;
    private String bandName;
    private Integer draw;
    private BigInteger contestEventId;
    private String conductor1Slug;
    private String conductor1FirstNames;
    private String conductor1Surname;
    private String conductor2Slug;
    private String conductor2FirstNames;
    private String conductor2Surname;
    private String conductor3Slug;
    private String conductor3FirstNames;
    private String conductor3Surname;

    public BandResultSqlDto(Object[] columnList) {
        this.contestResultId = (BigInteger)columnList[0];
        Date eventDate = (Date)columnList[1];
        this.eventDate = eventDate.toLocalDate();
        this.eventDateResolution = (String)columnList[2];
        this.contestSlug = (String)columnList[3];
        this.contestName = (String)columnList[4];
        this.resultPosition = (Integer)columnList[5];
        this.resultPositionType = (String)columnList[6];
        this.resultAward = (String)columnList[7];
        this.bandName = (String)columnList[8];
        this.draw = (Integer)columnList[9];
        this.contestEventId = (BigInteger)columnList[10];
        this.conductor1Slug = (String)columnList[11];
        this.conductor1FirstNames = (String)columnList[12];
        this.conductor1Surname = (String)columnList[13];
        this.conductor2Slug = (String)columnList[14];
        this.conductor2FirstNames = (String)columnList[15];
        this.conductor2Surname = (String)columnList[16];
        this.conductor3Slug = (String)columnList[17];
        this.conductor3FirstNames = (String)columnList[18];
        this.conductor3Surname = (String)columnList[19];
    }
}
