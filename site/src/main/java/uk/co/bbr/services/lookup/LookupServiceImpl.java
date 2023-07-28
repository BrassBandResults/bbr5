package uk.co.bbr.services.lookup;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.lookup.sql.LookupSql;
import uk.co.bbr.services.lookup.sql.dto.LookupSqlDto;

import javax.persistence.EntityManager;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LookupServiceImpl implements LookupService, SlugTools {

    private final EntityManager entityManager;

    @Override
    public List<LookupSqlDto> lookupPeople(String searchString) {
        return LookupSql.lookupPeople(this.entityManager, searchString);
    }

    @Override
    public List<LookupSqlDto> lookupGroups(String searchString) {
        return LookupSql.lookupGroups(this.entityManager, searchString);
    }

    @Override
    public List<LookupSqlDto> lookupVenues(String searchString) {
        return LookupSql.lookupVenues(this.entityManager, searchString);
    }

    @Override
    public List<LookupSqlDto> lookupContests(String searchString) {
        return LookupSql.lookupContests(this.entityManager, searchString);
    }

    @Override
    public List<LookupSqlDto> lookupBands(String searchString) {
        return LookupSql.lookupBands(this.entityManager, searchString);
    }

    @Override
    public List<LookupSqlDto> lookupPieces(String searchString) {
        return LookupSql.lookupPieces(this.entityManager, searchString);
    }

    @Override
    public List<LookupSqlDto> lookupTags(String searchString) {
        return LookupSql.lookupTags(this.entityManager, searchString);
    }

    @Override
    public List<LookupSqlDto> lookupBandAlias(String searchString) {
        return LookupSql.lookupBandAlias(this.entityManager, searchString);
    }

    @Override
    public List<LookupSqlDto> lookupContestAlias(String searchString) {
        return LookupSql.lookupContestAlias(this.entityManager, searchString);
    }

    @Override
    public List<LookupSqlDto> lookupPeopleAlias(String searchString) {
        return LookupSql.lookupPeopleAlias(this.entityManager, searchString);
    }

    @Override
    public List<LookupSqlDto> lookupPieceAlias(String searchString) {
        return LookupSql.lookupPieceAlias(this.entityManager, searchString);
    }

    @Override
    public List<LookupSqlDto> lookupVenueAlias(String searchString) {
        return LookupSql.lookupVenueAlias(this.entityManager, searchString);
    }
}
