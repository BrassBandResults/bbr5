package uk.co.bbr.services.lookup.sql;

import uk.co.bbr.services.framework.sql.SqlExec;
import uk.co.bbr.services.lookup.sql.dto.LookupSqlDto;

import jakarta.persistence.EntityManager;
import java.util.List;

public class LookupSql {
    private static final String PEOPLE_LOOKUP_SQL = "SELECT combined_name, slug, known_for as context, 'people', 'O' as type FROM person WHERE UPPER(combined_name) LIKE ?1";
    private static final String GROUP_LOOKUP_SQL = "SELECT name, slug, '' as context, 'contest-groups', 'O' as type FROM contest_group WHERE UPPER(name) LIKE ?1";
    private static final String VENUE_LOOKUP_SQL = "SELECT name, slug, '' as context, 'venues', 'O' as type FROM venue WHERE UPPER(name) LIKE ?1";
    private static final String CONTEST_LOOKUP_SQL = "SELECT name, slug, '' as context, 'contests', 'O' as type FROM contest WHERE UPPER(name) LIKE ?1";
    private static final String BAND_LOOKUP_SQL = "SELECT name, slug, CONCAT(YEAR(start_date), '-', YEAR(end_date)) as context, 'bands', 'O' as type FROM band WHERE UPPER(name) LIKE ?1";
    private static final String PIECE_LOOKUP_SQL = "SELECT p.name, p.slug, composer.combined_name as context, 'pieces', 'O' as type FROM piece p LEFT OUTER JOIN person composer ON composer.id = p.composer_id WHERE UPPER(p.name) LIKE ?1";
    private static final String TAG_LOOKUP_SQL = "SELECT name, slug, '' as context, 'tags', 'O' as type FROM contest_tag WHERE UPPER(name) LIKE ?1";
    private static final String VENUE_ALIAS_LOOKUP_SQL = "SELECT a.name, v.slug, v.name as context, 'venues', 'A' as type FROM venue_alias a INNER JOIN venue v ON a.venue_id = v.id WHERE UPPER(a.name) LIKE ?1";
    private static final String PIECE_ALIAS_LOOKUP_SQL = "SELECT a.name, p.slug, p.name as context, 'pieces', 'A' as type FROM piece_alias a INNER JOIN piece p ON a.piece_id = p.id WHERE UPPER(a.name) LIKE ?1";
    private static final String PEOPLE_ALIAS_LOOKUP_SQL = "SELECT a.name, p.slug, p.combined_name as context, 'people', 'A' as type FROM person_alias a INNER JOIN person p ON a.person_id = p.id WHERE UPPER(a.name) LIKE ?1";
    private static final String CONTEST_ALIAS_LOOKUP_SQL = "SELECT a.name, c.slug, c.name as context, 'contests', 'A' as type FROM contest_alias a INNER JOIN contest c ON a.contest_id = c.id WHERE UPPER(a.name) LIKE ?1";
    private static final String BAND_ALIAS_LOOKUP_SQL = "SELECT a.old_name, b.slug, b.name as context, 'bands', 'A' as type FROM band_previous_name a INNER JOIN band b ON a.band_id = b.id WHERE UPPER(a.old_name) LIKE ?1";
    private static final String GROUP_ALIAS_LOOKUP_SQL = "SELECT a.name, b.slug, b.name as context, 'contest-groups', 'A' as type FROM contest_group_alias a INNER JOIN contest_group b ON a.contest_group_id = b.id WHERE UPPER(a.name) LIKE ?1";

    private static List<LookupSqlDto> lookup(EntityManager entityManager, String sql, String searchString) {
        return SqlExec.execute(entityManager, sql, "%" + searchString.toUpperCase() + "%", LookupSqlDto.class);
    }

    public static List<LookupSqlDto> lookupPeople(EntityManager entityManager, String searchString) {
        return LookupSql.lookup(entityManager, PEOPLE_LOOKUP_SQL, searchString);
    }

    public static List<LookupSqlDto> lookupGroups(EntityManager entityManager, String searchString) {
        return LookupSql.lookup(entityManager, GROUP_LOOKUP_SQL, searchString);
    }

    public static List<LookupSqlDto> lookupVenues(EntityManager entityManager, String searchString) {
        return LookupSql.lookup(entityManager, VENUE_LOOKUP_SQL, searchString);
    }

    public static List<LookupSqlDto> lookupContests(EntityManager entityManager, String searchString) {
        return LookupSql.lookup(entityManager, CONTEST_LOOKUP_SQL, searchString);
    }

    public static List<LookupSqlDto> lookupBands(EntityManager entityManager, String searchString) {
        return LookupSql.lookup(entityManager, BAND_LOOKUP_SQL, searchString);
    }

    public static List<LookupSqlDto> lookupPieces(EntityManager entityManager, String searchString) {
        return LookupSql.lookup(entityManager, PIECE_LOOKUP_SQL, searchString);
    }

    public static List<LookupSqlDto> lookupTags(EntityManager entityManager, String searchString) {
        return LookupSql.lookup(entityManager, TAG_LOOKUP_SQL, searchString);
    }

    public static List<LookupSqlDto> lookupVenueAlias(EntityManager entityManager, String searchString) {
        return LookupSql.lookup(entityManager, VENUE_ALIAS_LOOKUP_SQL, searchString);
    }

    public static List<LookupSqlDto> lookupPieceAlias(EntityManager entityManager, String searchString) {
        return LookupSql.lookup(entityManager, PIECE_ALIAS_LOOKUP_SQL, searchString);
    }

    public static List<LookupSqlDto> lookupPeopleAlias(EntityManager entityManager, String searchString) {
        return LookupSql.lookup(entityManager, PEOPLE_ALIAS_LOOKUP_SQL, searchString);
    }

    public static List<LookupSqlDto> lookupContestAlias(EntityManager entityManager, String searchString) {
        return LookupSql.lookup(entityManager, CONTEST_ALIAS_LOOKUP_SQL, searchString);
    }

    public static List<LookupSqlDto> lookupBandAlias(EntityManager entityManager, String searchString) {
        return LookupSql.lookup(entityManager, BAND_ALIAS_LOOKUP_SQL, searchString);
    }

    public static List<LookupSqlDto> lookupGroupAlias(EntityManager entityManager, String searchString) {
        return LookupSql.lookup(entityManager, GROUP_ALIAS_LOOKUP_SQL, searchString);
    }
}
