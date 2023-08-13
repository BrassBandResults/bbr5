package uk.co.bbr.services.people.sql;

import lombok.experimental.UtilityClass;
import uk.co.bbr.services.bands.sql.dto.BandListSqlDto;
import uk.co.bbr.services.framework.sql.SqlExec;
import uk.co.bbr.services.people.sql.dto.PeopleListSqlDto;
import uk.co.bbr.services.people.sql.dto.PeopleWinnersSqlDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public class PeopleCountSql {

    private static final String PERSON_CONDUCTOR_COUNT_SQL = "SELECT conductor_id, count(*) FROM contest_result WHERE conductor_id IS NOT NULL AND result_position_type <> 'D' AND result_position_type <> 'W' GROUP BY conductor_id";
    private static final String PERSON_CONDUCTOR_2_COUNT_SQL = "SELECT conductor_two_id, count(*) FROM contest_result WHERE conductor_two_id IS NOT NULL AND result_position_type <> 'D' AND result_position_type <> 'W' GROUP BY conductor_two_id";
    private static final String PERSON_CONDUCTOR_3_COUNT_SQL = "SELECT conductor_three_id, count(*) FROM contest_result WHERE conductor_three_id IS NOT NULL AND result_position_type <> 'D' AND result_position_type <> 'W' GROUP BY conductor_three_id";
    private static final String PERSON_ADJUDICATOR_COUNT_SQL = "SELECT person_id, count(*) FROM contest_event_adjudicator WHERE person_id IS NOT NULL GROUP BY person_id";
    private static final String PERSON_COMPOSER_COUNT_SQL = "SELECT composer_id, count(*) FROM piece WHERE composer_id IS NOT NULL GROUP BY composer_id";
    private static final String PERSON_ARRANGER_COUNT_SQL = "SELECT arranger_id, count(*) FROM piece WHERE arranger_id IS NOT NULL GROUP BY arranger_id";

    public static Map<Long, Integer> selectConductorCounts(EntityManager entityManager) {
        return PeopleCountSql.selectCounts(entityManager, PERSON_CONDUCTOR_COUNT_SQL);
    }
    public static Map<Long, Integer> selectConductorTwoCounts(EntityManager entityManager) {
        return PeopleCountSql.selectCounts(entityManager, PERSON_CONDUCTOR_2_COUNT_SQL);
    }
    public static Map<Long, Integer> selectConductorThreeCounts(EntityManager entityManager) {
        return PeopleCountSql.selectCounts(entityManager, PERSON_CONDUCTOR_3_COUNT_SQL);
    }

    public static Map<Long, Integer> selectAdjudicatorCounts(EntityManager entityManager) {
        return PeopleCountSql.selectCounts(entityManager, PERSON_ADJUDICATOR_COUNT_SQL);
    }
    public static Map<Long, Integer> selectComposerCounts(EntityManager entityManager) {
        return PeopleCountSql.selectCounts(entityManager, PERSON_COMPOSER_COUNT_SQL);
    }
    public static Map<Long, Integer> selectArrangerCounts(EntityManager entityManager) {
        return PeopleCountSql.selectCounts(entityManager, PERSON_ARRANGER_COUNT_SQL);
    }

    private static Map<Long, Integer> selectCounts(EntityManager entityManager, String sql) {
        Map<Long, Integer>  returnData = new HashMap<>();
        try {
            Query query = entityManager.createNativeQuery(sql);
            List<Object[]> queryResults = query.getResultList();

            for (Object[] columnList : queryResults) {
                Long id = columnList[0] instanceof BigInteger ? ((BigInteger)columnList[0]).longValue() : (Integer)columnList[0];
                int count = columnList[1] instanceof BigInteger ? ((BigInteger)columnList[1]).intValue() : (Integer)columnList[1];
                returnData.put(id, count);
            }

            return returnData;
        } catch (Exception e) {
            throw new RuntimeException("SQL Failure, " + e.getMessage());
        }
    }


    private static final String PEOPLE_LIST_BY_INITIAL_LETTER_SQL = """
        WITH
        conductor_one_count AS (
            SELECT result.conductor_id as conductor_id, count(*) as result_count_1
            FROM contest_result result
            INNER JOIN person p ON p.id = result.conductor_id
            WHERE UPPER(p.surname) LIKE ?1
            GROUP BY result.conductor_id
        ),
        conductor_two_count AS (
            SELECT result.conductor_two_id as conductor_id, count(*) as result_count_2
            FROM contest_result result
            INNER JOIN person p ON p.id = result.conductor_two_id
            WHERE UPPER(p.surname) LIKE ?1
            GROUP BY result.conductor_two_id
        ),
        conductor_three_count AS (
            SELECT result.conductor_three_id as conductor_id, count(*) as result_count_3
            FROM contest_result result
            INNER JOIN person p ON p.id = result.conductor_three_id
            WHERE UPPER(p.surname) LIKE ?1
            GROUP BY result.conductor_three_id
        ),
        adjudicator_count AS (
            SELECT a.person_id as adjudicator_id, count(*) as adjudication_count
            FROM contest_event_adjudicator a
            INNER JOIN person p ON p.id = a.person_id
            WHERE UPPER (p.surname) LIKE ?1
            GROUP BY a.person_id
        ),
        composition_count AS (
            SELECT m.composer_id as composer_id, count(*) as composer_count
            FROM piece m
            INNER JOIN person p ON p.id = m.composer_id
            WHERE UPPER (p.surname) LIKE ?1
            GROUP BY m.composer_id
        ),
        arranger_count AS (
            SELECT m.arranger_id as arranger_id, count(*) as arranger_count
            FROM piece m
            INNER JOIN person p ON p.id = m.arranger_id
            WHERE UPPER (p.surname) LIKE ?1
            GROUP BY m.arranger_id
        )
        SELECT p.surname as surname, p.first_names as first_names, p.slug as person_slug, p.suffix as suffix, p.known_for as known_for,
        c1.result_count_1, c2.result_count_2, c3.result_count_3,
        a.adjudication_count,
        mc.composer_count, ma.arranger_count
        FROM person p
        LEFT OUTER JOIN conductor_one_count c1 ON c1.conductor_id = p.id
        LEFT OUTER JOIN conductor_two_count c2 ON c2.conductor_id = p.id
        LEFT OUTER JOIN conductor_three_count c3 ON c3.conductor_id = p.id
        LEFT OUTER JOIN adjudicator_count a ON a.adjudicator_id = p.id
        LEFT OUTER JOIN composition_count mc ON mc.composer_id = p.id
        LEFT OUTER JOIN arranger_count ma ON ma.arranger_id = p.id
        WHERE UPPER(p.surname) LIKE ?1
        ORDER BY p.surname, p.first_names, p.suffix""";
    public static List<PeopleListSqlDto> selectPeopleWhereSurnameStartsWithLetterForList(EntityManager entityManager, String letter) {
        return SqlExec.execute(entityManager, PEOPLE_LIST_BY_INITIAL_LETTER_SQL, letter + "%", PeopleListSqlDto.class);
    }
}

