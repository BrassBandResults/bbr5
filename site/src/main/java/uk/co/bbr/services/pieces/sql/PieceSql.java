package uk.co.bbr.services.pieces.sql;

import uk.co.bbr.services.pieces.sql.dto.OwnChoiceUsagePieceSqlDto;
import uk.co.bbr.services.pieces.sql.dto.OwnChoiceUsageSqlDto;
import uk.co.bbr.services.pieces.sql.dto.PieceUsageCountSqlDto;
import uk.co.bbr.services.pieces.sql.dto.SetTestUsagePieceSqlDto;
import uk.co.bbr.services.pieces.sql.dto.SetTestUsageSqlDto;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class PieceSql {

    private static final String PIECE_OWN_CHOICE_SQL = """
            SELECT e.date_of_event, e.date_resolution, c.slug, c.name as contest_name, r.band_name as competed_as, b.name as band_name, b.slug as band_slug, r.result_position, r.result_position_type, r.result_award, r.id as result_id, e.id as event_id, reg.country_code, reg.name as region_name
            FROM contest_result_test_piece rt
                INNER JOIN contest_result r ON r.id = rt.contest_result_id
                INNER JOIN contest_event e ON e.id = r.contest_event_id
                INNER JOIN contest c ON c.id = e.contest_id
                INNER JOIN band b ON b.id = r.band_id
                INNER JOIN region reg ON reg.id = b.region_id
            WHERE rt.piece_id = ?1
            ORDER BY e.date_of_event, c.name DESC""";

    public static OwnChoiceUsageSqlDto selectOwnChoicePieceUsage(EntityManager entityManager, Long pieceId) {
        OwnChoiceUsageSqlDto returnData = new OwnChoiceUsageSqlDto();
        try {
            Query query = entityManager.createNativeQuery(PIECE_OWN_CHOICE_SQL);
            query.setParameter(1, pieceId);
            List<Object[]> queryResults = query.getResultList();

            for (Object[] eachRowData : queryResults) {
                OwnChoiceUsagePieceSqlDto eachReturnObject = new OwnChoiceUsagePieceSqlDto(eachRowData);
                returnData.add(eachReturnObject);
            }

            return returnData;
        } catch (Exception e) {
            throw new RuntimeException("SQL Failure, " + e.getMessage());
        }
    }

    private static final String PIECE_SET_TEST_SQL = """
            SELECT e.date_of_event, e.date_resolution, c.slug, c.name as contest_name, r.band_name as competed_as, b.name as band_name , b.slug as band_slug, r.result_position, r.result_position_type, r.result_award, r.id as result_id, e.id as event_id, reg.country_code, reg.name as region_name
            FROM contest_event_test_piece et
                INNER JOIN contest_event e ON e.id = et.contest_event_id
                INNER JOIN contest c ON c.id = e.contest_id
                LEFT OUTER JOIN contest_result r ON r.contest_event_id = e.id AND r.result_position = 1 AND r.result_position_type = 'R'
                LEFT OUTER JOIN band b ON b.id = r.band_id
                LEFT OUTER JOIN region reg ON reg.id = b.region_id
            WHERE et.piece_id = ?1
            ORDER BY e.date_of_event, c.name DESC""";

    public static SetTestUsageSqlDto selectSetTestPieceUsage(EntityManager entityManager, Long pieceId) {
        SetTestUsageSqlDto returnData = new SetTestUsageSqlDto();
        try {
            Query query = entityManager.createNativeQuery(PIECE_SET_TEST_SQL);
            query.setParameter(1, pieceId);
            List<Object[]> queryResults = query.getResultList();

            for (Object[] eachRowData : queryResults) {
                SetTestUsagePieceSqlDto eachReturnObject = new SetTestUsagePieceSqlDto(eachRowData);
                returnData.add(eachReturnObject);
            }

            return returnData;
        } catch (Exception e) {
            throw new RuntimeException("SQL Failure, " + e.getMessage());
        }
    }

                private static final String PIECES_USAGE_COUNT_BY_PREFIX_SQL = """
            SELECT p.id,
                   (SELECT count(*) FROM contest_event_test_piece e WHERE e.piece_id = p.id) as set_test_count,
                   (SELECT count(*) FROM contest_result_test_piece r WHERE r.piece_id = p.id) as own_choice_count
            FROM piece p
            WHERE UPPER(p.name) LIKE ?1
            ORDER BY p.id""";

    public static List<PieceUsageCountSqlDto> selectPieceUsageCounts(EntityManager entityManager, String prefix) {
        List<PieceUsageCountSqlDto> returnData = new ArrayList<>();
        try {
            Query query = entityManager.createNativeQuery(PIECES_USAGE_COUNT_BY_PREFIX_SQL);
            query.setParameter(1, prefix + "%");
            List<Object[]> queryResults = query.getResultList();

            for (Object[] eachRowData : queryResults) {
                PieceUsageCountSqlDto eachReturnObject = new PieceUsageCountSqlDto(eachRowData);
                returnData.add(eachReturnObject);
            }

            return returnData;
        } catch (Exception e) {
            throw new RuntimeException("SQL Failure, " + e.getMessage());
        }
    }

    private static final String ALL_PIECES_USAGE_COUNT_SQL = """
            SELECT p.id,
                    (SELECT count(*) FROM contest_event_test_piece e WHERE e.piece_id = p.id) as set_test_count,
                   (SELECT count(*) FROM contest_result_test_piece r WHERE r.piece_id = p.id) as own_choice_count
            FROM piece p
            ORDER BY p.id""";

    public static List<PieceUsageCountSqlDto> selectAllPieceUsageCounts(EntityManager entityManager) {
        List<PieceUsageCountSqlDto> returnData = new ArrayList<>();
        try {
            Query query = entityManager.createNativeQuery(ALL_PIECES_USAGE_COUNT_SQL);
            List<Object[]> queryResults = query.getResultList();

            for (Object[] eachRowData : queryResults) {
                PieceUsageCountSqlDto eachReturnObject = new PieceUsageCountSqlDto(eachRowData);
                returnData.add(eachReturnObject);
            }

            return returnData;
        } catch (Exception e) {
            throw new RuntimeException("SQL Failure, " + e.getMessage());
        }
    }

    private static final String PIECES_USAGE_COUNT_FOR_NUMBERS_SQL = """
            SELECT p.id,
                   (SELECT count(*) FROM contest_event_test_piece e WHERE e.piece_id = p.id) as set_test_count,
                   (SELECT count(*) FROM contest_result_test_piece r WHERE r.piece_id = p.id) as own_choice_count
            FROM piece p
            WHERE p.name LIKE '0%'
            OR p.name LIKE '1%'
            OR p.name LIKE '2%'
            OR p.name LIKE '3%'
            OR p.name LIKE '4%'
            OR p.name LIKE '5%'
            OR p.name LIKE '6%'
            OR p.name LIKE '7%'
            OR p.name LIKE '8%'
            OR p.name LIKE '9%'
            ORDER BY p.id""";

    public static List<PieceUsageCountSqlDto> selectNumbersPieceUsageCounts(EntityManager entityManager) {
        List<PieceUsageCountSqlDto> returnData = new ArrayList<>();
        try {
            Query query = entityManager.createNativeQuery(PIECES_USAGE_COUNT_FOR_NUMBERS_SQL);
            List<Object[]> queryResults = query.getResultList();

            for (Object[] eachRowData : queryResults) {
                PieceUsageCountSqlDto eachReturnObject = new PieceUsageCountSqlDto(eachRowData);
                returnData.add(eachReturnObject);
            }

            return returnData;
        } catch (Exception e) {
            throw new RuntimeException("SQL Failure, " + e.getMessage());
        }
    }
}
