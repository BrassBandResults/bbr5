package uk.co.bbr.services.regions.sql;

import lombok.experimental.UtilityClass;
import uk.co.bbr.services.bands.sql.dto.BandListSqlDto;
import uk.co.bbr.services.bands.sql.dto.BandWinnersSqlDto;
import uk.co.bbr.services.framework.sql.SqlExec;
import uk.co.bbr.services.regions.sql.dto.RegionListSqlDto;

import javax.persistence.EntityManager;
import javax.persistence.Query;
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


}

