package uk.co.bbr.services.results;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.results.dto.ParseResultDto;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@RequiredArgsConstructor
public class ParseServiceImpl implements ParseService {
    private static final String REGEX_RESULT_BAND_CONDUCTOR_DRAW_POINTS = "\\s*([\\d\\-WwDd]+)[.,]?\\s+([\\w()&'\\-. /]+)\\s*,\\s*\\(?\\s*([\\w.'\\- ]+)\\)?\\s*,?\\s*([\\d\\-]*)[\\s,]*([\\d.]*)\\s*\\w*";
    private static final String REGEX_BAND_CONDUCTOR = "\\s*([\\w()&'\\-. /]+)\\s*,\\s*\\(?\\s*([\\w.'\\- ]+)\\)?\\s*";

    @Override
    public ParseResultDto parseLine(String resultLine) {
        ParseResultDto parsedResult = new ParseResultDto();

        Pattern pattern1 = Pattern.compile(REGEX_RESULT_BAND_CONDUCTOR_DRAW_POINTS);
        Matcher matcher1 = pattern1.matcher(resultLine);

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
}
