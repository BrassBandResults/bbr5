package uk.co.bbr.services.contests;

import uk.co.bbr.services.contests.dao.ContestAliasDao;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.people.dao.PersonAliasDao;

import java.util.Optional;

public interface ContestService {
    ContestDao create(String name);
    ContestDao create(ContestDao contest);

    ContestDao migrate(ContestDao contest);

    ContestAliasDao migrateAlias(ContestDao contest, ContestAliasDao previousName);
    ContestAliasDao createAlias(ContestDao contest, ContestAliasDao previousName);

    Optional<ContestAliasDao> aliasExists(ContestDao contest, String aliasName);

    Optional<ContestDao> fetchBySlug(String slug);
}
