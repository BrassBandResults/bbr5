package uk.co.bbr.services.results;

import uk.co.bbr.services.results.dto.ParseResultDto;

public interface ParseService {

    ParseResultDto parseLine(String resultLine);
}
