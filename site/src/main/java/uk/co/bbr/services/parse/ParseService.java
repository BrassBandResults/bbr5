package uk.co.bbr.services.parse;

import uk.co.bbr.services.parse.dto.ParseResultDto;

public interface ParseService {

    ParseResultDto parseLine(String resultLine);
}
