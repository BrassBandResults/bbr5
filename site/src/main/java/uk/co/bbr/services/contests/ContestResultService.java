package uk.co.bbr.services.contests;

import uk.co.bbr.services.contests.dao.ContestEventDao;
import uk.co.bbr.services.contests.dao.ContestResultDao;

public interface ContestResultService {

    ContestResultDao addResult(ContestEventDao event, ContestResultDao result);
}
