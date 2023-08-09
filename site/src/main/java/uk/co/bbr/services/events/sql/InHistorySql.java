package uk.co.bbr.services.events.sql;

import uk.co.bbr.services.events.sql.dto.EventResultSqlDto;
import uk.co.bbr.services.events.sql.dto.EventUpDownLeftRightSqlDto;
import uk.co.bbr.services.events.sql.dto.HistoricalEventSqlDto;
import uk.co.bbr.services.events.sql.dto.ResultPieceSqlDto;
import uk.co.bbr.services.framework.sql.SqlExec;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

public class InHistorySql {

    private static final String RANDOM_HISTORICAL_EVENT_FOR_TODAY_SQL = """
        SELECT TOP 1 e.name as event_name, c.slug as contest_slug, e.date_of_event, r.band_name as competed_as, b.slug as band_slug, b.name as band_name, reg.name as region_name, reg.slug as region_slug, reg.country_code
        FROM contest c
               INNER JOIN contest_event e ON e.contest_id = c.id
               INNER JOIN contest_result r ON r.contest_event_id = e.id
               INNER JOIN band b ON r.band_id = b.id
               LEFT OUTER JOIN region reg ON reg.id = b.region_id
        WHERE DAY(e.date_of_event) = DAY(GETDATE())
        AND MONTH(e.date_of_event) = MONTH (GETDATE())
        AND e.date_resolution = 'D'
        AND e.date_of_event < (GETDATE() - 400)
        AND r.result_position = 1
        AND r.result_position_type = 'R'
        ORDER BY NEWID()""";

    public static List<HistoricalEventSqlDto> eventsForToday(EntityManager entityManager) {
        return SqlExec.execute(entityManager, RANDOM_HISTORICAL_EVENT_FOR_TODAY_SQL, HistoricalEventSqlDto.class);
    }

    private static final String RANDOM_HISTORICAL_EVENT_FOR_THIS_WEEK_SQL = """
        SELECT TOP 1 e.name as event_name, c.slug as contest_slug, e.date_of_event, r.band_name as competed_as, b.slug as band_slug, b.name as band_name, reg.name as region_name, reg.slug as region_slug, reg.country_code
        FROM contest c
                 INNER JOIN contest_event e ON e.contest_id = c.id
                 INNER JOIN contest_result r ON r.contest_event_id = e.id
                 INNER JOIN band b ON r.band_id = b.id
                 LEFT OUTER JOIN region reg ON reg.id = b.region_id
        WHERE DATEPART(week, e.date_of_event) = DATEPART(week, GETDATE())
          AND e.date_resolution = 'D'
          AND e.date_of_event < (GETDATE() - 400)
          AND r.result_position = 1
          AND r.result_position_type = 'R'
        ORDER BY NEWID()""";

    public static List<HistoricalEventSqlDto> eventsForThisWeek(EntityManager entityManager) {
        return SqlExec.execute(entityManager, RANDOM_HISTORICAL_EVENT_FOR_THIS_WEEK_SQL, HistoricalEventSqlDto.class);
    }
}
