package uk.co.bbr.services.people.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.co.bbr.services.people.sql.dto.CompareConductorsSqlDto;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ConductorCompareDto {
    private final List<CompareConductorsSqlDto> results;
    private int leftPersonPercent;
    private int rightPersonPercent;
    private int leftPersonWins;
    private int rightPersonWins;

    public ConductorCompareDto(List<CompareConductorsSqlDto> results, String contestSlug) {
        List<CompareConductorsSqlDto> filteredResults = new ArrayList<>();
        for (CompareConductorsSqlDto eachResult : results) {
            if (eachResult.getContestSlug().equals(contestSlug)) {
                filteredResults.add(eachResult);
            }
        }
        this.results = filteredResults;
        this.calculateWins(this.results);
    }

    public ConductorCompareDto(List<CompareConductorsSqlDto> results) {
        this.results = results;
        this.calculateWins(this.results);
    }

    private void calculateWins(List<CompareConductorsSqlDto> resultsToAnalyse) {
        int leftCount = 0;
        int rightCount = 0;

        for (CompareConductorsSqlDto eachResult : resultsToAnalyse) {
            if (eachResult.getLeftResult() < eachResult.getRightResult()) {
                leftCount++;
            }
            if (eachResult.getLeftResult() > eachResult.getRightResult()) {
                rightCount++;
            }
        }
        this.leftPersonWins = leftCount;
        this.rightPersonWins = rightCount;

        int totalWins = leftPersonWins + rightPersonWins;

        if (totalWins > 0) {
            this.leftPersonPercent = (this.leftPersonWins * 100) / totalWins;
            this.rightPersonPercent = (this.rightPersonWins * 100) / totalWins;
        } else {
            this.leftPersonPercent = 0;
            this.rightPersonPercent = 0;
        }
    }
}
