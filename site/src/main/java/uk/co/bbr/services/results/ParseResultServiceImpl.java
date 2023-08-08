package uk.co.bbr.services.results;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.results.dto.ParseResultDto;
import uk.co.bbr.services.results.dto.ParsedResultsDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ParseResultServiceImpl implements ParseResultService {

    private final ParseService parseService;
    private final FinderService finderService;

    @Override
    public ParsedResultsDto parseBlock(String resultBlock, LocalDate dateContext) {
        List<ParseResultDto> parsedResults = new ArrayList<>();

        String[] lines = resultBlock.split("\n");

        for (String line : lines) {
            if (line.strip().length() == 0) {
                continue;
            }
            parsedResults.add(this.parseLine(line, dateContext));
        }

        return new ParsedResultsDto(parsedResults);
    }

    @Override
    public ParseResultDto parseLine(String resultLine, LocalDate dateContext) {
        ParseResultDto parsedLine = this.parseService.parseLine(resultLine);
        return this.finderService.findMatches(parsedLine, dateContext);
    }
}
