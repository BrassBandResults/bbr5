package uk.co.bbr.services.contests.sql.dto;

import lombok.Getter;

import java.sql.Date;
import java.time.LocalDate;

@Getter
public class ContestResultPieceSqlDto {
    private final LocalDate eventDate;
    private final String dateResolution;
    private final String contestSlug;
    private final String bandCompetedAs;
    private final String bandSlug;
    private final String bandName;
    private final String pieceName;
    private final String pieceSlug;
    private final String pieceYear;
    private final String position;
    private final String positionType;
    private final String regionName;
    private final String regionCountryCode;

    public ContestResultPieceSqlDto(Object[] eachRowData) {
        Date eventDate = (Date)eachRowData[0];
        this.eventDate = eventDate.toLocalDate();
        this.dateResolution = (String)eachRowData[1];
        this.contestSlug = (String)eachRowData[2];
        this.bandCompetedAs = (String)eachRowData[3];
        this.bandSlug = (String)eachRowData[4];
        this.bandName = (String)eachRowData[5];
        this.pieceName = (String)eachRowData[6];
        this.pieceSlug = (String)eachRowData[7];
        this.pieceYear = (String)eachRowData[8];
        this.position = "" + (Integer)eachRowData[9];
        this.positionType = (String)eachRowData[10];
        this.regionName = (String)eachRowData[11];
        this.regionCountryCode = (String)eachRowData[12];
    }
}
