package uk.co.bbr.services.contests.sql;

import jakarta.persistence.EntityManager;
import uk.co.bbr.services.contests.sql.dto.ContestUpDownSqlDto;
import uk.co.bbr.services.framework.sql.SqlExec;

import java.util.List;

public class ContestSql {

    private static final String CONTEST_UP_SQL = """
            SELECT TOP 1 c.name, c.slug
                FROM contest c
                         INNER JOIN contest_group g ON g.id = c.contest_group_id
                WHERE g.slug = ?1
                  AND c.ordering < ?2
                ORDER BY c.ordering DESC""";

    public static ContestUpDownSqlDto selectLinkedUpContest(EntityManager entityManager, String groupSlug, Integer ordering) {
        List<ContestUpDownSqlDto> returnValue = SqlExec.execute(entityManager, CONTEST_UP_SQL, groupSlug, ordering, ContestUpDownSqlDto.class);
        if (returnValue.isEmpty()) {
            return null;
        }
        return returnValue.get(0);
    }

    private static final String CONTEST_DOWN_SQL = """
            SELECT TOP 1 c.name, c.slug
                FROM contest c
                         INNER JOIN contest_group g ON g.id = c.contest_group_id
                WHERE g.slug = ?1
                  AND c.ordering > ?2
                ORDER BY c.ordering ASC""";

    public static ContestUpDownSqlDto selectLinkedDownContest(EntityManager entityManager, String groupSlug, Integer ordering) {
        List<ContestUpDownSqlDto> returnValue = SqlExec.execute(entityManager, CONTEST_DOWN_SQL, groupSlug, ordering, ContestUpDownSqlDto.class);
        if (returnValue.isEmpty()) {
            return null;
        }
        return returnValue.get(0);
    }
}
