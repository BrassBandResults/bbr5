package uk.co.bbr.services.contests;

import uk.co.bbr.services.contests.dao.ContestGroupAliasDao;
import uk.co.bbr.services.contests.dao.ContestGroupDao;
import uk.co.bbr.services.contests.dao.ContestTagDao;
import uk.co.bbr.services.contests.dto.ContestGroupDetailsDto;
import uk.co.bbr.services.contests.dto.ContestGroupYearDto;
import uk.co.bbr.services.contests.dto.ContestGroupYearsDetailsDto;
import uk.co.bbr.services.contests.dto.GroupListDto;

import java.util.List;
import java.util.Optional;

public interface ContestGroupService {
    ContestGroupDao create(String name);
    ContestGroupDao create(ContestGroupDao contestTag);
    ContestGroupDao migrate(ContestGroupDao contestGroup);

    ContestGroupDao update(ContestGroupDao group);

    Optional<ContestGroupAliasDao> aliasExists(ContestGroupDao group, String name);

    ContestGroupAliasDao migrateAlias(ContestGroupDao group, ContestGroupAliasDao alias);
    ContestGroupAliasDao createAlias(ContestGroupDao group, ContestGroupAliasDao alias);
    ContestGroupAliasDao createAlias(ContestGroupDao group, String alias);
    Optional<ContestGroupDao> fetchBySlug(String groupSlug);
    GroupListDto listGroupsStartingWith(String prefix);

    ContestGroupDao addGroupTag(ContestGroupDao group, ContestTagDao tag);
    ContestGroupDetailsDto fetchDetail(ContestGroupDao groupGroup);
    ContestGroupYearsDetailsDto fetchYearsBySlug(String groupSlug);

    ContestGroupYearDto fetchEventsByGroupSlugAndYear(String groupSlug, Integer year);

    List<ContestGroupAliasDao> fetchAliases(ContestGroupDao contestGroup);
}

