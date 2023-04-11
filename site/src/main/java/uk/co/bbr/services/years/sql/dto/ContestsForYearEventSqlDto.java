package uk.co.bbr.services.years.sql.dto;

import lombok.Getter;

import java.math.BigInteger;
import java.sql.Date;
import java.time.LocalDate;

@Getter
public class ContestsForYearEventSqlDto {
    private final LocalDate eventDate;
    private final String eventDateResolution;
    private final String contestSlug;
    private final String contestName;
    private final String bandCompetedAs;
    private final String bandSlug;
    private final String bandName;
    private final String bandRegionCountryCode;
    private final String resultPieceSlug;
    private final String resultPieceName;
    private final String setPieceSlug;
    private final String setPieceName;
    private final String conductor1Slug;
    private final String conductor1FirstNames;
    private final String conductor1Surname;
    private final String conductor2Slug;
    private final String conductor2FirstNames;
    private final String conductor2Surname;
    private final String conductor3Slug;
    private final String conductor3FirstNames;
    private final String conductor3Surname;
    private final Boolean noContest;

    public ContestsForYearEventSqlDto(Object[] columnList) {
        Date eventDate = (Date)columnList[0];
        this.eventDate = eventDate.toLocalDate();
        this.eventDateResolution = (String)columnList[1];
        this.contestSlug = (String)columnList[2];
        this.contestName = (String)columnList[3];
        this.noContest = (Boolean)columnList[4];
        this.bandCompetedAs = (String)columnList[5];
        this.bandSlug = (String)columnList[6];
        this.bandName = (String)columnList[7];
        this.bandRegionCountryCode = (String)columnList[8];
        this.resultPieceSlug = (String)columnList[9];
        this.resultPieceName = (String)columnList[10];
        this.setPieceSlug = (String)columnList[11];
        this.setPieceName = (String)columnList[12];
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
