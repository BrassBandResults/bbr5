package uk.co.bbr.services.lookup.sql;

import uk.co.bbr.services.framework.sql.SqlExec;
import uk.co.bbr.services.lookup.sql.dto.FinderSqlDto;
import uk.co.bbr.services.lookup.sql.dto.LookupSqlDto;

import javax.persistence.EntityManager;
import java.util.List;

public class FinderSql {
    private static final String BAND_FIND_BY_EXACT_NAME_SQL = "SELECT b.name, b.slug, b.start_date, b.end_date FROM band b WHERE UPPER(b.name) = ?1";
    private static final String BAND_ALIAS_FIND_BY_EXACT_NAME_SQL = "SELECT a.old_name as old_name, b.slug as slug, COALESCE(a.start_date, b.start_date) as start_date, COALESCE(a.end_date, b.start_date) as end_date FROM band_previous_name a INNER JOIN band b ON b.id = a.band_id WHERE UPPER(a.old_name) = ?1";

    private static final String BAND_FIND_BY_CONTAINS_NAME_SQL = "SELECT b.name, b.slug, b.start_date, b.end_date FROM band b WHERE UPPER(b.name) LIKE ?1";
    private static final String BAND_ALIAS_FIND_BY_CONTAINS_NAME_SQL = "SELECT a.old_name as old_name, b.slug as slug, COALESCE(a.start_date, b.start_date) as start_date, COALESCE(a.end_date, b.start_date) as end_date FROM band_previous_name a INNER JOIN band b ON b.id = a.band_id WHERE UPPER(a.old_name) LIKE ?1";


    public static List<FinderSqlDto> bandFindExactNameMatch(EntityManager entityManager, String bandNameUpper) {
        return SqlExec.execute(entityManager, BAND_FIND_BY_EXACT_NAME_SQL, bandNameUpper, FinderSqlDto.class);
    }

    public static List<FinderSqlDto> bandAliasFindExactNameMatch(EntityManager entityManager, String bandNameUpper) {
        return SqlExec.execute(entityManager, BAND_ALIAS_FIND_BY_EXACT_NAME_SQL, bandNameUpper, FinderSqlDto.class);
    }

    public static List<FinderSqlDto> bandFindContainsNameMatch(EntityManager entityManager, String bandNameUpper) {
        return SqlExec.execute(entityManager, BAND_FIND_BY_CONTAINS_NAME_SQL, "%" + bandNameUpper + "%", FinderSqlDto.class);
    }

    public static List<FinderSqlDto> bandAliasFindContainsNameMatch(EntityManager entityManager, String bandNameUpper) {
        return SqlExec.execute(entityManager, BAND_ALIAS_FIND_BY_CONTAINS_NAME_SQL, "%" + bandNameUpper + "%", FinderSqlDto.class);
    }

}
