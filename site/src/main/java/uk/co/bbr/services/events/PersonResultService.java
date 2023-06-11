package uk.co.bbr.services.events;

import uk.co.bbr.services.bands.types.ResultSetCategory;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.dto.ResultDetailsDto;
import uk.co.bbr.services.groups.dao.ContestGroupDao;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.tags.dao.ContestTagDao;

public interface PersonResultService {

    ResultDetailsDto findResultsForConductor(PersonDao person, ResultSetCategory category);
    ResultDetailsDto findResultsForConductor(PersonDao person, ResultSetCategory category, ContestDao contest);
    ResultDetailsDto findResultsForConductor(PersonDao person, ResultSetCategory category, ContestGroupDao contestGroup);
    ResultDetailsDto findResultsForConductor(PersonDao person, ResultSetCategory category, ContestTagDao contestTag);
}
