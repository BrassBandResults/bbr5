package uk.co.bbr.services.contests;

import uk.co.bbr.services.contests.dao.ContestDao;

public interface ContestService {
    ContestDao create(String name);
    ContestDao create(ContestDao contest);
}
