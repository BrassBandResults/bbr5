package uk.co.bbr.services.framework.sql;

import java.math.BigInteger;
import java.sql.Date;
import java.time.LocalDate;

public abstract class AbstractSqlDto {

    protected Integer getInteger(Object[] columnList, int position) {
        if (columnList[position] == null) {
            return null;
        }
        return columnList[position] instanceof BigInteger ? ((BigInteger)columnList[position]).intValue() : (Integer)columnList[position];
    }

    protected Long getLong(Object[] columnList, int position) {
        if (columnList[position] == null) {
            return null;
        }
        return columnList[position] instanceof BigInteger ? ((BigInteger)columnList[position]).longValue() : (Integer)columnList[position];
    }

    protected LocalDate getLocalDate(Object[] columnList, int position) {
        Date tempEventDate = (Date)columnList[position];
        if (tempEventDate == null) {
            return null;
        }
        return tempEventDate.toLocalDate();
    }
}
