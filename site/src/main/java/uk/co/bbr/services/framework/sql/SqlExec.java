package uk.co.bbr.services.framework.sql;

import lombok.experimental.UtilityClass;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class SqlExec {
    public static <T extends AbstractSqlDto> List<T> execute(EntityManager entityManager, String sql, Object param1, Class<T> clazz) {
        List<T> returnData = new ArrayList<>();
        try {
            Query query = entityManager.createNativeQuery(sql);
            if (param1 != null) {
                query.setParameter(1, param1);
            }
            List<Object[]> queryResults = query.getResultList();

            for (Object[] columnList : queryResults) {
                Constructor<T> ctor = clazz.getConstructor(Object[].class);
                Object[] constructorParams = new Object[1];
                constructorParams[0] = columnList;
                T eachReturnObject = ctor.newInstance(constructorParams);
                returnData.add(eachReturnObject);
            }

            return returnData;
        } catch (Exception e) {
            throw new RuntimeException("SQL Failure, " + e.getMessage(), e);
        }
    }

    public static <T extends AbstractSqlDto> List<T> execute(EntityManager entityManager, String sql, Class<T> clazz) {
        return execute(entityManager, sql, null, clazz);
    }
}
