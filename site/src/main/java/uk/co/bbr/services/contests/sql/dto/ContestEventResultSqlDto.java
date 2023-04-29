package uk.co.bbr.services.contests.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;

import java.sql.Date;
import java.time.LocalDate;

@Getter
public class ContestEventResultSqlDto extends AbstractSqlDto {
    private final LocalDate eventDate;
    private final String eventDateResolution;
    private final String contestSlug;
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


    public ContestEventResultSqlDto(Object[] columnList) {
        Date eventDate = (Date)columnList[0];
        this.eventDate = eventDate.toLocalDate();
        this.eventDateResolution = (String)columnList[1];
        this.contestSlug = (String)columnList[2];
        this.bandCompetedAs = (String)columnList[3];
        this.bandSlug = (String)columnList[4];
        this.bandName = (String)columnList[5];
        this.bandRegionCountryCode = (String)columnList[6];
        this.resultPieceSlug = (String)columnList[7];
        this.resultPieceName = (String)columnList[8];
        this.setPieceSlug = (String)columnList[9];
        this.setPieceName = (String)columnList[10];
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
