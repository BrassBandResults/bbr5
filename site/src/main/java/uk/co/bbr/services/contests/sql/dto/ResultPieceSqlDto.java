package uk.co.bbr.services.contests.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;

import java.math.BigInteger;

@Getter
public class ResultPieceSqlDto extends AbstractSqlDto {


    private final BigInteger contestResultId;
    private final String pieceSlug;
    private final String pieceName;
    private final String pieceYear;



    public ResultPieceSqlDto(Object[] columnList) {
        this.contestResultId = (BigInteger)columnList[0];
        this.pieceSlug = (String)columnList[1];
        this.pieceName = (String)columnList[2];
        this.pieceYear = (String)columnList[3];
    }
}
