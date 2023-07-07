package uk.co.bbr.services.groups;

import uk.co.bbr.services.groups.dao.ContestGroupAliasDao;
import uk.co.bbr.services.groups.dao.ContestGroupDao;
import uk.co.bbr.services.tags.dao.ContestTagDao;
import uk.co.bbr.services.groups.dto.ContestGroupDetailsDto;
import uk.co.bbr.services.groups.dto.ContestGroupYearDto;
import uk.co.bbr.services.groups.dto.ContestGroupYearsDetailsDto;
import uk.co.bbr.services.events.dto.GroupListDto;

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

    List<ContestGroupDao> lookupByPrefix(String searchString);
}

