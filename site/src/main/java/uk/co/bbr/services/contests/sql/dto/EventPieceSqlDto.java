package uk.co.bbr.services.contests.sql.dto;

import lombok.Getter;

import java.math.BigInteger;

@Getter
public class EventPieceSqlDto {

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
