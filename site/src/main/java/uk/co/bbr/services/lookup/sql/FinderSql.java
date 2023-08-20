package uk.co.bbr.services.lookup.sql;

import lombok.experimental.UtilityClass;
import uk.co.bbr.services.framework.sql.SqlExec;
import uk.co.bbr.services.lookup.sql.dto.FinderSqlDto;
import jakarta.persistence.EntityManager;
import java.util.List;

@UtilityClass
public class FinderSql {
    private static final String BAND_FIND_BY_EXACT_NAME_SQL = "SELECT b.name, b.slug, b.start_date, b.end_date FROM band b WHERE UPPER(b.name) = ?1";
    private static final String BAND_ALIAS_FIND_BY_EXACT_NAME_SQL = "SELECT a.old_name as old_name, b.slug as slug, COALESCE(a.start_date, b.start_date) as start_date, COALESCE(a.end_date, b.end_date) as end_date FROM band_previous_name a INNER JOIN band b ON b.id = a.band_id WHERE UPPER(a.old_name) = ?1";

    private static final String BAND_FIND_BY_CONTAINS_NAME_SQL = "SELECT b.name, b.slug, b.start_date, b.end_date FROM band b WHERE UPPER(b.name) LIKE ?1";
    private static final String BAND_ALIAS_FIND_BY_CONTAINS_NAME_SQL = "SELECT a.old_name as old_name, b.slug as slug, COALESCE(a.start_date, b.start_date) as start_date, COALESCE(a.end_date, b.end_date) as end_date FROM band_previous_name a INNER JOIN band b ON b.id = a.band_id WHERE UPPER(a.old_name) LIKE ?1";


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

    private static final String PERSON_FIND_BY_EXACT_COMBINED_NAME_SQL = "SELECT p.combined_name, p.slug, p.start_date, p.end_date FROM person p WHERE UPPER(p.combined_name) = ?1";
    private static final String PERSON_ALIAS_FIND_BY_EXACT_NAME_SQL = "SELECT a.name as old_name, p.slug as slug, p.start_date as start_date, p.end_date as end_date FROM person_alias a INNER JOIN person p ON p.id = a.person_id WHERE UPPER(a.name) = ?1";


    public static List<FinderSqlDto> personFetchByCombinedNameUpper(EntityManager entityManager, String combinedNameUpper) {
        return SqlExec.execute(entityManager, PERSON_FIND_BY_EXACT_COMBINED_NAME_SQL, combinedNameUpper, FinderSqlDto.class);
    }

    public static List<FinderSqlDto> personAliasFetchByUpperName(EntityManager entityManager, String combinedNameUpper) {
        return SqlExec.execute(entityManager, PERSON_ALIAS_FIND_BY_EXACT_NAME_SQL, combinedNameUpper, FinderSqlDto.class);
    }

    private static final String PERSON_FIND_BY_INITIAL_AND_SURNAME_SQL = "SELECT p.combined_name, p.slug, p.start_date, p.end_date FROM person p WHERE UPPER(p.first_names) LIKE ?1 AND UPPER(p.surname) = ?2";

    public static List<FinderSqlDto> personFetchByInitialAndSurname(EntityManager entityManager, String initialUpper, String surnameUpper) {
        return SqlExec.execute(entityManager, PERSON_FIND_BY_INITIAL_AND_SURNAME_SQL, initialUpper + "%", surnameUpper, FinderSqlDto.class);
    }

    private static final String PERSON_FIND_CONDUCTORS_FOR_BAND_SQL = """
        SELECT p.combined_name, p.slug, p.start_date, p.end_date
        FROM person p
        WHERE p.id IN
          (SELECT DISTINCT r.conductor_id
           FROM contest_result r
           INNER JOIN band b ON b.id = r.band_id
           WHERE b.slug = ?1)
         AND UPPER(p.combined_name) = ?2""";

    public static List<FinderSqlDto> fetchBandConductors(EntityManager entityManager, String bandSlug, String conductorNameUpper) {
        return SqlExec.execute(entityManager, PERSON_FIND_CONDUCTORS_FOR_BAND_SQL, bandSlug, conductorNameUpper, FinderSqlDto.class);
    }
}
