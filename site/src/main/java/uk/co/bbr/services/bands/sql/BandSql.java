package uk.co.bbr.services.bands.sql;

import lombok.experimental.UtilityClass;
import uk.co.bbr.services.bands.sql.dto.BandWinnersSqlDto;
import uk.co.bbr.services.framework.sql.SqlExec;

import javax.persistence.EntityManager;
import java.util.List;

@UtilityClass
public class BandSql {

    private static final String WINNING_BANDS_SQL = """
            WITH
              winners AS
               (SELECT band_id, count(*) as winners
                FROM contest_result r
                INNER JOIN contest_event e ON e.id = r.contest_event_id
                INNER JOIN contest c ON c.id = e.contest_id
                WHERE r.result_position = 1
                AND r.result_position_type = 'R'
                AND c.name NOT LIKE '%Whit Friday%'
                GROUP BY band_id),
              total AS
               (SELECT band_id, count(*) as contests
                FROM contest_result r
                INNER JOIN contest_event e ON e.id = r.contest_event_id
                INNER JOIN contest c ON c.id = e.contest_id
                AND c.name NOT LIKE '%Whit Friday%'
                AND r.result_position_type = 'R'
                GROUP BY band_id)
            SELECT b.slug, b.name, w.winners, t.contests
            FROM band b
            INNER JOIN winners w ON b.id = w.band_id
            INNER JOIN total t ON b.id = t.band_id
            ORDER BY 3 desc""";

    public static List<BandWinnersSqlDto> selectWinningBands(EntityManager entityManager) {
        return SqlExec.execute(entityManager, WINNING_BANDS_SQL, BandWinnersSqlDto.class);
    }
}
