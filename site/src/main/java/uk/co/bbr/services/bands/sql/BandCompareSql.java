package uk.co.bbr.services.bands.sql;

import lombok.experimental.UtilityClass;
import uk.co.bbr.services.bands.sql.dto.CompareBandsSqlDto;
import uk.co.bbr.services.framework.sql.SqlExec;
import jakarta.persistence.EntityManager;
import java.util.List;

@UtilityClass
public class BandCompareSql {

    private static final String COMPARE_BANDS_SQL = """
    SELECT   band_one_result.result_position as left_position,
             band_one_result.result_position_type as left_position_type,
             band_two_result.result_position as right_position,
             band_two_result.result_position_type as right_position_type,
             e.date_of_event,
             c.slug,
             c.name,
             e.date_resolution,
             band_one_result.band_name as left_band_name,
             band_two_result.band_name as right_band_name,
             left_conductor.first_names as left_conductor_first_names,
             left_conductor.surname as left_conductor_surname,
             left_conductor.known_for as left_conductor_known_for,
             left_conductor.slug as left_conductor_slug,
             right_conductor.first_names as right_conductor_first_names,
             right_conductor.surname as right_conductor_surname,
             right_conductor.known_for as right_conductor_known_for,
             right_conductor.slug as right_conductor_slug
    FROM contest_event e
    INNER JOIN contest_result band_one_result ON band_one_result.contest_event_id = e.id
    INNER JOIN contest_result band_two_result ON band_two_result.contest_event_id = e.id
    INNER JOIN contest c ON c.id = e.contest_id
    LEFT OUTER JOIN person left_conductor ON left_conductor.id = band_one_result.conductor_id
    LEFT OUTER JOIN person right_conductor ON right_conductor.id = band_two_result.conductor_id
    WHERE band_one_result.band_id = ?1
    AND band_two_result.band_id = ?2
    AND band_one_result.contest_event_id = band_two_result.contest_event_id
    AND e.id = band_one_result.contest_event_id
    ORDER BY e.date_of_event, c.name DESC""";
    public static List<CompareBandsSqlDto> compareBands(EntityManager entityManager, long leftBandId, long rightBandId) {
        return SqlExec.execute(entityManager, COMPARE_BANDS_SQL, leftBandId, rightBandId, CompareBandsSqlDto.class);
    }
}

