package com.wetrack.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by moziliang on 16/10/1.
 */
public class DataFormat {
    public DataFormat(String tableName, ArrayList<String>allDataNames, String keyColumnName) {
        databaseTableName = tableName;
        this.allDataNames = allDataNames;
        this.keyColumnName = keyColumnName;
    }

    private String databaseTableName;
    public String getDatabaseTableName() {
        return databaseTableName;
    }
    public void setDatabaseTableName(String databaseTableName) {
        this.databaseTableName = databaseTableName;
    }

    private ArrayList<String> allDataNames = new ArrayList<>();
    public ArrayList<String> getAllDataNames() {
        return allDataNames;
    }
    public void setAllDataNames(ArrayList<String> allDataNames) {
        this.allDataNames = allDataNames;
    }

    private String keyColumnName;
    public String getKeyColumnName() {
        return keyColumnName;
    }
    public void setKeyColumnName(String keyColumnName) {
        this.keyColumnName = keyColumnName;
    }

    private Map<String, String> dataNameAndValues = new HashMap<>();
    public String getValueByName(String dataName) {
        if (!allDataNames.contains(dataName)) {
            return null;
        }
        if (dataNameAndValues.containsKey(dataName)) {
            return dataNameAndValues.get(dataName);
        } else {
            return null;
        }
    }
    public void setValueByName(String dataName, String value) {
        dataNameAndValues.put(dataName, value);
    }
    public Map<String, String> getDataNameAndValues() {
        return dataNameAndValues;
    }
    public void setDataNameAndValues(Map<String, String> dataNameAndValues) {
        this.dataNameAndValues = dataNameAndValues;
    }

    @Override
    public String toString() {
        String answer = "";
        if (allDataNames != null && !allDataNames.isEmpty()) {
            for (String name : allDataNames) {
                answer += dataNameAndValues.get(name) + ", ";
            }
        }
        return answer;
    }
}
