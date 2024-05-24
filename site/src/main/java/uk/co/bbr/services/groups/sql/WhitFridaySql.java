package uk.co.bbr.services.groups.sql;

import jakarta.persistence.EntityManager;
import uk.co.bbr.services.framework.sql.SqlExec;
import uk.co.bbr.services.groups.sql.dao.WhitFridayResultSqlDto;

import java.time.LocalDate;
import java.util.List;

public class WhitFridaySql {

    private static final String WHIT_FRIDAY_YEAR_OVERALL_RESULTS = """
SELECT b.name as band_name, b.slug as band_slug, r.band_name as band_competed_as,
       reg.slug as region_slug, reg.name as region_name, reg.country_code as region_country_code,
       r.result_position
FROM contest_result r
         INNER JOIN contest_event e ON e.id = r.contest_event_id
         INNER JOIN contest c ON c.id = e.contest_id
         INNER JOIN contest_group g ON g.id = c.contest_group_id
         INNER JOIN band b ON b.id = r.band_id
         LEFT OUTER JOIN region reg ON reg.id = b.region_id
WHERE g.slug = ?1
         AND e.date_of_event > ?2
         AND e.date_of_event < ?3
         AND r.result_position_type = 'R'
         ORDER BY b.id, r.result_position
""";

    public static List<WhitFridayResultSqlDto> fetchWhitFridayResults(EntityManager entityManager, String groupSlug, int year) {
        LocalDate fromDate = LocalDate.of(year, 1, 1);
        LocalDate toDate = LocalDate.of(year,12, 31);
        return SqlExec.execute(entityManager, WHIT_FRIDAY_YEAR_OVERALL_RESULTS, groupSlug, fromDate, toDate, WhitFridayResultSqlDto.class);
    }
}
