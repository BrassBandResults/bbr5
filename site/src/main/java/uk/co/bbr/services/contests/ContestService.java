package uk.co.bbr.services.contests;

import uk.co.bbr.services.contests.dao.ContestAliasDao;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.sql.dto.ContestListSqlDto;
import uk.co.bbr.services.groups.dao.ContestGroupDao;
import uk.co.bbr.services.tags.dao.ContestTagDao;

import java.util.List;
import java.util.Optional;

public interface ContestService {
    ContestDao create(String name);
    ContestDao create(String contestName, ContestGroupDao group, int ordering);

    ContestDao create(ContestDao contest);

    ContestDao update(ContestDao contest);

    ContestAliasDao createAlias(ContestDao contest, ContestAliasDao previousName);
    ContestAliasDao createAlias(ContestDao contest, String previousName);

    Optional<ContestAliasDao> aliasExists(ContestDao contest, String aliasName);

    Optional<ContestDao> fetchBySlug(String slug);

    List<ContestListSqlDto> listContestsStartingWith(String letter);

    List<ContestListSqlDto> listUnusedContests();

    ContestDao addContestToGroup(ContestDao contest, ContestGroupDao group);

    ContestDao addContestTag(ContestDao contest, ContestTagDao tag);

    List<ContestAliasDao> fetchAliases(ContestDao contest);

    Optional<ContestDao> fetchByExactName(String contestName);

    Optional<ContestDao> fetchByNameUpper(String contestName);

    void delete(ContestDao contest);

    ContestDao fetchContestLinkUp(ContestDao contest);

    ContestDao fetchContestLinkDown(ContestDao contest);
}
