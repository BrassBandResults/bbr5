package uk.co.bbr.services.results;

import uk.co.bbr.services.results.dto.ParseResultDto;

import java.time.LocalDate;

public interface FinderService {
    ParseResultDto findMatches(ParseResultDto parsedLine, LocalDate dateContext);
}
