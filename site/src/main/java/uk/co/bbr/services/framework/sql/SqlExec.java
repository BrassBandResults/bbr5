package uk.co.bbr.services.framework.sql;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class SqlExec {
    public static <T extends AbstractSqlDto> List<T> execute(EntityManager entityManager, String sql, Long id, Class<T> clazz) {
        List<T> returnData = new ArrayList<>();
        try {
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter(1, id);
            List<Object[]> queryResults = query.getResultList();

            for (Object[] eachRowData : queryResults) {
                Constructor<T> ctor = clazz.getConstructor(List.class);
                T eachReturnObject = ctor.newInstance(queryResults);
                returnData.add(eachReturnObject);
            }

            return returnData;
        } catch (Exception e) {
            throw new RuntimeException("SQL Failure, " + e.getMessage());
        }
    }
}
