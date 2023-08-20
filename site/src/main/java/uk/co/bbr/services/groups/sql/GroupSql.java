package uk.co.bbr.services.groups.sql;

import jakarta.persistence.EntityManager;
import lombok.experimental.UtilityClass;
import uk.co.bbr.services.framework.sql.SqlExec;
import uk.co.bbr.services.groups.sql.dao.ContestListSqlDto;
import uk.co.bbr.services.groups.sql.dao.GroupListSqlDto;

import java.util.List;

@UtilityClass
public class GroupSql {
    private static final String GROUP_LIST_BY_INITIAL_LETTER_SQL = """
        WITH
            event_counts AS (
                SELECT c.contest_group_id as contest_id, count(*) as event_count
                FROM contest_event e
                         INNER JOIN contest c ON c.id = e.contest_id
                         INNER JOIN contest_group g ON g.id = c.contest_group_id
                WHERE UPPER(g.name) LIKE ?1
                GROUP BY c.contest_group_id
            ),
            contest_counts AS (
                SELECT c.contest_group_id as group_id, count(*) as contest_count
                FROM contest c
                         INNER JOIN contest_group g ON g.id = c.contest_group_id
                WHERE UPPER(g.name) LIKE ?1
                GROUP BY c.contest_group_id
            )
            SELECT g.name as group_name, g.slug as group_slug, ec.event_count, cc.contest_count
                FROM contest_group g
                LEFT OUTER JOIN event_counts ec ON ec.contest_id = g.id
                LEFT OUTER JOIN contest_counts cc ON cc.group_id = g.id
                WHERE UPPER(g.name) LIKE ?1
                ORDER BY g.name""";

    public static List<GroupListSqlDto> findByPrefixForList(EntityManager entityManager, String letter) {
        return SqlExec.execute(entityManager, GROUP_LIST_BY_INITIAL_LETTER_SQL, letter + "%", GroupListSqlDto.class);
    }

    private static final String GROUP_LIST_ALL_SQL = """
        WITH
            event_counts AS (
                SELECT c.contest_group_id as contest_id, count(*) as event_count
                FROM contest_event e
                         INNER JOIN contest c ON c.id = e.contest_id
                         INNER JOIN contest_group g ON g.id = c.contest_group_id
                GROUP BY c.contest_group_id
            ),
            contest_counts AS (
                SELECT c.contest_group_id as group_id, count(*) as contest_count
                FROM contest c
                         INNER JOIN contest_group g ON g.id = c.contest_group_id
                GROUP BY c.contest_group_id
            )
            SELECT g.name as group_name, g.slug as group_slug, ec.event_count, cc.contest_count
                FROM contest_group g
                LEFT OUTER JOIN event_counts ec ON ec.contest_id = g.id
                LEFT OUTER JOIN contest_counts cc ON cc.group_id = g.id
                ORDER BY g.name""";

    public static List<GroupListSqlDto> findAllForList(EntityManager entityManager) {
        return SqlExec.execute(entityManager, GROUP_LIST_ALL_SQL, GroupListSqlDto.class);
    }

    private static final String CONTESTS_FOR_GROUP = """
        WITH result_counts AS (
            SELECT e.contest_id as contest_id, count(*) as event_count
            FROM contest_event e
                     INNER JOIN contest c ON c.id = e.contest_id
                     INNER JOIN contest_group g ON g.id = c.contest_group_id
            WHERE g.slug = ?1
            GROUP BY e.contest_id
            )
        SELECT c.name as contest_name, c.slug as contest_slug, c.extinct as extinct, counts.event_count
        FROM contest c
        INNER JOIN contest_group g ON g.id = c.contest_group_id
        LEFT OUTER JOIN result_counts counts ON counts.contest_id = c.id
        WHERE g.slug = ?1
        ORDER BY c.name""";

    public static List<ContestListSqlDto> contestsForGroup(EntityManager entityManager, String groupSlug) {
        return SqlExec.execute(entityManager, CONTESTS_FOR_GROUP, groupSlug, ContestListSqlDto.class);
    }

}

