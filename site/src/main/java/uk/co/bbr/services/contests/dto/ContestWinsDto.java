package uk.co.bbr.services.contests.dto;

import lombok.Getter;

@Getter
public class ContestWinsDto {
    private String bandSlug;
    private String bandName;
    private int winCount;

    public ContestWinsDto(Object[] eachRowData) {
        this.bandSlug = (String)eachRowData[0];
        this.bandName = (String)eachRowData[1];
        this.winCount = (Integer)eachRowData[2];
    }
}
