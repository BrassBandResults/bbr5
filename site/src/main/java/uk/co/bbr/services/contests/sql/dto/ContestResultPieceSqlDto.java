package uk.co.bbr.services.contests.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;

import java.sql.Date;
import java.time.LocalDate;

@Getter
public class ContestResultPieceSqlDto extends AbstractSqlDto {
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

    public ContestResultPieceSqlDto(Object[] columnList) {
        Date tempEventDate = (Date)columnList[0];
        this.eventDate = tempEventDate.toLocalDate();
        this.dateResolution = (String)columnList[1];
        this.contestSlug = (String)columnList[2];
        this.bandCompetedAs = (String)columnList[3];
        this.bandSlug = (String)columnList[4];
        this.bandName = (String)columnList[5];
        this.pieceName = (String)columnList[6];
        this.pieceSlug = (String)columnList[7];
        this.pieceYear = (String)columnList[8];
        this.position = String.valueOf(columnList[9]);
        this.positionType = (String)columnList[10];
        this.regionName = (String)columnList[11];
        this.regionCountryCode = (String)columnList[12];
    }
}
