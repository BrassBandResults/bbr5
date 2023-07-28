package uk.co.bbr.services.results;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.lookup.BandFinderService;
import uk.co.bbr.services.lookup.PersonFinderService;
import uk.co.bbr.services.results.dto.ParseResultDto;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class FinderServiceImpl implements FinderService {
    private final BandFinderService bandFinderService;
    private final PersonFinderService personFinderService;

    @Override
    public ParseResultDto findMatches(ParseResultDto parsedResult, LocalDate dateContext) {
        if (parsedResult.getRawBandName() != null) {
            String matchedBandSlug = this.bandFinderService.findMatchByName(parsedResult.getRawBandName(), dateContext);
            parsedResult.setMatchedBand(matchedBandSlug);

            if (parsedResult.getRawConductorName() != null) {
                String matchedConductorSlug = this.personFinderService.findMatchByName(parsedResult.getRawConductorName(), matchedBandSlug, dateContext);
                parsedResult.setMatchedConductor(matchedConductorSlug);
            }
        }

        return parsedResult;
    }
}
