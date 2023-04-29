package uk.co.bbr.services.contests.sql;

import uk.co.bbr.services.contests.sql.dto.ContestWinsSqlDto;
import uk.co.bbr.services.contests.sql.dto.BandResultSqlDto;
import uk.co.bbr.services.contests.sql.dto.ContestEventResultSqlDto;
import uk.co.bbr.services.contests.sql.dto.ContestResultPieceSqlDto;
import uk.co.bbr.services.contests.sql.dto.EventPieceSqlDto;
import uk.co.bbr.services.contests.sql.dto.PersonConductingResultSqlDto;
import uk.co.bbr.services.contests.sql.dto.ResultPieceSqlDto;
import uk.co.bbr.services.framework.sql.SqlExec;

import javax.persistence.EntityManager;
import java.util.List;

public class ContestResultSql {

    private static final String BAND_RESULT_LIST_SQL = """
        SELECT r.id as result_id, e.date_of_event, e.date_resolution, c.slug as contest_slug, c.name, r.result_position, r.result_position_type, r.result_award, r.band_name, r.draw, e.id as event_id, g.slug as group_slug, g.name as group_name,
        con1.slug as c1_slug, con1.first_names as c1_first_names, con1.surname as c1_surname,
        con2.slug as c2_slug, con2.first_names as c2_first_names, con2.surname as c2_surname,
        con3.slug as c3_slug, con3.first_names as c3_first_names, con3.surname as c3_surname
        FROM contest_result r
        INNER JOIN contest_event e ON e.id = r.contest_event_id
        INNER JOIN contest c ON c.id = e.contest_id
        LEFT OUTER JOIN contest_group g ON g.id = c.contest_group_id
        LEFT OUTER JOIN person con1 ON con1.id = r.conductor_id
        LEFT OUTER JOIN person con2 ON con2.id = r.conductor_two_id
        LEFT OUTER JOIN person con3 ON con3.id = r.conductor_three_id
        WHERE r.band_id = ?1
        ORDER BY e.date_of_event desc""";

    public static List<BandResultSqlDto> selectBandResults(EntityManager entityManager, Long bandId) {
        return SqlExec.execute(entityManager, BAND_RESULT_LIST_SQL, bandId, BandResultSqlDto.class);
    }

    private static final String BAND_RESULT_RESULT_PIECES_SQL = """
        SELECT rp.contest_result_id, p.slug, p.name, p.piece_year
        FROM contest_result_test_piece rp
        INNER JOIN piece p ON p.id = rp.piece_id
        WHERE rp.contest_result_id IN (
                SELECT r.id FROM contest_result r WHERE r.band_id = ?1
        )""";

    public static List<ResultPieceSqlDto> selectBandResultPerformances(EntityManager entityManager, Long bandId) {
        return SqlExec.execute(entityManager, BAND_RESULT_RESULT_PIECES_SQL, bandId, ResultPieceSqlDto.class);
    }

    private static final String BAND_RESULT_EVENT_PIECES_SQL = """
        SELECT ep.contest_event_id, p.slug, p.name, p.piece_year
        FROM contest_event_test_piece ep
        INNER JOIN piece p ON p.id = ep.piece_id
        WHERE ep.contest_event_id IN (
                SELECT r.contest_event_id FROM contest_result r WHERE r.band_id = ?1
        )""";

    public static List<EventPieceSqlDto> selectBandEventPieces(EntityManager entityManager, Long bandId) {
        return SqlExec.execute(entityManager, BAND_RESULT_EVENT_PIECES_SQL, bandId, EventPieceSqlDto.class);
    }

    private static final String PERSON_CONDUCTING_SQL = """
        SELECT e.date_of_event, e.date_resolution, c.slug as contest_slug, c.name as contest_name, r.band_name, b.name as current_band_name, b.slug as band_slug, r.result_position, r.result_position_type, r.result_award, r.points_total, r.draw, r.id as result_id, e.id as event_id, region.name as region_name, region.country_code
        FROM contest_result r
                 INNER JOIN contest_event e ON e.id = r.contest_event_id
                 INNER JOIN contest c ON c.id = e.contest_id
                 INNER JOIN band b ON b.id = r.band_id
                 LEFT OUTER JOIN region region on region.id = b.region_id
        WHERE r.conductor_id = ?1 OR r.conductor_two_id = ?1 OR r.conductor_three_id = ?1
        ORDER BY e.date_of_event DESC;
        """;

    public static List<PersonConductingResultSqlDto> selectPersonConductingResults(EntityManager entityManager, Long personId) {
        return SqlExec.execute(entityManager, PERSON_CONDUCTING_SQL, personId, PersonConductingResultSqlDto.class);
    }

    private static final String CONTEST_EVENTS_LIST_SQL = """
            SELECT e.date_of_event, e.date_resolution, c.slug as contest_slug, r.band_name as band_competed_as, b.slug as band_slug, b.name as band_name, reg.country_code,
                    rp.slug as own_choice_piece_slug, rp.name as own_choice_piece_name,
                    ep.slug as test_piece_slug, ep.name as test_piece_name,
                    con1.slug as c1_slug, con1.first_names as c1_first_names, con1.surname as c1_surname,
                    con2.slug as c2_slug, con2.first_names as c2_first_names, con2.surname as c2_surname,
                    con3.slug as c3_slug, con3.first_names as c3_first_names, con3.surname as c3_surname
            FROM contest_event e
                INNER JOIN contest c ON c.id = e.contest_id
                LEFT OUTER JOIN contest_result r ON r.contest_event_id = e.id AND r.result_position = 1 AND r.result_position_type = 'R'
                LEFT OUTER JOIN band b ON b.id = r.band_id
                LEFT OUTER JOIN region reg ON reg.id = b.region_id
                LEFT OUTER JOIN contest_event_test_piece et ON et.contest_event_id = e.id
                LEFT OUTER JOIN contest_result_test_piece rt ON rt.contest_result_id = r.id
                LEFT OUTER JOIN piece ep ON ep.id = et.piece_id
                LEFT OUTER JOIN piece rp ON rp.id = rt.piece_id
                LEFT OUTER JOIN person con1 ON con1.id = r.conductor_id
                LEFT OUTER JOIN person con2 ON con2.id = r.conductor_two_id
                LEFT OUTER JOIN person con3 ON con3.id = r.conductor_three_id
            WHERE c.id = ?1
            ORDER BY e.date_of_event DESC""";

    public static List<ContestEventResultSqlDto> selectEventListForContest(EntityManager entityManager, Long contestId) {
        return SqlExec.execute(entityManager, CONTEST_EVENTS_LIST_SQL, contestId, ContestEventResultSqlDto.class);
    }

    private static final String OWN_CHOICE_FOR_CONTEST = """
        SELECT e.date_of_event, e.date_resolution, c.slug as contest_slug, r.band_name as competed_as, b.slug as band_slug, b.name as band_name, p.name as piece_name, p.slug as piece_slug, p.piece_year, r.result_position, r.result_position_type, reg.name, reg.country_code
        FROM contest_result_test_piece rt
        INNER JOIN contest_result r ON r.id = rt.contest_result_id
        INNER JOIN contest_event e ON e.id = r.contest_event_id
        INNER JOIN contest c ON c.id = e.contest_id
        INNER JOIN band b ON b.id = r.band_id
        INNER JOIN region reg ON reg.id = b.region_id
        INNER JOIN piece p ON p.id = rt.piece_id
        WHERE c.id = ?1
        ORDER BY e.date_of_event, r.result_position DESC""";

    public static List<ContestResultPieceSqlDto> selectOwnChoiceUsedForContest(EntityManager entityManager, Long contestId) {
        return SqlExec.execute(entityManager, OWN_CHOICE_FOR_CONTEST, contestId, ContestResultPieceSqlDto.class);
    }

    private static final String BAND_WINS_FOR_CONTEST_SQL = """
                    SELECT b.slug, b.name, COUNT(*)
                    FROM contest_result r
                             INNER JOIN band b ON b.id = r.band_id
                             INNER JOIN contest_event e ON e.id = r.contest_event_id
                             INNER JOIN contest c ON c.id = e.contest_id
                    WHERE c.id = ?1
                      AND r.result_position_type = 'R'
                      AND r.result_position = 1
                    GROUP BY b.slug, b.name
                    ORDER BY 3 DESC""";

    public static List<ContestWinsSqlDto> selectWinsForContest(EntityManager entityManager, Long contestId) {
        return SqlExec.execute(entityManager, BAND_WINS_FOR_CONTEST_SQL, contestId, ContestWinsSqlDto.class);
    }
}
