package com.wetrack.database;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.BaseDataType;
import com.j256.ormlite.support.DatabaseResults;

import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class StringListPersister extends BaseDataType {
    private static final StringListPersister instance = new StringListPersister();

    private static final String DELIMITER = ",";

    /* This method must exist. */
    public static StringListPersister getSingleton() {
        return instance;
    }

    public StringListPersister() {
        super(SqlType.STRING, new Class[]{ List.class });
    }

    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) throws SQLException {
        if (fieldType.getField().getType().equals(List.class))
            return StringUtils.join((List<String>) javaObject, DELIMITER);
        throw new SQLException("The given Java type " + fieldType.getField().getType().getName()
                + " is not supported by StringListPersister.");
    }

    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) throws SQLException {
        if (fieldType.getField().getType().equals(List.class))
            return Arrays.asList(((String) sqlArg).split(DELIMITER));
        throw new SQLException("The given Java type " + fieldType.getField().getType().getName()
                + " is not supported by StringListPersister.");
    }

    @Override
    public Object parseDefaultString(FieldType fieldType, String defaultStr) throws SQLException {
        return defaultStr;
    }

    @Override
    public Object resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
        return results.getString(columnPos);
    }
}
