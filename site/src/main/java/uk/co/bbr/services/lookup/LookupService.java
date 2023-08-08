package uk.co.bbr.services.lookup;

import uk.co.bbr.services.lookup.sql.dto.LookupSqlDto;

import java.util.List;

public interface LookupService {
    List<LookupSqlDto> lookupPeople(String searchString);
    List<LookupSqlDto> lookupPeopleAndAlias(String searchString);

    List<LookupSqlDto> lookupGroups(String searchString);
    List<LookupSqlDto> lookupGroupsAndAlias(String searchString);

    List<LookupSqlDto> lookupVenues(String searchString);
    List<LookupSqlDto> lookupVenuesAndAlias(String searchString);

    List<LookupSqlDto> lookupContests(String searchString);
    List<LookupSqlDto> lookupContestsAndAlias(String searchString);

    List<LookupSqlDto> lookupBands(String searchString);
    List<LookupSqlDto> lookupBandsAndAlias(String searchString);

    List<LookupSqlDto> lookupPieces(String searchString);
    List<LookupSqlDto> lookupPiecesAndAlias(String searchString);

    List<LookupSqlDto> lookupTags(String searchString);

    List<LookupSqlDto> lookupBandAlias(String searchString);

    List<LookupSqlDto> lookupContestAlias(String searchString);

    List<LookupSqlDto> lookupPeopleAlias(String searchString);

    List<LookupSqlDto> lookupPieceAlias(String searchString);

    List<LookupSqlDto> lookupVenueAlias(String searchString);

    List<LookupSqlDto> lookupGroupAlias(String searchString);
}

