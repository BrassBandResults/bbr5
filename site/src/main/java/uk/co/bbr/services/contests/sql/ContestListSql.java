package uk.co.bbr.services.contests.sql;

import lombok.experimental.UtilityClass;
import uk.co.bbr.services.contests.sql.dto.ContestListSqlDto;
import uk.co.bbr.services.framework.sql.SqlExec;

import jakarta.persistence.EntityManager;
import java.util.List;

@UtilityClass
public class ContestListSql {
    private static final String CONTEST_LIST_ALL = """
        WITH
           event_counts_contest AS (
               SELECT e.contest_id as contest_id, count(*) as event_count
               FROM contest_event e
                        INNER JOIN contest c ON c.id = e.contest_id
               WHERE c.contest_group_id IS NULL
               GROUP BY e.contest_id
           ),
           event_counts_group AS (
               SELECT c.contest_group_id as contest_group_id, count(*) as event_count
               FROM contest_event e
                        INNER JOIN contest c ON c.id = e.contest_id
                        INNER JOIN contest_group g ON g.id = c.contest_group_id
               GROUP BY c.contest_group_id
           )
       SELECT c.name as contest_name, c.slug as contest_slug, ec.event_count
       FROM contest c
                LEFT OUTER JOIN event_counts_contest ec ON ec.contest_id = c.id
       WHERE c.contest_group_id IS NULL
       UNION
       SELECT a.name as contest_name, c.slug as contest_slug, ec.event_count
       FROM contest_alias a
       INNER JOIN contest c ON c.id = a.contest_id
       LEFT OUTER JOIN event_counts_contest ec ON ec.contest_id = c.id
       WHERE c.contest_group_id IS NULL
       UNION
       SELECT g.name as group_name, UPPER(g.slug) as group_slug, ec.event_count
       FROM contest_group g
       LEFT OUTER JOIN event_counts_group ec ON ec.contest_group_id = g.id
       UNION
       SELECT a.name as group_alias_name, UPPER(g.slug) as group_slug, ec.event_count
       FROM contest_group_alias a
       INNER JOIN contest_group g ON g.id = a.contest_group_id
       LEFT OUTER JOIN event_counts_group ec ON ec.contest_group_id = g.id
       ORDER BY 1""";

    public static List<ContestListSqlDto> listAllForContestList(EntityManager entityManager) {
        return SqlExec.execute(entityManager, CONTEST_LIST_ALL, ContestListSqlDto.class);
    }


    private static final String CONTEST_LIST_FOR_PREFIX = """
        WITH
           event_counts_contest AS (
               SELECT e.contest_id as contest_id, count(*) as event_count
               FROM contest_event e
                        INNER JOIN contest c ON c.id = e.contest_id
               WHERE c.contest_group_id IS NULL
               AND c.name LIKE ?1
               GROUP BY e.contest_id
           ),
           event_counts_group AS (
               SELECT c.contest_group_id as contest_group_id, count(*) as event_count
               FROM contest_event e
                        INNER JOIN contest c ON c.id = e.contest_id
                        INNER JOIN contest_group g ON g.id = c.contest_group_id
               WHERE g.name LIKE ?1
               GROUP BY c.contest_group_id
           )
       SELECT c.name as contest_name, c.slug as contest_slug, ec.event_count
       FROM contest c
                LEFT OUTER JOIN event_counts_contest ec ON ec.contest_id = c.id
       WHERE c.contest_group_id IS NULL
       AND c.name LIKE ?1
       UNION
       SELECT a.name as contest_name, c.slug as contest_slug, ec.event_count
       FROM contest_alias a
       INNER JOIN contest c ON c.id = a.contest_id
       LEFT OUTER JOIN event_counts_contest ec ON ec.contest_id = c.id
       WHERE c.contest_group_id IS NULL
       AND a.name LIKE ?1
       UNION
       SELECT g.name as group_name, UPPER(g.slug) as group_slug, ec.event_count
       FROM contest_group g
       LEFT OUTER JOIN event_counts_group ec ON ec.contest_group_id = g.id
       WHERE g.name LIKE ?1
       UNION
       SELECT a.name as group_alias_name, UPPER(g.slug) as group_slug, ec.event_count
       FROM contest_group_alias a
       INNER JOIN contest_group g ON g.id = a.contest_group_id
       LEFT OUTER JOIN event_counts_group ec ON ec.contest_group_id = g.id
       WHERE g.name LIKE ?1
       ORDER BY 1""";

    public static List<ContestListSqlDto> listByPrefixForContestList(EntityManager entityManager, String prefix) {
        return SqlExec.execute(entityManager, CONTEST_LIST_FOR_PREFIX, prefix + '%', ContestListSqlDto.class);
    }
}

