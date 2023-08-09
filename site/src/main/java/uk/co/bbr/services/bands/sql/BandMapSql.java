package uk.co.bbr.services.bands.sql;

import lombok.experimental.UtilityClass;
import uk.co.bbr.services.bands.sql.dto.RegionBandSqlDto;
import uk.co.bbr.services.framework.sql.SqlExec;

import jakarta.persistence.EntityManager;
import java.util.List;

@UtilityClass
public class BandMapSql {

    private static final String MAP_BANDS_FOR_REGION = """
        SELECT b.slug as band_slug, b.name as band_name, b.status as band_status,
               s.slug as section_slug, s.name as section_name, s.translation_key as section_key, s.map_short_code as section_map_code,
               b.longitude, b.latitude, b.website,
               sun.id as sunday, mon.id as monday, tue.id as tuesday, wed.id as wednesday, thu.id as thursday, fri.id as friday, sat.id as saturday
        FROM band b
        LEFT OUTER JOIN section s ON s.id = b.section_id
        LEFT OUTER JOIN band_rehearsal_day sun ON sun.band_id = b.id AND sun.day_number = 0
        LEFT OUTER JOIN band_rehearsal_day mon ON mon.band_id = b.id AND mon.day_number = 1
        LEFT OUTER JOIN band_rehearsal_day tue ON tue.band_id = b.id AND tue.day_number = 2
        LEFT OUTER JOIN band_rehearsal_day wed ON wed.band_id = b.id AND wed.day_number = 3
        LEFT OUTER JOIN band_rehearsal_day thu ON thu.band_id = b.id AND thu.day_number = 4
        LEFT OUTER JOIN band_rehearsal_day fri ON fri.band_id = b.id AND fri.day_number = 5
        LEFT OUTER JOIN band_rehearsal_day sat ON sat.band_id = b.id AND sat.day_number = 6
        WHERE b.region_id = ?1
        AND LEN(b.latitude) > 0 AND LEN(b.longitude) > 0""";

    public static List<RegionBandSqlDto> selectBandsForRegionMap(EntityManager entityManager, Long regionId) {
        return SqlExec.execute(entityManager, MAP_BANDS_FOR_REGION, regionId, RegionBandSqlDto.class);
    }

    private static final String MAP_BANDS_FOR_DAYS_MAP = """
        SELECT b.slug as band_slug, b.name as band_name, b.status as band_status,
               s.slug as section_slug, s.name as section_name, s.translation_key as section_key, s.map_short_code as section_map_code,
               b.longitude, b.latitude, b.website,
               sun.id as sunday, mon.id as monday, tue.id as tuesday, wed.id as wednesday, thu.id as thursday, fri.id as friday, sat.id as saturday
        FROM band b
        LEFT OUTER JOIN section s ON s.id = b.section_id
        LEFT OUTER JOIN band_rehearsal_day sun ON sun.band_id = b.id AND sun.day_number = 0
        LEFT OUTER JOIN band_rehearsal_day mon ON mon.band_id = b.id AND mon.day_number = 1
        LEFT OUTER JOIN band_rehearsal_day tue ON tue.band_id = b.id AND tue.day_number = 2
        LEFT OUTER JOIN band_rehearsal_day wed ON wed.band_id = b.id AND wed.day_number = 3
        LEFT OUTER JOIN band_rehearsal_day thu ON thu.band_id = b.id AND thu.day_number = 4
        LEFT OUTER JOIN band_rehearsal_day fri ON fri.band_id = b.id AND fri.day_number = 5
        LEFT OUTER JOIN band_rehearsal_day sat ON sat.band_id = b.id AND sat.day_number = 6
        WHERE b.status != 0
        AND LEN(b.latitude) > 0 AND LEN(b.longitude) > 0""";

    public static List<RegionBandSqlDto> selectBandsWithRehearsalsForBandMap(EntityManager entityManager) {
        return SqlExec.execute(entityManager, MAP_BANDS_FOR_DAYS_MAP, RegionBandSqlDto.class);
    }
}

