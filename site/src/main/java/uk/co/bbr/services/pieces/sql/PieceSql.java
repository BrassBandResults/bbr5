package uk.co.bbr.services.pieces.sql;

import lombok.experimental.UtilityClass;
import uk.co.bbr.services.framework.sql.SqlExec;
import uk.co.bbr.services.pieces.sql.dto.BestPieceSqlDto;
import uk.co.bbr.services.pieces.sql.dto.OwnChoiceUsagePieceSqlDto;
import uk.co.bbr.services.pieces.sql.dto.PieceUsageCountSqlDto;
import uk.co.bbr.services.pieces.sql.dto.PiecesPerSectionSqlDto;
import uk.co.bbr.services.pieces.sql.dto.SetTestUsagePieceSqlDto;

import javax.persistence.EntityManager;
import java.util.List;

@UtilityClass
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

    public static List<OwnChoiceUsagePieceSqlDto> selectOwnChoicePieceUsage(EntityManager entityManager, Long pieceId) {
        return SqlExec.execute(entityManager, PIECE_OWN_CHOICE_SQL, pieceId, OwnChoiceUsagePieceSqlDto.class);
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

    public static List<SetTestUsagePieceSqlDto> selectSetTestPieceUsage(EntityManager entityManager, Long pieceId) {
        return SqlExec.execute(entityManager, PIECE_SET_TEST_SQL, pieceId, SetTestUsagePieceSqlDto.class);
    }

                private static final String PIECES_USAGE_COUNT_BY_PREFIX_SQL = """
            SELECT p.id,
                   (SELECT count(*) FROM contest_event_test_piece e WHERE e.piece_id = p.id) as set_test_count,
                   (SELECT count(*) FROM contest_result_test_piece r WHERE r.piece_id = p.id) as own_choice_count
            FROM piece p
            WHERE UPPER(p.name) LIKE ?1
            ORDER BY p.id""";

    public static List<PieceUsageCountSqlDto> selectPieceUsageCounts(EntityManager entityManager, String prefix) {
        return SqlExec.execute(entityManager, PIECES_USAGE_COUNT_BY_PREFIX_SQL, prefix + "%", PieceUsageCountSqlDto.class);
    }

    private static final String ALL_PIECES_USAGE_COUNT_SQL = """
            SELECT p.id,
                    (SELECT count(*) FROM contest_event_test_piece e WHERE e.piece_id = p.id) as set_test_count,
                   (SELECT count(*) FROM contest_result_test_piece r WHERE r.piece_id = p.id) as own_choice_count
            FROM piece p
            ORDER BY p.id""";

    public static List<PieceUsageCountSqlDto> selectAllPieceUsageCounts(EntityManager entityManager) {
        return SqlExec.execute(entityManager, ALL_PIECES_USAGE_COUNT_SQL, PieceUsageCountSqlDto.class);
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
        return SqlExec.execute(entityManager, PIECES_USAGE_COUNT_FOR_NUMBERS_SQL, PieceUsageCountSqlDto.class);
    }

    private static final String SUCCESSFUL_OWN_CHOICE_SQL = """
            SELECT r.result_position, p.slug as piece_slug, p.name as piece_name, p.piece_year, comp.surname as composer_surname, comp.first_names as composer_first_names, comp.suffix as composer_suffix, comp.slug as composer_slug, a.surname as arranger_surname, a.first_names as arranger_first_names, a.suffix as arranger_suffix, a.slug as arranger_slug
            FROM contest_result r
                INNER JOIN contest_result_test_piece rp ON rp.contest_result_id = r.id
                INNER JOIN piece p ON p.id = rp.piece_id
                INNER JOIN contest_event e ON e.id = r.contest_event_id
                INNER JOIN contest c ON c.id = e.contest_id
                LEFT OUTER JOIN person comp ON comp.id = p.composer_id
                LEFT OUTER JOIN person a ON a.id = p.arranger_id
            WHERE r.result_position < 4
              AND r.result_position_type = 'R'
              AND c.name NOT LIKE '%Whit Friday%'""";
    public static List<BestPieceSqlDto> mostSuccessfulOwnChoice(EntityManager entityManager) {
        return SqlExec.execute(entityManager, SUCCESSFUL_OWN_CHOICE_SQL, BestPieceSqlDto.class);
    }

    private static final String PIECES_FOR_SECTION_SQL = """
            WITH set_test AS (SELECT p.slug as piece_slug
                FROM piece p
                INNER JOIN contest_event_test_piece cetp on p.id = cetp.piece_id
                INNER JOIN contest_event ce on cetp.contest_event_id = ce.id
                INNER JOIN contest c on ce.contest_id = c.id
                INNER JOIN section s on c.section_id = s.id
                WHERE s.slug = ?1
                AND YEAR(ce.date_of_event) >= 1990
                GROUP BY p.slug)
            SELECT p.slug as piece_slug, p.name as piece_name, p.piece_year, comp.surname as composer_surname, comp.first_names as composer_first_names, comp.suffix as composer_suffix, comp.slug as composer_slug, a.surname as arranger_surname, a.first_names as arranger_first_names, a.suffix as arranger_suffix, a.slug as arranger_slug,
               (SELECT count(*) FROM contest_event_test_piece cetp WHERE p.id = cetp.piece_id) as set_test_count,
               (SELECT count(*) FROM contest_result_test_piece crtp WHERE p.id = crtp.piece_id) as own_choice_count
            FROM piece p
             LEFT OUTER JOIN person comp ON comp.id = p.composer_id
             LEFT OUTER JOIN person a ON a.id = p.arranger_id
            WHERE p.slug IN (SELECT piece_slug FROM set_test)
            ORDER BY 12""";

    public static List<PiecesPerSectionSqlDto> piecesForSection(EntityManager entityManager, String sectionSlug) {
        return SqlExec.execute(entityManager, PIECES_FOR_SECTION_SQL, sectionSlug, PiecesPerSectionSqlDto.class);
    }
}
