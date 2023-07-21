package uk.co.bbr.services.people.sql;

import lombok.experimental.UtilityClass;
import uk.co.bbr.services.framework.sql.SqlExec;
import uk.co.bbr.services.people.sql.dto.AdjudicationsSqlDto;
import uk.co.bbr.services.people.sql.dto.PeopleBandsSqlDto;

import javax.persistence.EntityManager;
import java.util.List;

@UtilityClass
public class AdjudicatorSql {
    private static final String PERSON_ADJUDICATIONS_SQL = """
        SELECT e.name as event_name, e.date_of_event, e.date_resolution, c.slug as contest_slug, w.band_name as winner_name, b.slug as band_slug, b.name as band_name, r.name as region_name, r.slug as region_slug, r.country_code
        FROM contest_event e
        INNER JOIN contest c ON e.contest_id = c.id
        INNER JOIN contest_event_adjudicator cea on e.id = cea.contest_event_id
        LEFT OUTER JOIN contest_result w ON w.contest_event_id = e.id AND w.result_position = 1
        INNER JOIN band b ON b.id = w.band_id
        INNER JOIN region r ON r.id = b.region_id
        WHERE cea.person_id = ?1
        ORDER BY e.date_of_event DESC""";
    public static List<AdjudicationsSqlDto> fetchAdjudications(EntityManager entityManager, Long personId) {
        return SqlExec.execute(entityManager, PERSON_ADJUDICATIONS_SQL, personId, AdjudicationsSqlDto.class);
    }
}
