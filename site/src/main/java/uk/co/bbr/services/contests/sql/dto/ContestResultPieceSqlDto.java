package uk.co.bbr.services.contests.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;

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
    private final Integer position;
    private final String positionType;
    private final String regionName;
    private final String regionCountryCode;

    public ContestResultPieceSqlDto(Object[] columnList) {
        this.eventDate = this.getLocalDate(columnList, 0);
        this.dateResolution = this.getString(columnList, 1);
        this.contestSlug = this.getString(columnList, 2);
        this.bandCompetedAs = this.getString(columnList, 3);
        this.bandSlug = this.getString(columnList, 4);
        this.bandName = this.getString(columnList, 5);
        this.pieceName = this.getString(columnList, 6);
        this.pieceSlug = this.getString(columnList, 7);
        this.pieceYear = this.getString(columnList, 8);
        this.position = this.getInteger(columnList, 9);
        this.positionType = this.getString(columnList, 10);
        this.regionName = this.getString(columnList, 11);
        this.regionCountryCode = this.getString(columnList, 12);
    }
}
