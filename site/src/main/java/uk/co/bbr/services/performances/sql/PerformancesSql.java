package uk.co.bbr.services.performances.sql;

import lombok.experimental.UtilityClass;
import uk.co.bbr.services.bands.sql.dto.BandListSqlDto;
import uk.co.bbr.services.bands.sql.dto.BandWinnersSqlDto;
import uk.co.bbr.services.framework.sql.SqlExec;
import uk.co.bbr.services.performances.sql.dto.PiecePerformanceSqlDto;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.List;

@UtilityClass
public class PerformancesSql {

    private static final String PIECE_PERFORMANCES_SQL = """
        SELECT r.id as result_id, c.name as contest_name, c.slug as contest_slug, e.date_of_event as date_of_event, e.date_resolution, r.band_name as competed_as, b.name as band_name, b.slug as band_slug, reg.name as region_name, reg.slug as region_slug, reg.country_code as region_country_code, r.result_position, r.result_position_type
        FROM contest_result r
        INNER JOIN personal_contest_history pch on r.id = pch.result_id
        INNER JOIN contest_event e ON e.id = r.contest_event_id
        INNER JOIN contest c ON c.id = e.contest_id
        INNER JOIN band b ON b.id = r.band_id
        INNER JOIN region reg ON reg.id = b.region_id
        WHERE pch.created_by = ?1
        AND (
            r.id IN (SELECT crtp.contest_result_id FROM contest_result_test_piece crtp WHERE crtp.piece_id = ?2)
            OR e.id IN (SELECT cetp.contest_event_id FROM contest_event_test_piece cetp WHERE cetp.piece_id = ?2)
            )
        ORDER BY e.date_of_event DESC""";

    public static List<PiecePerformanceSqlDto> selectPerformancesOfPiece(EntityManager entityManager, String username, Long pieceId) {
        return SqlExec.execute(entityManager, PIECE_PERFORMANCES_SQL, username, pieceId, PiecePerformanceSqlDto.class);
    }
}

