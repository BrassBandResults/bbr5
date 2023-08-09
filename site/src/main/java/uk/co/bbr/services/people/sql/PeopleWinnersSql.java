package uk.co.bbr.services.people.sql;

import lombok.experimental.UtilityClass;
import uk.co.bbr.services.framework.sql.SqlExec;
import uk.co.bbr.services.people.sql.dto.PeopleWinnersSqlDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public class PeopleWinnersSql {

    private static final String WINNING_PEOPLE_SQL = """
            WITH
             winners AS
              (SELECT conductor_id, count(*) as winners
               FROM contest_result r
               INNER JOIN contest_event e ON e.id = r.contest_event_id
               INNER JOIN contest c ON c.id = e.contest_id
               INNER JOIN person p ON p.id = r.conductor_id
               WHERE r.result_position = 1
               AND r.result_position_type = 'R'
               AND c.name NOT LIKE '%Whit Friday%'
               AND conductor_id IS NOT NULL
               AND p.slug != 'unknown'
               GROUP BY conductor_id),
             total AS
              (SELECT conductor_id, count(*) as contests
               FROM contest_result r
               INNER JOIN contest_event e ON e.id = r.contest_event_id
               INNER JOIN contest c ON c.id = e.contest_id
               INNER JOIN person p ON p.id = r.conductor_id
               AND r.result_position_type = 'R'
               AND c.name NOT LIKE '%Whit Friday%'
               AND p.slug != 'unknown'
               GROUP BY conductor_id)
           SELECT p.slug, p.surname, p.first_names, p.known_for, w.winners, t.contests
           FROM person p
           INNER JOIN winners w ON p.id = w.conductor_id
           INNER JOIN total t ON p.id = t.conductor_id
           ORDER BY 5 desc""";
    public static List<PeopleWinnersSqlDto> selectWinningPeople(EntityManager entityManager) {
        return SqlExec.execute(entityManager, WINNING_PEOPLE_SQL, PeopleWinnersSqlDto.class);
    }

    private static final String WINNING_PEOPLE_BEFORE_SQL = """
            WITH
             winners AS
              (SELECT conductor_id, count(*) as winners
               FROM contest_result r
               INNER JOIN contest_event e ON e.id = r.contest_event_id
               INNER JOIN contest c ON c.id = e.contest_id
               INNER JOIN person p ON p.id = r.conductor_id
               WHERE r.result_position = 1
               AND r.result_position_type = 'R'
               AND c.name NOT LIKE '%Whit Friday%'
               AND conductor_id IS NOT NULL
               AND p.slug != 'unknown'
               AND e.date_of_event < ?1
               GROUP BY conductor_id),
             total AS
              (SELECT conductor_id, count(*) as contests
               FROM contest_result r
               INNER JOIN contest_event e ON e.id = r.contest_event_id
               INNER JOIN contest c ON c.id = e.contest_id
               INNER JOIN person p ON p.id = r.conductor_id
               AND r.result_position_type = 'R'
               AND c.name NOT LIKE '%Whit Friday%'
               AND p.slug != 'unknown'
               AND e.date_of_event < ?1
               GROUP BY conductor_id)
           SELECT p.slug, p.surname, p.first_names, p.known_for, w.winners, t.contests
           FROM person p
           INNER JOIN winners w ON p.id = w.conductor_id
           INNER JOIN total t ON p.id = t.conductor_id
           ORDER BY 5 desc""";
    public static List<PeopleWinnersSqlDto> selectWinningPeopleBefore(EntityManager entityManager, int year) {
        return SqlExec.execute(entityManager, WINNING_PEOPLE_BEFORE_SQL, LocalDate.of(year, 1, 1), PeopleWinnersSqlDto.class);
    }

    private static final String WINNING_PEOPLE_AFTER_SQL = """
            WITH
             winners AS
              (SELECT conductor_id, count(*) as winners
               FROM contest_result r
               INNER JOIN contest_event e ON e.id = r.contest_event_id
               INNER JOIN contest c ON c.id = e.contest_id
               INNER JOIN person p ON p.id = r.conductor_id
               WHERE r.result_position = 1
               AND r.result_position_type = 'R'
               AND c.name NOT LIKE '%Whit Friday%'
               AND conductor_id IS NOT NULL
               AND p.slug != 'unknown'
               AND e.date_of_event > ?1
               GROUP BY conductor_id),
             total AS
              (SELECT conductor_id, count(*) as contests
               FROM contest_result r
               INNER JOIN contest_event e ON e.id = r.contest_event_id
               INNER JOIN contest c ON c.id = e.contest_id
               INNER JOIN person p ON p.id = r.conductor_id
               AND r.result_position_type = 'R'
               AND c.name NOT LIKE '%Whit Friday%'
               AND p.slug != 'unknown'
               AND e.date_of_event > ?1
               GROUP BY conductor_id)
           SELECT p.slug, p.surname, p.first_names, p.known_for, w.winners, t.contests
           FROM person p
           INNER JOIN winners w ON p.id = w.conductor_id
           INNER JOIN total t ON p.id = t.conductor_id
           ORDER BY 5 desc""";
    public static List<PeopleWinnersSqlDto> selectWinningPeopleAfter(EntityManager entityManager, int year) {
        return SqlExec.execute(entityManager, WINNING_PEOPLE_AFTER_SQL, LocalDate.of(year-1, 12, 31), PeopleWinnersSqlDto.class);
    }

}

