package uk.co.bbr.services.results;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.lookup.BandFinderService;
import uk.co.bbr.services.lookup.PersonFinderService;
import uk.co.bbr.services.people.dao.PersonDao;
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
            BandDao matchedBand = this.bandFinderService.findMatchByName(parsedResult.getRawBandName(), dateContext);
            String matchedBandSlug = null;
            if (matchedBand != null) {
                parsedResult.setMatchedBand(matchedBand.getSlug(), matchedBand.getName());
                matchedBandSlug = matchedBand.getSlug();
            }
            if (parsedResult.getRawConductorName() != null) {
                PersonDao matchedConductor = this.personFinderService.findMatchByName(parsedResult.getRawConductorName(), matchedBandSlug, dateContext);
                if (matchedConductor != null) {
                    parsedResult.setMatchedConductor(matchedConductor.getSlug(), matchedConductor.getCombinedName());
                }
            }

        }

        return parsedResult;
    }
}
