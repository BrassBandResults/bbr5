package uk.co.bbr.services.events.sql;

import uk.co.bbr.services.events.sql.dto.EventUpDownLeftRightSqlDto;
import uk.co.bbr.services.framework.sql.SqlExec;

import javax.persistence.EntityManager;
import java.util.List;

public class EventSql {

    private static final String EVENT_UP_SQL = """
            SELECT TOP 1 c.slug, e.name, e.date_of_event
                FROM contest_event e
                         INNER JOIN contest c ON c.id = e.contest_id
                         INNER JOIN contest_group g ON g.id = c.contest_group_id
                WHERE g.slug = ?1
                  AND c.ordering < ?2
                  AND YEAR(e.date_of_event) = ?3
                ORDER BY c.ordering DESC""";

    public static EventUpDownLeftRightSqlDto selectLinkedUpEvent(EntityManager entityManager, String groupSlug, Integer ordering, Integer year) {
        List<EventUpDownLeftRightSqlDto> returnValue = SqlExec.execute(entityManager, EVENT_UP_SQL, groupSlug, ordering, year, EventUpDownLeftRightSqlDto.class);
        if (returnValue.isEmpty()) {
            return null;
        }
        return returnValue.get(0);
    }

    private static final String EVENT_DOWN_SQL = """
            SELECT TOP 1 c.slug, e.name, e.date_of_event
                  FROM contest_event e
                           INNER JOIN contest c ON c.id = e.contest_id
                           INNER JOIN contest_group g ON g.id = c.contest_group_id
                  WHERE g.slug = ?1
                    AND c.ordering > ?2
                    AND YEAR(e.date_of_event) = ?3
                  ORDER BY c.ordering""";

    public static EventUpDownLeftRightSqlDto selectLinkedDownEvent(EntityManager entityManager, String groupSlug, Integer ordering, Integer year) {
        List<EventUpDownLeftRightSqlDto> returnValue = SqlExec.execute(entityManager, EVENT_DOWN_SQL, groupSlug, ordering, year, EventUpDownLeftRightSqlDto.class);
        if (returnValue.isEmpty()) {
            return null;
        }
        return returnValue.get(0);
    }

    private static final String EVENT_PREVIOUS_SQL = """
            SELECT TOP 1 c.slug, e.name, e.date_of_event
                      FROM contest_event e
                               INNER JOIN contest c ON c.id = e.contest_id
                               INNER JOIN contest_group g ON g.id = c.contest_group_id
                      WHERE g.slug = ?1
                        AND c.ordering = ?2
                        AND YEAR(e.date_of_event) < ?3
                      ORDER BY e.date_of_event DESC""";

    public static EventUpDownLeftRightSqlDto selectLinkedPreviousEvent(EntityManager entityManager, String groupSlug, Integer ordering, Integer year) {
        List<EventUpDownLeftRightSqlDto> returnValue = SqlExec.execute(entityManager, EVENT_PREVIOUS_SQL, groupSlug, ordering, year, EventUpDownLeftRightSqlDto.class);
        if (returnValue.isEmpty()) {
            return null;
        }
        return returnValue.get(0);
    }

    private static final String EVENT_NEXT_SQL = """
            SELECT TOP 1 c.slug, e.name, e.date_of_event
                  FROM contest_event e
                           INNER JOIN contest c ON c.id = e.contest_id
                           INNER JOIN contest_group g ON g.id = c.contest_group_id
                  WHERE g.slug = ?1
                    AND c.ordering = ?2
                    AND YEAR(e.date_of_event) > ?3
                  ORDER BY e.date_of_event""";

    public static EventUpDownLeftRightSqlDto selectLinkedNextEvent(EntityManager entityManager, String groupSlug, Integer ordering, Integer year) {
        List<EventUpDownLeftRightSqlDto> returnValue = SqlExec.execute(entityManager, EVENT_NEXT_SQL, groupSlug, ordering, year, EventUpDownLeftRightSqlDto.class);
        if (returnValue.isEmpty()) {
            return null;
        }
        return returnValue.get(0);
    }
}
