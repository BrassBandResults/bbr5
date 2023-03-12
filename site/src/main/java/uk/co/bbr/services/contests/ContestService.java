package uk.co.bbr.services.contests;

import uk.co.bbr.services.contests.dao.ContestAliasDao;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dao.ContestGroupDao;
import uk.co.bbr.services.contests.dao.ContestTagDao;
import uk.co.bbr.services.contests.dto.ContestListDto;
import uk.co.bbr.services.people.dao.PersonAliasDao;
import uk.co.bbr.services.regions.dao.RegionDao;

import java.util.Optional;

public interface ContestService {
    ContestDao create(String name);
    ContestDao create(String contestName, ContestGroupDao group, int ordering);

    ContestDao create(ContestDao contest);

    ContestDao migrate(ContestDao contest);
    ContestDao update(ContestDao contest);

    ContestAliasDao migrateAlias(ContestDao contest, ContestAliasDao previousName);
    ContestAliasDao createAlias(ContestDao contest, ContestAliasDao previousName);
    ContestAliasDao createAlias(ContestDao contest, String previousName);

    Optional<ContestAliasDao> aliasExists(ContestDao contest, String aliasName);

    Optional<ContestDao> fetchBySlug(String slug);

    ContestListDto listContestsStartingWith(String letter);

    ContestDao addContestToGroup(ContestDao contest, ContestGroupDao group);

    ContestDao addContestTag(ContestDao contest, ContestTagDao tag);


}
