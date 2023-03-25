package uk.co.bbr.services.contests.sql;

import uk.co.bbr.services.contests.sql.dto.BandEventPiecesSqlDto;
import uk.co.bbr.services.contests.sql.dto.BandResultSqlDto;
import uk.co.bbr.services.contests.sql.dto.BandResultsPiecesSqlDto;
import uk.co.bbr.services.contests.sql.dto.EventPieceSqlDto;
import uk.co.bbr.services.contests.sql.dto.ResultPieceSqlDto;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

public class ContestResultSql {

    public static final String BAND_RESULT_LIST_SQL = """
        SELECT r.id as result_id, e.date_of_event, e.date_resolution, c.slug as contest_slug, c.name, r.result_position, r.result_position_type, r.draw, e.id as event_id,
        con1.slug as c1_slug, con1.first_names as c1_first_names, con1.surname as c1_surname,
        con2.slug as c2_slug, con2.first_names as c2_first_names, con2.surname as c2_surname,
        con3.slug as c3_slug, con3.first_names as c3_first_names, con3.surname as c3_surname
        FROM contest_result r
        INNER JOIN contest_event e ON e.id = r.contest_event_id
        INNER JOIN contest c ON c.id = e.contest_id
        LEFT OUTER JOIN person con1 ON con1.id = r.conductor_id
        LEFT OUTER JOIN person con2 ON con2.id = r.conductor_two_id
        LEFT OUTER JOIN person con3 ON con3.id = r.conductor_three_id
        WHERE r.band_id = ?1
        ORDER BY e.date_of_event desc""";

    public static final String BAND_RESULT_RESULT_PIECES_SQL = """
        SELECT rp.contest_result_id, p.slug, p.name, p.piece_year
        FROM contest_result_test_piece rp
        INNER JOIN piece p ON p.id = rp.piece_id
        WHERE rp.contest_result_id IN (
                SELECT r.id FROM contest_result r WHERE r.band_id = ?1
        )""";

    public static final String BAND_RESULT_EVENT_PIECES_SQL = """
        SELECT ep.contest_event_id, p.slug, p.name, p.piece_year
        FROM contest_event_test_piece ep
        INNER JOIN piece p ON p.id = ep.piece_id
        WHERE ep.contest_event_id IN (
                SELECT r.contest_event_id FROM contest_result r WHERE r.band_id = ?1
        )""";

    public static List<BandResultSqlDto> selectBandResults(EntityManager entityManager, Long bandId) {
        List<BandResultSqlDto> returnData = new ArrayList<>();
        try {
            Query query = entityManager.createNativeQuery(BAND_RESULT_LIST_SQL);
            query.setParameter(1, bandId);
            List<Object[]> queryResults = query.getResultList();

            for (Object[] eachRowData : queryResults) {
                BandResultSqlDto eachReturnObject = new BandResultSqlDto(eachRowData);
                returnData.add(eachReturnObject);
            }

            return returnData;
        } catch (Exception e) {
            throw new RuntimeException("SQL Failure, " + e.getMessage());
        }
    }

    public static BandResultsPiecesSqlDto selectBandResultPerformances(EntityManager entityManager, Long bandId) {
        BandResultsPiecesSqlDto returnData = new BandResultsPiecesSqlDto();
        try {
            Query query = entityManager.createNativeQuery(BAND_RESULT_RESULT_PIECES_SQL);
            query.setParameter(1, bandId);
            List<Object[]> queryResults = query.getResultList();

            for (Object[] eachRowData : queryResults) {
                ResultPieceSqlDto eachReturnObject = new ResultPieceSqlDto(eachRowData);
                returnData.add(eachReturnObject);
            }

            return returnData;
        } catch (Exception e) {
            throw new RuntimeException("SQL Failure, " + e.getMessage());
        }
    }

    public static BandEventPiecesSqlDto selectBandEventPieces(EntityManager entityManager, Long bandId) {
        BandEventPiecesSqlDto returnData = new BandEventPiecesSqlDto();
        try {
            Query query = entityManager.createNativeQuery(BAND_RESULT_EVENT_PIECES_SQL);
            query.setParameter(1, bandId);
            List<Object[]> queryResults = query.getResultList();

            for (Object[] eachRowData : queryResults) {
                EventPieceSqlDto eachReturnObject = new EventPieceSqlDto(eachRowData);
                returnData.add(eachReturnObject);
            }

            return returnData;
        } catch (Exception e) {
            throw new RuntimeException("SQL Failure, " + e.getMessage());
        }
    }

}
