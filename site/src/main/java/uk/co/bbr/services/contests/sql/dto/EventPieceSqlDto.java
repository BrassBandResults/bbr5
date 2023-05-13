package uk.co.bbr.services.contests.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;

import java.math.BigInteger;

@Getter
public class EventPieceSqlDto extends AbstractSqlDto {

    private final BigInteger contestEventId;
    private final String pieceSlug;
    private final String pieceName;
    private final String pieceYear;


    public EventPieceSqlDto(Object[] eachRowData) {
        this.contestEventId = (BigInteger)eachRowData[0];
        this.pieceSlug = (String)eachRowData[1];
        this.pieceName = (String)eachRowData[2];
        this.pieceYear = (String)eachRowData[3];
    }
}
