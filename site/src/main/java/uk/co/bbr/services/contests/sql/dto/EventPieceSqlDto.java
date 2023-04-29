package uk.co.bbr.services.contests.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;

import java.math.BigInteger;

@Getter
public class EventPieceSqlDto extends AbstractSqlDto {

    private BigInteger contestEventId;
    private String pieceSlug;
    private String pieceName;
    private String pieceYear;


    public EventPieceSqlDto(Object[] eachRowData) {
        this.contestEventId = (BigInteger)eachRowData[0];
        this.pieceSlug = (String)eachRowData[1];
        this.pieceName = (String)eachRowData[2];
        this.pieceYear = (String)eachRowData[3];
    }
}
