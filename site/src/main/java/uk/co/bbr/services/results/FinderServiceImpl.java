package uk.co.bbr.services.results;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.bands.BandFinderService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.people.PersonFinderService;
import uk.co.bbr.services.results.dto.ParseResultDto;
import uk.co.bbr.services.people.dao.PersonDao;

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
