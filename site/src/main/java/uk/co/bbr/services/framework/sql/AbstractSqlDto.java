package uk.co.bbr.services.framework.sql;

import java.math.BigInteger;
import java.sql.Date;
import java.time.LocalDate;

public abstract class AbstractSqlDto {

    protected Integer getInteger(Object[] columnList, int position) {
        if (columnList[position] == null) {
            return null;
        }
        if (columnList[position] instanceof Long) {
            return ((Long) columnList[position]).intValue();
        }
        return columnList[position] instanceof BigInteger ? ((BigInteger)columnList[position]).intValue() : (Integer)columnList[position];
    }

    protected Integer getIntegerOrZero(Object[] columnList, int position) {
        Integer value = this.getInteger(columnList, position);
        if (value == null) {
            return 0;
        }
        return value;
    }

    protected Long getLong(Object[] columnList, int position) {
        if (columnList[position] == null) {
            return null;
        }
        return columnList[position] instanceof BigInteger ? ((BigInteger)columnList[position]).longValue() : (Long)columnList[position];
    }

    protected String getString(Object[] columnList, int position) {
        if (columnList[position] == null) {
            return null;
        }
        if (columnList[position] instanceof Character) {
            return String.valueOf(columnList[position]);
        }
        return (String)columnList[position];
    }

    protected LocalDate getLocalDate(Object[] columnList, int position) {
        Date tempEventDate = (Date)columnList[position];
        if (tempEventDate == null) {
            return null;
        }
        return tempEventDate.toLocalDate();
    }

    protected Boolean getBoolean(Object[] columnList, int position) {
        if (columnList[position] == null) {
            return null;
        }
        return (Boolean)columnList[position];
    }
}
