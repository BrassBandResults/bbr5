package uk.co.bbr.services.contests;

import uk.co.bbr.services.contests.dao.ContestTagDao;
import uk.co.bbr.services.contests.dao.ContestTypeDao;

public interface ContestTypeService {

    ContestTypeDao fetchDefaultContestType();
}
