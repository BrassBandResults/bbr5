package uk.co.bbr.services.contests;

import uk.co.bbr.services.contests.dao.ContestGroupDao;
import uk.co.bbr.services.contests.dao.ContestTagDao;

public interface ContestGroupService {
    ContestGroupDao create(String name);
    ContestGroupDao create(ContestGroupDao contestTag);

    ContestGroupDao update(ContestGroupDao group);
}
