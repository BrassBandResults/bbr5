package uk.co.bbr.services.results;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.results.dto.ParseResultDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ParseResultServiceImpl implements ParseResultService {

    private final ParseService parseService;
    private final FinderService finderService;

    @Override
    public List<ParseResultDto> parseBlock(String resultBlock, LocalDate dateContext) {
        List<ParseResultDto> parsedResults = new ArrayList<>();

        String[] lines = resultBlock.split("\n");

        for (String line : lines) {
            parsedResults.add(this.parseLine(line, dateContext));
        }

        return parsedResults;
    }

    @Override
    public ParseResultDto parseLine(String resultLine, LocalDate dateContext) {
        ParseResultDto parsedLine = this.parseService.parseLine(resultLine);
        return this.finderService.findMatches(parsedLine, dateContext);
    }
}
