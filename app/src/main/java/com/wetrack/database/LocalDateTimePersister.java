package com.wetrack.database;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.BaseDataType;
import com.j256.ormlite.support.DatabaseResults;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.sql.SQLException;

public class LocalDateTimePersister extends BaseDataType {
    private static final LocalDateTimePersister instance = new LocalDateTimePersister();

    /* This method must exist. */
    public static LocalDateTimePersister getSingleton() {
        return instance;
    }

    public LocalDateTimePersister() {
        super(SqlType.STRING, new Class[]{ LocalDate.class, LocalDateTime.class});
    }

    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) throws SQLException {
        return javaObject.toString();
    }

    @Override
    public Object parseDefaultString(FieldType fieldType, String defaultStr) throws SQLException {
        if (fieldType.getField().getType().equals(LocalDate.class)) {
            try {
                LocalDate.parse(defaultStr);
            } catch (Exception e) {
                throw new SQLException("Failed to parse given default string `" + defaultStr + "` as LocalDate.", e);
            }
            return defaultStr;
        } else if (fieldType.getField().getType().equals(LocalDateTime.class)) {
            try {
                LocalDateTime.parse(defaultStr);
            } catch (Exception e) {
                throw new SQLException("Failed to parse given default string `" + defaultStr + "` as LocalDateTime.", e);
            }
            return defaultStr;
        }
        throw new SQLException("Given Java field type " + fieldType.getField().getType().getName()
                + " is not supported by LocalDateTimePersister.");
    }

    @Override
    public Object resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
        return results.getString(columnPos);
    }

    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) throws SQLException {
        if (fieldType.getField().getType().equals(LocalDate.class)) {
            try {
                return LocalDate.parse((String) sqlArg);
            } catch (Exception e) {
                throw new SQLException("Failed to parse given SQL argument `" + sqlArg + "` as LocalDate.", e);
            }
        } else if (fieldType.getField().getType().equals(LocalDateTime.class)) {
            try {
                return LocalDateTime.parse((String) sqlArg);
            } catch (Exception e) {
                throw new SQLException("Failed to parse given SQL argument `" + sqlArg + "` as LocalDateTime.", e);
            }
        }
        throw new SQLException("Given Java field type " + fieldType.getField().getType().getName()
                + " is not supported by LocalDateTimePersister.");
    }
}
