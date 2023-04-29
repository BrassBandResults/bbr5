package uk.co.bbr.services.parse;

import uk.co.bbr.services.parse.dto.ParseResultDto;

import java.time.LocalDate;

public interface ParseService {

    ParseResultDto parseLine(String resultLine, LocalDate dateContext);
}
