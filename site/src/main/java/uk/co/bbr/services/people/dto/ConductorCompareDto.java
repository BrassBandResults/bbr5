package uk.co.bbr.services.people.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.co.bbr.services.people.sql.dto.CompareConductorsSqlDto;

import java.util.List;

@Getter
public class ConductorCompareDto {
    private final List<CompareConductorsSqlDto> results;
    private final int leftPersonPercent;
    private final int rightPersonPercent;
    private final int leftPersonWins;
    private final int rightPersonWins;

    public ConductorCompareDto(List<CompareConductorsSqlDto> results) {
        this.results = results;

        int leftCount = 0;
        int rightCount = 0;

        for (CompareConductorsSqlDto eachResult : this.results) {
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
