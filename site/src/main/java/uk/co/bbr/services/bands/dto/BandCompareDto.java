package uk.co.bbr.services.bands.dto;

import lombok.Getter;
import uk.co.bbr.services.bands.sql.dto.CompareBandsSqlDto;

import java.util.List;

@Getter
public class BandCompareDto {
    private final List<CompareBandsSqlDto> results;
    private final int leftBandPercent;
    private final int rightBandPercent;
    private final int leftBandWins;
    private final int rightBandWins;

    public BandCompareDto(List<CompareBandsSqlDto> results) {
        this.results = results;

        int leftCount = 0;
        int rightCount = 0;

        for (CompareBandsSqlDto eachResult : this.results) {
            if (eachResult.getLeftResult() < eachResult.getRightResult()) {
                leftCount++;
            }
            if (eachResult.getLeftResult() > eachResult.getRightResult()) {
                rightCount++;
            }
        }
        this.leftBandWins = leftCount;
        this.rightBandWins = rightCount;

        int totalWins = leftBandWins + rightBandWins;

        if (totalWins > 0) {
            this.leftBandPercent = (this.leftBandWins * 100) / totalWins;
            this.rightBandPercent = (this.rightBandWins * 100) / totalWins;
        } else {
            this.leftBandPercent = 0;
            this.rightBandPercent = 0;
        }
    }
}
