package uk.co.bbr.services.lookup.sql;

import uk.co.bbr.services.framework.sql.SqlExec;
import uk.co.bbr.services.lookup.sql.dto.LookupSqlDto;

import javax.persistence.EntityManager;
import java.util.List;

public class LookupSql {
    private static final String PEOPLE_LOOKUP_SQL = "SELECT combined_name, slug, known_for as context, 'people' FROM person WHERE UPPER(combined_name) LIKE ?1";
    private static final String GROUP_LOOKUP_SQL = "SELECT name, slug, '' as context, 'contest-groups' FROM contest_group WHERE UPPER(name) LIKE ?1";
    private static final String VENUE_LOOKUP_SQL = "SELECT name, slug, '' as context, 'venues' FROM venue WHERE UPPER(name) LIKE ?1";
    private static final String CONTEST_LOOKUP_SQL = "SELECT name, slug, '' as context, 'contests' FROM contest WHERE UPPER(name) LIKE ?1";
    private static final String BAND_LOOKUP_SQL = "SELECT name, slug, CONCAT(YEAR(start_date), '-', YEAR(end_date)) as context, 'bands' FROM band WHERE UPPER(name) LIKE ?1";


    public static List<LookupSqlDto> lookupPeople(EntityManager entityManager, String searchString) {
        return SqlExec.execute(entityManager, PEOPLE_LOOKUP_SQL, "%" + searchString.toUpperCase() + "%", LookupSqlDto.class);
    }

    public static List<LookupSqlDto> lookupGroups(EntityManager entityManager, String searchString) {
        return SqlExec.execute(entityManager, GROUP_LOOKUP_SQL, "%" + searchString.toUpperCase() + "%", LookupSqlDto.class);
    }

    public static List<LookupSqlDto> lookupVenues(EntityManager entityManager, String searchString) {
        return SqlExec.execute(entityManager, VENUE_LOOKUP_SQL, "%" + searchString.toUpperCase() + "%", LookupSqlDto.class);
    }

    public static List<LookupSqlDto> lookupContests(EntityManager entityManager, String searchString) {
        return SqlExec.execute(entityManager, CONTEST_LOOKUP_SQL, "%" + searchString.toUpperCase() + "%", LookupSqlDto.class);
    }

    public static List<LookupSqlDto> lookupBands(EntityManager entityManager, String searchString) {
        return SqlExec.execute(entityManager, BAND_LOOKUP_SQL, "%" + searchString.toUpperCase() + "%", LookupSqlDto.class);
    }
}
