package uk.co.bbr.services.pieces.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;

import java.math.BigInteger;
import java.sql.Date;
import java.time.LocalDate;

@Getter
public class SetTestUsagePieceSqlDto extends AbstractSqlDto {
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
    private final BigInteger contestResultId;
    private final BigInteger contestEventId;
    private final String bandCountryCode;
    private final String bandRegionName;

    public SetTestUsagePieceSqlDto(Object[] columnList) {
        Date tempEventDate = (Date)columnList[0];
        this.eventDate = tempEventDate.toLocalDate();
        this.eventDateResolution = (String)columnList[1];
        this.contestSlug = (String)columnList[2];
        this.contestName = (String)columnList[3];
        this.bandCompetedAs = (String)columnList[4];
        this.bandName = (String)columnList[5];
        this.bandSlug = (String)columnList[6];
        this.resultPosition = (Integer)columnList[7];
        this.resultPositionType = (String)columnList[8];
        this.resultAward = (String)columnList[9];
        this.contestResultId = (BigInteger)columnList[10];
        this.contestEventId = (BigInteger)columnList[11];
        this.bandCountryCode = (String)columnList[12];
        this.bandRegionName = (String)columnList[13];
    }
}
