package uk.co.bbr.services.contests;

import uk.co.bbr.services.contests.dao.ContestTagDao;

public interface ContestTagService {
    ContestTagDao create(String name);
    ContestTagDao create(ContestTagDao contestTag);
}
