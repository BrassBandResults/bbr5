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
            BandDao matchedBand = this.bandFinderService.findMatchByName(parsedResult.getRawBandName(), dateContext);
            parsedResult.setMatchedBand(matchedBand);

            if (parsedResult.getRawConductorName() != null) {
                PersonDao matchedConductor = this.personFinderService.findMatchByName(parsedResult.getRawConductorName(), matchedBand, dateContext);
                parsedResult.setMatchedConductor(matchedConductor);
            }
        }

        return parsedResult;
    }
}
