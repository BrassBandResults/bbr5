package uk.co.bbr.services.contests;

import uk.co.bbr.services.contests.dao.ContestTypeDao;

import java.util.List;
import java.util.Optional;

public interface ContestTypeService {

    ContestTypeDao fetchDefaultContestType();

    Optional<ContestTypeDao> fetchByName(String text);

    List<ContestTypeDao> fetchAll();

    Optional<ContestTypeDao> fetchById(Long contestTypeId);

    Optional<ContestTypeDao> fetchBySlug(String contestTypeSlug);
}
