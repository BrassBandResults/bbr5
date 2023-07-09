package uk.co.bbr.services.lookup;

import uk.co.bbr.services.events.dto.GroupListDto;
import uk.co.bbr.services.groups.dao.ContestGroupAliasDao;
import uk.co.bbr.services.groups.dao.ContestGroupDao;
import uk.co.bbr.services.groups.dto.ContestGroupDetailsDto;
import uk.co.bbr.services.groups.dto.ContestGroupYearDto;
import uk.co.bbr.services.groups.dto.ContestGroupYearsDetailsDto;
import uk.co.bbr.services.lookup.sql.dto.LookupSqlDto;
import uk.co.bbr.services.tags.dao.ContestTagDao;

import java.util.List;
import java.util.Optional;

public interface LookupService {
    List<LookupSqlDto> lookupPeople(String searchString);

    List<LookupSqlDto> lookupGroups(String searchString);

    List<LookupSqlDto> lookupVenues(String searchString);

    List<LookupSqlDto> lookupContests(String searchString);

    List<LookupSqlDto> lookupBands(String searchString);

    List<LookupSqlDto> lookupPieces(String searchString);

    List<LookupSqlDto> lookupTags(String searchString);
}

