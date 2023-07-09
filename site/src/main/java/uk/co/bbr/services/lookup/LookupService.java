package uk.co.bbr.services.lookup;

import uk.co.bbr.services.lookup.sql.dto.LookupSqlDto;

import java.util.List;

public interface LookupService {
    List<LookupSqlDto> lookupPeople(String searchString);

    List<LookupSqlDto> lookupGroups(String searchString);

    List<LookupSqlDto> lookupVenues(String searchString);

    List<LookupSqlDto> lookupContests(String searchString);

    List<LookupSqlDto> lookupBands(String searchString);

    List<LookupSqlDto> lookupPieces(String searchString);

    List<LookupSqlDto> lookupTags(String searchString);

    List<LookupSqlDto> lookupBandAlias(String searchString);

    List<LookupSqlDto> lookupContestAlias(String searchString);

    List<LookupSqlDto> lookupPeopleAlias(String searchString);

    List<LookupSqlDto> lookupPieceAlias(String searchString);

    List<LookupSqlDto> lookupVenueAlias(String searchString);
}

