package uk.co.bbr.services.parse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.parse.dto.ParseResultDto;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;

import java.time.LocalDate;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@RequiredArgsConstructor
public class ParseServiceImpl implements ParseService {

    private final BandService bandService;
    private final PersonService personService;

    private static final String REGEX_RESULT_BAND_CONDUCTOR_DRAW_POINTS = "\\s*([\\d\\-WwDd]+)\\.?\\s+([\\w()&'\\-. /]+)\\s*,\\s*([\\w.'\\- ]+)\\s*[(,]?\\s*([\\d\\-]+)\\)?[\\s,]*([\\d.]*)\\s*\\w*";

    @Override
    public ParseResultDto parseLine(String resultLine, LocalDate dateContext) {
        ParseResultDto parsedResult = new ParseResultDto();

        Pattern pattern = Pattern.compile(REGEX_RESULT_BAND_CONDUCTOR_DRAW_POINTS);
        Matcher matcher = pattern.matcher(resultLine);

        if (matcher.matches()) {
            parsedResult.setRawPosition(matcher.group(1).toUpperCase());
            parsedResult.setRawBandName(matcher.group(2));
            parsedResult.setRawConductorName(matcher.group(3));
            parsedResult.setRawDraw(matcher.group(4));
            parsedResult.setRawPoints(matcher.group(5));

            BandDao matchedBand = this.bandService.findMatchingBandByName(parsedResult.getRawBandName(), dateContext);
            parsedResult.setMatchedBand(matchedBand);

            PersonDao matchedConductor = this.personService.findMatchingPersonByName(parsedResult.getRawConductorName(), matchedBand, dateContext);
            parsedResult.setMatchedConductor(matchedConductor);
        }

        return parsedResult;
    }
}
