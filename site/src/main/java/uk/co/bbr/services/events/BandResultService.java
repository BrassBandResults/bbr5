package uk.co.bbr.services.events;

import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.types.ResultSetCategory;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.dto.ResultDetailsDto;
import uk.co.bbr.services.groups.dao.ContestGroupDao;
import uk.co.bbr.services.tags.dao.ContestTagDao;

public interface BandResultService {

    ResultDetailsDto findResultsForBand(BandDao band, ResultSetCategory category);

    ResultDetailsDto findResultsForBand(BandDao band, ResultSetCategory category, ContestDao contest);

    ResultDetailsDto findResultsForBand(BandDao band, ResultSetCategory category, ContestGroupDao contestGroup);

    ResultDetailsDto findResultsForBand(BandDao band, ResultSetCategory category, ContestTagDao contestTag);
}
