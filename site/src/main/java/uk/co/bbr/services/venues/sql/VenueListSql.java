package uk.co.bbr.services.venues.sql;

import lombok.experimental.UtilityClass;
import uk.co.bbr.services.framework.sql.SqlExec;
import uk.co.bbr.services.venues.sql.dto.VenueListSqlDto;

import jakarta.persistence.EntityManager;
import java.util.List;

@UtilityClass
public class VenueListSql {

    private static final String VENUE_LIST_PREFIX_SQL = """
        SELECT v.slug as venue_slug, v.name as venue_name, r.slug as region_slug, r.name as region_name, r.country_code, (SELECT count(*) FROM contest_event e WHERE e.venue_id = v.id) as event_count, v.latitude, v.longitude, v.id
        FROM venue v
        LEFT OUTER JOIN region r ON r.id = v.region_id
        WHERE UPPER(v.name) LIKE ?1
        ORDER BY v.name""";

    public static List<VenueListSqlDto> venueListPrefix(EntityManager entityManager, String prefix) {
        return SqlExec.execute(entityManager, VENUE_LIST_PREFIX_SQL, prefix.toUpperCase() + "%", VenueListSqlDto.class);
    }

    private static final String VENUE_LIST_NUMBERS_SQL = """
        SELECT v.slug as venue_slug, v.name as venue_name, r.slug as region_slug, r.name as region_name, r.country_code, (SELECT count(*) FROM contest_event e WHERE e.venue_id = v.id) as event_count, v.latitude, v.longitude, v.id
        FROM venue v
        LEFT OUTER JOIN region r ON r.id = v.region_id
        WHERE v.name LIKE '0%'
        OR v.name LIKE '1%'
        OR v.name LIKE '2%'
        OR v.name LIKE '3%'
        OR v.name LIKE '4%'
        OR v.name LIKE '5%'
        OR v.name LIKE '6%'
        OR v.name LIKE '7%'
        OR v.name LIKE '8%'
        OR v.name LIKE '9%'
        ORDER BY v.name""";

    public static List<VenueListSqlDto> venueListNumber(EntityManager entityManager) {
        return SqlExec.execute(entityManager, VENUE_LIST_NUMBERS_SQL, VenueListSqlDto.class);
    }

    private static final String VENUE_LIST_ALL_SQL = """
        SELECT v.slug as venue_slug, v.name as venue_name, r.slug as region_slug, r.name as region_name, r.country_code, (SELECT count(*) FROM contest_event e WHERE e.venue_id = v.id) as event_count, v.latitude, v.longitude, v.id
        FROM venue v
        LEFT OUTER JOIN region r ON r.id = v.region_id
        ORDER BY v.name""";

    public static List<VenueListSqlDto> venueListAll(EntityManager entityManager) {
        return SqlExec.execute(entityManager, VENUE_LIST_ALL_SQL, VenueListSqlDto.class);
    }

    private static final String VENUE_LIST_UNUSED_SQL = """
        SELECT v.slug as venue_slug, v.name as venue_name, r.slug as region_slug, r.name as region_name, r.country_code, 0 as event_count, v.latitude, v.longitude, v.id
        FROM venue v
        LEFT OUTER JOIN region r ON r.id = v.region_id
        WHERE NOT EXISTS (SELECT * FROM contest_event WHERE venue_id = v.id)
        AND NOT EXISTS (SELECT * FROM venue WHERE parent_id = v.id)
        ORDER BY v.name""";

    public static List<VenueListSqlDto> unusedVenues(EntityManager entityManager) {
        return SqlExec.execute(entityManager, VENUE_LIST_UNUSED_SQL, VenueListSqlDto.class);
    }

    private static final String VENUE_LIST_NO_LOCATION_SQL = """
        SELECT v.slug as venue_slug, v.name as venue_name, r.slug as region_slug, r.name as region_name, r.country_code, 0 as event_count, v.latitude, v.longitude, v.id
        FROM venue v
        LEFT OUTER JOIN region r ON r.id = v.region_id
        WHERE v.latitude IS NULL OR LENGTH(TRIM(v.latitude)) = 0 OR v.longitude IS NULL OR LENGTH(TRIM(v.longitude)) = 0
        ORDER BY v.name""";

    public static List<VenueListSqlDto> noLocationVenues(EntityManager entityManager) {
        return SqlExec.execute(entityManager, VENUE_LIST_NO_LOCATION_SQL, VenueListSqlDto.class);
    }
}
