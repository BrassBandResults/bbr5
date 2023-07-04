package uk.co.bbr.services.bands.sql;

import lombok.experimental.UtilityClass;
import uk.co.bbr.services.bands.sql.dto.BandWinnersSqlDto;
import uk.co.bbr.services.framework.sql.SqlExec;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
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
            SELECT b.slug as band_slug, b.name as band_name, w.winners, t.contests, r.slug as region_slug, r.name as region_name, r.country_code
            FROM band b
            INNER JOIN winners w ON b.id = w.band_id
            INNER JOIN total t ON b.id = t.band_id
            LEFT OUTER JOIN region r ON r.id = b.region_id
            ORDER BY 3 desc""";

    public static List<BandWinnersSqlDto> selectWinningBands(EntityManager entityManager) {
        return SqlExec.execute(entityManager, WINNING_BANDS_SQL, BandWinnersSqlDto.class);
    }

    private static final String BANDS_COMPETED_IN_YEAR_SQL = """
            SELECT COUNT(*) FROM 
            ( SELECT DISTINCT(r.band_id)
              FROM contest_result r
              INNER JOIN contest_event e ON e.id = r.contest_event_id
              WHERE YEAR(e.date_of_event) = ?1) counts
    """;
    public static int countBandsCompetedInYear(EntityManager entityManager, int year) {
        int count = 0;
        try {
            Query query = entityManager.createNativeQuery(BANDS_COMPETED_IN_YEAR_SQL);
            query.setParameter(1, year);
            List<BigInteger> queryResults = query.getResultList();

            for (BigInteger columnList : queryResults) {
                count = columnList.intValue();
            }

            return count;
        } catch (Exception e) {
            throw new RuntimeException("SQL Failure, " + e.getMessage());
        }
    }
}

