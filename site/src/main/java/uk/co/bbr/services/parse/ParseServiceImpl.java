package uk.co.bbr.services.parse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.parse.dto.ParseResultDto;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@RequiredArgsConstructor
public class ParseServiceImpl implements ParseService {

    private final BandService bandService;
    private final PersonService personService;

    private static final String REGEX_RESULT_BAND_CONDUCTOR_DRAW_POINTS = "\\s*([\\d\\-WwDd]+)[.,]?\\s+([\\w()&'\\-. /]+)\\s*,\\s*\\(?\\s*([\\w.'\\- ]+)\\)?\\s*,?\\s*([\\d\\-]*)[\\s,]*([\\d.]*)\\s*\\w*";
    private static final String REGEX_BAND_CONDUCTOR = "\\s*([\\w()&'\\-. /]+)\\s*,\\s*\\(?\\s*([\\w.'\\- ]+)\\)?\\s*";

    @Override
    public ParseResultDto parseLine(String resultLine, LocalDate dateContext) {

        ParseResultDto result = this.parseLine(resultLine);
        this.searchMatches(result, dateContext);

        return result;
    }

    @Override
    public List<ParseResultDto> parseBlock(String resultBlock, LocalDate dateContext) {
        List<ParseResultDto> parsedResults = new ArrayList<>();

        String[] lines = resultBlock.split("\n");

        for (String line : lines) {
            parsedResults.add(this.parseLine(line, dateContext));
        }

        return parsedResults;
    }

    private ParseResultDto parseLine(String resultLine) {
        ParseResultDto parsedResult = new ParseResultDto();

        Pattern pattern = Pattern.compile(REGEX_RESULT_BAND_CONDUCTOR_DRAW_POINTS);
        Matcher matcher1 = pattern.matcher(resultLine);

        if (matcher1.matches()) {
            parsedResult.setRawPosition(matcher1.group(1).toUpperCase());
            parsedResult.setRawBandName(matcher1.group(2));
            parsedResult.setRawConductorName(matcher1.group(3));
            if (matcher1.group(4).trim().length() > 0) {
                parsedResult.setRawDraw(Integer.parseInt(matcher1.group(4)));
            } else {
                parsedResult.setRawDraw(0);
            }
            parsedResult.setRawPoints(matcher1.group(5));
            return parsedResult;
        }

        Pattern pattern2 = Pattern.compile(REGEX_BAND_CONDUCTOR);
        Matcher matcher2 = pattern2.matcher(resultLine);

        if (matcher2.matches()) {
            parsedResult.setRawPosition("");
            parsedResult.setRawBandName(matcher2.group(1));
            parsedResult.setRawConductorName(matcher2.group(2));
            parsedResult.setRawDraw(null);
            parsedResult.setRawPoints("");
            return parsedResult;
        }

        return parsedResult;
    }

    private void searchMatches(ParseResultDto parsedResult, LocalDate dateContext) {
        if (parsedResult.getRawBandName() != null) {
            BandDao matchedBand = this.bandService.findMatchingBandByName(parsedResult.getRawBandName(), dateContext);
            parsedResult.setMatchedBand(matchedBand);

            if (parsedResult.getRawConductorName() != null) {
                PersonDao matchedConductor = this.personService.findMatchingPersonByName(parsedResult.getRawConductorName(), matchedBand, dateContext);
                parsedResult.setMatchedConductor(matchedConductor);
            }
        }
    }
}
