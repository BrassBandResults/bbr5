package uk.co.bbr.services.events.sql;

import lombok.experimental.UtilityClass;
import uk.co.bbr.services.events.sql.dto.ContestResultDrawPositionSqlDto;
import uk.co.bbr.services.framework.sql.SqlExec;

import javax.persistence.EntityManager;
import java.util.List;

@UtilityClass
public class ContestResultSql1 {

    private static final String CONTEST_FILTERED_TO_POSITION_SQL = """
            SELECT e.date_of_event, e.date_resolution, c.slug, r.result_position, r.result_position_type, r.band_name, b.name as actual_band_name, b.slug as band_slug, reg.name as region_name, reg.slug as region_slug, reg.country_code, r.draw, r.points_total,
                                  con1.slug as c1_slug, con1.first_names as c1_first_names, con1.surname as c1_surname,
                                  con2.slug as c2_slug, con2.first_names as c2_first_names, con2.surname as c2_surname,
                                  con3.slug as c3_slug, con3.first_names as c3_first_names, con3.surname as c3_surname
                           FROM contest_result r
                           INNER JOIN contest_event e on e.id = r.contest_event_id
                           INNER JOIN contest c ON c.id = e.contest_id
                           INNER JOIN band b ON b.id = r.band_id
                           INNER JOIN region reg ON reg.id = b.region_id
                           LEFT OUTER JOIN person con1 ON con1.id = r.conductor_id
                           LEFT OUTER JOIN person con2 ON con2.id = r.conductor_two_id
                           LEFT OUTER JOIN person con3 ON con3.id = r.conductor_three_id
                           WHERE c.slug = ?1
                           AND r.result_position_type = ?2
                           AND r.result_position = ?3
                           ORDER BY e.date_of_event DESC""";

    public static List<ContestResultDrawPositionSqlDto> selectContestResultsForPosition(EntityManager entityManager, String contestSlug, String positionType, String position) {
        return SqlExec.execute(entityManager, CONTEST_FILTERED_TO_POSITION_SQL, contestSlug, positionType, position, ContestResultDrawPositionSqlDto.class);
    }

    private static final String CONTEST_FILTERED_TO_DRAW_SQL = """
            SELECT e.date_of_event, e.date_resolution, c.slug, r.result_position, r.result_position_type, r.band_name, b.name as actual_band_name, b.slug as band_slug, reg.name as region_name, reg.slug as region_slug, reg.country_code, r.draw, r.points_total,
                                  con1.slug as c1_slug, con1.first_names as c1_first_names, con1.surname as c1_surname,
                                  con2.slug as c2_slug, con2.first_names as c2_first_names, con2.surname as c2_surname,
                                  con3.slug as c3_slug, con3.first_names as c3_first_names, con3.surname as c3_surname
                           FROM contest_result r
                           INNER JOIN contest_event e on e.id = r.contest_event_id
                           INNER JOIN contest c ON c.id = e.contest_id
                           INNER JOIN band b ON b.id = r.band_id
                           INNER JOIN region reg ON reg.id = b.region_id
                           LEFT OUTER JOIN person con1 ON con1.id = r.conductor_id
                           LEFT OUTER JOIN person con2 ON con2.id = r.conductor_two_id
                           LEFT OUTER JOIN person con3 ON con3.id = r.conductor_three_id
                           WHERE c.slug = ?1
                           AND r.draw = ?2
                           ORDER BY e.date_of_event DESC""";

    public static List<ContestResultDrawPositionSqlDto> selectContestResultsForDraw(EntityManager entityManager, String contestSlug, Integer draw) {
        return SqlExec.execute(entityManager, CONTEST_FILTERED_TO_DRAW_SQL, contestSlug, draw, ContestResultDrawPositionSqlDto.class);
    }

}

