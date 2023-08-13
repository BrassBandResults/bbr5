package uk.co.bbr.services.regions.sql;

import lombok.experimental.UtilityClass;
import uk.co.bbr.services.bands.sql.dto.BandListSqlDto;
import uk.co.bbr.services.bands.sql.dto.BandWinnersSqlDto;
import uk.co.bbr.services.framework.sql.SqlExec;
import uk.co.bbr.services.regions.sql.dto.BandListForRegionSqlDto;
import uk.co.bbr.services.regions.sql.dto.ContestListForRegionSqlDto;
import uk.co.bbr.services.regions.sql.dto.RegionListSqlDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.math.BigInteger;
import java.util.List;

@UtilityClass
public class RegionSql {

    private static final String REGION_LIST_ALL_SQL = """
            SELECT r.slug, r.name, r.country_code, 0 as active_bands_count, 0 as extinct_bands_count, r.id
            FROM region r
            ORDER BY r.name
            """;

    public static List<RegionListSqlDto> listAll(EntityManager entityManager) {
        return SqlExec.execute(entityManager, REGION_LIST_ALL_SQL, RegionListSqlDto.class);
    }

    private static final String REGION_LIST_ALL_WITH_COUNTS_SQL = """
            WITH active_band_counts AS (SELECT b.region_id as region_id, count(*) as active_count FROM band b WHERE b.status != 0 GROUP BY b.region_id),
                 extinct_band_counts AS (SELECT b.region_id as region_id, count(*) as extinct_count FROM band b WHERE b.status = 0 GROUP BY b.region_id)
            SELECT r.slug, r.name, r.country_code, active.active_count as active_bands_count, extinct.extinct_count as extinct_bands_count, r.id
            FROM region r
            LEFT OUTER JOIN active_band_counts active ON active.region_id = r.id
            LEFT OUTER JOIN extinct_band_counts extinct ON extinct.region_id = r.id
            ORDER BY r.name
            """;

    public static List<RegionListSqlDto> listAllWithCounts(EntityManager entityManager) {
        return SqlExec.execute(entityManager, REGION_LIST_ALL_WITH_COUNTS_SQL, RegionListSqlDto.class);
    }

    private static final String BANDS_FOR_REGION = """
        WITH result_counts AS (
            SELECT result.band_id as band_id, count(*) as result_count
            FROM contest_result result
            INNER JOIN band bnd ON bnd.id = result.band_id
            WHERE bnd.region_id = ?1
            GROUP BY result.band_id
        )
        SELECT b.name as band_name, b.slug as band_slug, r.name as region_name, r.slug as region_slug, r.country_code as region_code, c.result_count, b.status, s.slug, s.translation_key
        FROM band b
        LEFT OUTER JOIN region r ON r.id = b.region_id
        LEFT OUTER JOIN result_counts c ON c.band_id = b.id
        LEFT OUTER JOIN section s ON s.id = b.section_id
        WHERE b.region_id = ?1
        ORDER BY b.name""";

    public static List<BandListForRegionSqlDto> listBandsForRegion(EntityManager entityManager, Long regionId) {
        return SqlExec.execute(entityManager, BANDS_FOR_REGION, regionId, BandListForRegionSqlDto.class);
    }

    private static final String CONTESTS_FOR_REGION = """
        WITH result_counts AS (
            SELECT e.contest_id as contest_id, count(*) as event_count
            FROM contest_event e
                     INNER JOIN contest c ON c.id = e.contest_id
                     INNER JOIN region r ON r.id = c.region_id
            WHERE r.slug = ?1
            GROUP BY e.contest_id
            )
        SELECT c.name as contest_name, c.slug as contest_slug, counts.event_count
        FROM contest c
        INNER JOIN region r ON r.id = c.region_id
        LEFT OUTER JOIN result_counts counts ON counts.contest_id = c.id
        WHERE r.slug = ?1
        ORDER BY c.name""";

    public static List<ContestListForRegionSqlDto> listContestsForRegion(EntityManager entityManager, String regionSlug) {
        return SqlExec.execute(entityManager, CONTESTS_FOR_REGION, regionSlug, ContestListForRegionSqlDto.class);
    }

}

