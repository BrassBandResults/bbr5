package uk.co.bbr.services.results;

import uk.co.bbr.services.results.dto.ParseResultDto;

import java.time.LocalDate;
import java.util.List;

public interface ParseResultService {

    ParseResultDto parseLine(String resultLine, LocalDate dateContext);

    List<ParseResultDto> parseBlock(String resultBlock, LocalDate dateContext);
}
