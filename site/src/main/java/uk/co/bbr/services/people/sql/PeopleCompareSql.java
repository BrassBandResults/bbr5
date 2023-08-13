package uk.co.bbr.services.people.sql;

import lombok.experimental.UtilityClass;
import uk.co.bbr.services.framework.sql.SqlExec;
import uk.co.bbr.services.people.sql.dto.CompareConductorsSqlDto;
import uk.co.bbr.services.people.sql.dto.PeopleWinnersSqlDto;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

@UtilityClass
public class PeopleCompareSql {

    private static final String COMPARE_CONDUCTORS_SQL = """
    SELECT   conductor_one_result.result_position as left_position,
             conductor_one_result.result_position_type as left_position_type,
             conductor_two_result.result_position as right_position,
             conductor_two_result.result_position_type as right_position_type,
             e.date_of_event,
             c.slug,
             c.name,
             e.date_resolution,
             conductor_one_result.band_name as left_band_name,
             conductor_two_result.band_name as right_band_name,
             conductor_one_band.slug as left_band_slug,
             conductor_two_band.slug as right_band_slug,
             conductor_one_band.name as left_band_real_name,
             conductor_two_band.name as right_band_real_name,
             band_one_region.slug as left_band_region_slug,
             band_one_region.name as left_band_region_name,
             band_one_region.country_code as left_band_region_country_code,
             band_two_region.slug as right_band_region_slug,
             band_two_region.name as right_band_region_name,
             band_two_region.country_code as right_band_region_country_code
    FROM
      contest_result conductor_one_result,
      contest_result conductor_two_result,
      band conductor_one_band,
      band conductor_two_band,
      region band_one_region,
      region band_two_region,
      contest_event e,
      contest c
    WHERE conductor_one_result.conductor_id = ?1
    AND conductor_two_result.conductor_id = ?2
    AND conductor_one_result.contest_event_id = conductor_two_result.contest_event_id
    AND conductor_one_band.id = conductor_one_result.band_id
    AND conductor_two_band.id = conductor_two_result.band_id
    AND band_one_region.id = conductor_one_band.region_id
    AND band_two_region.id = conductor_two_band.region_id
    AND e.id = conductor_one_result.contest_event_id
    AND e.contest_id = c.id
    ORDER BY e.date_of_event, c.name DESC""";
    public static List<CompareConductorsSqlDto> compareConductors(EntityManager entityManager, long leftConductorId, long rightConductorId) {
        return SqlExec.execute(entityManager, COMPARE_CONDUCTORS_SQL, leftConductorId, rightConductorId, CompareConductorsSqlDto.class);
    }
}

