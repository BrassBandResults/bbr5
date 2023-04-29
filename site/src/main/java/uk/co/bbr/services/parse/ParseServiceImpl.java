package uk.co.bbr.services.parse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.parse.dto.ParseResultDto;


@Service
@RequiredArgsConstructor
public class ParseServiceImpl implements ParseService {

    @Override
    public ParseResultDto parseLine(String resultLine) {
        ParseResultDto parsedResult = new ParseResultDto();

        return parsedResult;
    }
}
