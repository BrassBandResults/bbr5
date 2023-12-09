package uk.co.bbr.services.bands.sql;

import lombok.experimental.UtilityClass;
import uk.co.bbr.services.bands.sql.dto.BandListSqlDto;
import uk.co.bbr.services.bands.sql.dto.BandWinnersSqlDto;
import uk.co.bbr.services.framework.sql.SqlExec;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
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
            List queryResults = query.getResultList();

            if (!queryResults.isEmpty()) {
                if (queryResults.get(0) instanceof BigInteger) {
                    count = ((BigInteger) queryResults.get(0)).intValue();
                } else if (queryResults.get(0) instanceof Long) {
                    count = ((Long)queryResults.get(0)).intValue();
                }else {
                    count = (Integer)queryResults.get(0);
                }
            }

            return count;
        } catch (Exception e) {
            throw new RuntimeException("SQL Failure, " + e.getMessage());
        }
    }

    private static final String BANDS_LIST_BY_INITIAL_LETTER_SQL = """
        WITH result_counts AS (
            SELECT result.band_id as band_id, count(*) as result_count
            FROM contest_result result
            INNER JOIN band bnd ON bnd.id = result.band_id
            WHERE UPPER(bnd.name) LIKE ?1
            GROUP BY result.band_id
        )
        SELECT b.name as band_name, b.slug as band_slug, r.name as region_name, r.slug as region_slug, r.country_code as region_code, c.result_count
        FROM band b
        LEFT OUTER JOIN region r ON r.id = b.region_id
        LEFT OUTER JOIN result_counts c ON c.band_id = b.id
        WHERE UPPER(b.name) LIKE ?1
        ORDER BY b.name""";

    public static List<BandListSqlDto> selectBandsStartingWithLetterForList(EntityManager entityManager, String letter) {
        return SqlExec.execute(entityManager, BANDS_LIST_BY_INITIAL_LETTER_SQL, letter + "%", BandListSqlDto.class);
    }

    private static final String BANDS_LIST_BY_INITIAL_NUMBER_SQL = """
        WITH result_counts AS (
            SELECT result.band_id as band_id, count(*) as result_count
            FROM contest_result result
            INNER JOIN band bnd ON bnd.id = result.band_id
            WHERE bnd.name LIKE '0%'
            OR bnd.name LIKE '1%'
            OR bnd.name LIKE '2%'
            OR bnd.name LIKE '3%'
            OR bnd.name LIKE '4%'
            OR bnd.name LIKE '5%'
            OR bnd.name LIKE '6%'
            OR bnd.name LIKE '7%'
            OR bnd.name LIKE '8%'
            OR bnd.name LIKE '9%'
            GROUP BY result.band_id
        )
        SELECT b.name as band_name, b.slug as band_slug, r.name as region_name, r.slug as region_slug, r.country_code as region_code, c.result_count
        FROM band b
        LEFT OUTER JOIN region r ON r.id = b.region_id
        LEFT OUTER JOIN result_counts c ON c.band_id = b.id
        WHERE  b.name LIKE '0%'
            OR b.name LIKE '1%'
            OR b.name LIKE '2%'
            OR b.name LIKE '3%'
            OR b.name LIKE '4%'
            OR b.name LIKE '5%'
            OR b.name LIKE '6%'
            OR b.name LIKE '7%'
            OR b.name LIKE '8%'
            OR b.name LIKE '9%'
        ORDER BY b.name""";

    public static List<BandListSqlDto> selectBandsStartingWithNumbersForList(EntityManager entityManager) {
        return SqlExec.execute(entityManager, BANDS_LIST_BY_INITIAL_NUMBER_SQL, BandListSqlDto.class);
    }

    private static final String BANDS_LIST_ALL_BANDS_SQL = """
        WITH result_counts AS (
            SELECT result.band_id as band_id, count(*) as result_count
            FROM contest_result result
            INNER JOIN band bnd ON bnd.id = result.band_id
            GROUP BY result.band_id
        )
        SELECT b.name as band_name, b.slug as band_slug, r.name as region_name, r.slug as region_slug, r.country_code as region_code, c.result_count
        FROM band b
        LEFT OUTER JOIN region r ON r.id = b.region_id
        LEFT OUTER JOIN result_counts c ON c.band_id = b.id
        ORDER BY b.name""";

    public static List<BandListSqlDto> selectAllBandsForList(EntityManager entityManager) {
        return SqlExec.execute(entityManager, BANDS_LIST_ALL_BANDS_SQL, BandListSqlDto.class);
    }

    private static final String BANDS_LIST_UNUSED_BANDS_SQL = """
        SELECT b.name as band_name, b.slug as band_slug, r.name as region_name, r.slug as region_slug, r.country_code as region_code, 0
        FROM band b
        LEFT OUTER JOIN region r ON r.id = b.region_id
        WHERE NOT EXISTS (SELECT * FROM contest_result WHERE band_id = b.id)
        ORDER BY b.name""";

    public static List<BandListSqlDto> selectUnusedBandsForList(EntityManager entityManager) {
        return SqlExec.execute(entityManager, BANDS_LIST_UNUSED_BANDS_SQL, BandListSqlDto.class);
    }
}

