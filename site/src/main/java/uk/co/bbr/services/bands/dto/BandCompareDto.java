package uk.co.bbr.services.bands.dto;

import lombok.Getter;
import uk.co.bbr.services.bands.sql.dto.CompareBandsSqlDto;

import java.util.ArrayList;
import java.util.List;

@Getter
public class BandCompareDto {
    private final List<CompareBandsSqlDto> results;
    private int leftBandPercent;
    private int rightBandPercent;
    private int leftBandWins;
    private int rightBandWins;

    public BandCompareDto(List<CompareBandsSqlDto> results, String contestSlug) {
        List<CompareBandsSqlDto> filteredResults = new ArrayList<>();
        for (CompareBandsSqlDto eachResult : results) {
            if (eachResult.getContestSlug().equals(contestSlug)) {
                filteredResults.add(eachResult);
            }
        }
        this.results = filteredResults;
        this.calculateWins(this.results);
    }

    public BandCompareDto(List<CompareBandsSqlDto> results) {
        this.results = results;
        this.calculateWins(this.results);
    }

    private void calculateWins(List<CompareBandsSqlDto> resultsToAnalyse) {
        int leftCount = 0;
        int rightCount = 0;

        for (CompareBandsSqlDto eachResult : resultsToAnalyse) {
            if (eachResult.getLeftResult() != 0 && eachResult.getLeftResult() < eachResult.getRightResult()) {
                leftCount++;
            }
            if (eachResult.getRightResult() != 0 && eachResult.getLeftResult() > eachResult.getRightResult()) {
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
