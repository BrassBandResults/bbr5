package uk.co.bbr.services.contests;

import uk.co.bbr.services.contests.dao.ContestTagDao;
import uk.co.bbr.services.contests.dao.ContestTypeDao;

import java.util.Optional;

public interface ContestTypeService {

    ContestTypeDao fetchDefaultContestType();

    Optional<ContestTypeDao> fetchByName(String text);
}
