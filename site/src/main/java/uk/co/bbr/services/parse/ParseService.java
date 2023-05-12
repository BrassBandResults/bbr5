package uk.co.bbr.services.parse;

import uk.co.bbr.services.parse.dto.ParseResultDto;

import java.time.LocalDate;
import java.util.List;

public interface ParseService {

    ParseResultDto parseLine(String resultLine, LocalDate dateContext);

    List<ParseResultDto> parseBlock(String resultBlock, LocalDate dateContext);
}
