package uk.co.bbr.services.results;

import uk.co.bbr.services.results.dto.ParseResultDto;

import java.util.List;

public interface ParseService {

    ParseResultDto parseLine(String resultLine);
}
