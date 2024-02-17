package uk.co.bbr.services.years.sql;

import lombok.experimental.UtilityClass;
import uk.co.bbr.services.framework.sql.SqlExec;
import uk.co.bbr.services.years.sql.dto.ContestsForYearEventSqlDto;
import uk.co.bbr.services.years.sql.dto.YearListEntrySqlDto;

import jakarta.persistence.EntityManager;
import java.util.List;

@UtilityClass
public class YearSql {

    private static final String YEAR_LIST_SQL = """
WITH events AS (SELECT YEAR(e.date_of_event) as event_year, count(DISTINCT(e.id)) as event_count
                FROM contest_event e
                WHERE e.no_contest = 0
                GROUP BY YEAR(e.date_of_event)),
     bands AS (SELECT YEAR(e.date_of_event) as event_year, COUNT(DISTINCT(r.band_id)) as band_count
                FROM contest_result r
                INNER JOIN contest_event e ON e.id = r.contest_event_id
                WHERE e.no_contest = 0
                GROUP BY YEAR(e.date_of_event))
SELECT e.event_year, b.band_count, e.event_count
FROM events e
INNER JOIN bands b ON b.event_year = e.event_year
ORDER BY 1 DESC
            """;

    public static List<YearListEntrySqlDto> selectYearsPageData(EntityManager entityManager) {
        return SqlExec.execute(entityManager, YEAR_LIST_SQL, YearListEntrySqlDto.class);
    }

    private static final String CONTEST_EVENTS_LIST_FOR_YEAR_SQL = """
            SELECT e.date_of_event, e.date_resolution, c.slug as contest_slug, c.name as contest_name, e.no_contest, r.band_name as band_competed_as, b.slug as band_slug, b.name as band_name, reg.country_code,
                    con1.slug as c1_slug, con1.first_names as c1_first_names, con1.surname as c1_surname,
                    con2.slug as c2_slug, con2.first_names as c2_first_names, con2.surname as c2_surname,
                    con3.slug as c3_slug, con3.first_names as c3_first_names, con3.surname as c3_surname
            FROM contest_event e
                INNER JOIN contest c ON c.id = e.contest_id
                LEFT OUTER JOIN contest_result r ON r.contest_event_id = e.id AND r.result_position = 1 AND r.result_position_type = 'R'
                LEFT OUTER JOIN band b ON b.id = r.band_id
                LEFT OUTER JOIN region reg ON reg.id = b.region_id
                LEFT OUTER JOIN person con1 ON con1.id = r.conductor_id
                LEFT OUTER JOIN person con2 ON con2.id = r.conductor_two_id
                LEFT OUTER JOIN person con3 ON con3.id = r.conductor_three_id
            WHERE YEAR(e.date_of_event) = ?1
            ORDER BY e.date_of_event ASC, c.name ASC""";

    public static List<ContestsForYearEventSqlDto> selectEventsForYear(EntityManager entityManager, String year) {
        return SqlExec.execute(entityManager, CONTEST_EVENTS_LIST_FOR_YEAR_SQL, year, ContestsForYearEventSqlDto.class);
    }
}
