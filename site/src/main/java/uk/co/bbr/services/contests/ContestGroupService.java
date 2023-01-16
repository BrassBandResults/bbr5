package uk.co.bbr.services.contests;

import uk.co.bbr.services.contests.dao.ContestGroupAliasDao;
import uk.co.bbr.services.contests.dao.ContestGroupDao;
import uk.co.bbr.services.people.dao.PersonAliasDao;

import java.util.Optional;

public interface ContestGroupService {
    ContestGroupDao create(String name);
    ContestGroupDao create(ContestGroupDao contestTag);
    ContestGroupDao migrate(ContestGroupDao contestGroup);

    ContestGroupDao update(ContestGroupDao group);

    Optional<ContestGroupAliasDao> aliasExists(ContestGroupDao group, String name);

    ContestGroupAliasDao migrateAlias(ContestGroupDao group, ContestGroupAliasDao alias);
    ContestGroupAliasDao createAlias(ContestGroupDao group, ContestGroupAliasDao alias);

    Optional<ContestGroupDao> fetchBySlug(String groupSlug);
}
