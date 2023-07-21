package uk.co.bbr.services.results.dto;

import uk.co.bbr.services.results.types.ParseOutcome;

import java.util.List;

public class ParsedResultsDto {

    List<ParseResultDto> parsedResults;
    public ParsedResultsDto(List<ParseResultDto> parsedResults) {
        this.parsedResults = parsedResults;
    }

    public boolean allGreen() {
        boolean allGreen = true;
        for (ParseResultDto eachResult : parsedResults) {
            if (!eachResult.getOutcome().equals(ParseOutcome.GREEN_MATCHES_FOUND_IN_DATABASE)) {
                allGreen = false;
            }
        }
        return allGreen;
    }

    public List<ParseResultDto> getResultLines() {
        return this.parsedResults;
    }
}
