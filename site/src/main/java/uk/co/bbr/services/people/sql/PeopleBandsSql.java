package uk.co.bbr.services.people.sql;

import lombok.experimental.UtilityClass;
import uk.co.bbr.services.framework.sql.SqlExec;
import uk.co.bbr.services.people.sql.dto.PeopleBandsSqlDto;
import uk.co.bbr.services.people.sql.dto.PeopleWinnersSqlDto;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

@UtilityClass
public class PeopleBandsSql {

    private static final String BANDS_CONDUCTED_SQL = """
            WITH bands AS (
                SELECT r.conductor_id, count(distinct r.band_id) as band_count
                FROM contest_result r
                GROUP BY r.conductor_id
              )
              SELECT p.slug, p.surname, p.first_names, p.known_for, b.band_count
              FROM person p
              INNER JOIN bands b ON b.conductor_id = p.id
              WHERE b.band_count > 4
              AND p.slug != 'unknown'
              ORDER BY 5 desc""";
    public static List<PeopleBandsSqlDto> selectWinningPeople(EntityManager entityManager) {
        return SqlExec.execute(entityManager, BANDS_CONDUCTED_SQL, PeopleBandsSqlDto.class);
    }

    private static final String BANDS_CONDUCTED_BEFORE_SQL = """
            WITH bands AS (
                SELECT r.conductor_id, count(distinct r.band_id) as band_count
                FROM contest_result r
                INNER JOIN contest_event e ON e.id = r.contest_event_id
                WHERE e.date_of_event < ?1
                GROUP BY r.conductor_id
              )
              SELECT p.slug, p.surname, p.first_names, p.known_for, b.band_count
              FROM person p
              INNER JOIN bands b ON b.conductor_id = p.id
              WHERE b.band_count > 4
              AND p.slug != 'unknown'
              ORDER BY 5 DESC""";
    public static List<PeopleBandsSqlDto> selectWinningPeopleBefore(EntityManager entityManager, int year) {
        return SqlExec.execute(entityManager, BANDS_CONDUCTED_BEFORE_SQL, LocalDate.of(year, 1, 1), PeopleBandsSqlDto.class);
    }

    private static final String BANDS_CONDUCTED_AFTER_SQL = """
            WITH bands AS (
                SELECT r.conductor_id, count(distinct r.band_id) as band_count
                FROM contest_result r
                INNER JOIN contest_event e ON e.id = r.contest_event_id
                WHERE e.date_of_event > ?1
                GROUP BY r.conductor_id
              )
              SELECT p.slug, p.surname, p.first_names, p.known_for, b.band_count
              FROM person p
              INNER JOIN bands b ON b.conductor_id = p.id
              WHERE b.band_count > 4
              AND p.slug != 'unknown'
              ORDER BY 5 DESC""";
    public static List<PeopleBandsSqlDto> selectWinningPeopleAfter(EntityManager entityManager, int year) {
        return SqlExec.execute(entityManager, BANDS_CONDUCTED_AFTER_SQL, LocalDate.of(year-1, 12, 31), PeopleBandsSqlDto.class);
    }

}

