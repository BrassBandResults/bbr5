package uk.co.bbr.services.contests.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;

import java.math.BigInteger;

@Getter
public class EventPieceSqlDto extends AbstractSqlDto {

    private final Long contestEventId;
    private final String pieceSlug;
    private final String pieceName;
    private final String pieceYear;


    public EventPieceSqlDto(Object[] columnList) {
        this.contestEventId = this.getLong(columnList,0);
        this.pieceSlug = this.getString(columnList, 1);
        this.pieceName = this.getString(columnList, 2);
        this.pieceYear = this.getString(columnList, 3);
    }
}
