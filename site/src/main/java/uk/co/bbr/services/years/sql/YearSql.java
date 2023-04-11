package uk.co.bbr.services.years.sql;

import uk.co.bbr.services.pieces.sql.dto.SetTestUsagePieceSqlDto;
import uk.co.bbr.services.pieces.sql.dto.SetTestUsageSqlDto;
import uk.co.bbr.services.years.sql.dto.YearListEntrySqlDto;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

public class YearSql {

    private static final String YEAR_LIST_SQL = """
            SELECT YEAR(e.date_of_event) as year, count(distinct(r.band_id)) as band_count, count(distinct(e.id)) as event_count 
            FROM contest_event e
            INNER JOIN contest_result r ON r.contest_event_id = e.id 
            GROUP BY YEAR(e.date_of_event) 
            ORDER BY YEAR(e.date_of_event) DESC
            """;

    public static List<YearListEntrySqlDto> selectSetTestPieceUsage(EntityManager entityManager) {
        List<YearListEntrySqlDto> returnData = new ArrayList<>();
        try {
            Query query = entityManager.createNativeQuery(YEAR_LIST_SQL);
            List<Object[]> queryResults = query.getResultList();

            for (Object[] eachRowData : queryResults) {
                YearListEntrySqlDto eachReturnObject = new YearListEntrySqlDto(eachRowData);
                returnData.add(eachReturnObject);
            }

            return returnData;
        } catch (Exception e) {
            throw new RuntimeException("SQL Failure, " + e.getMessage());
        }
    }
}
