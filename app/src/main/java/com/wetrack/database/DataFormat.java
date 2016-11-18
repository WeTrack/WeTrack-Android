package com.wetrack.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by moziliang on 16/10/1.
 */
public class DataFormat {
    private static String DATABASE_TABLE_NAME;
    public static String getDatabaseTableName() {
        return DATABASE_TABLE_NAME;
    }
    public static void setDatabaseTableName(String databaseTableName) {
        DATABASE_TABLE_NAME = databaseTableName;
    }

    private static ArrayList<String> allDataNames;
    public static ArrayList<String> getAllDataNames() {
        return allDataNames;
    }
    public static void setAllDataNames(ArrayList<String> allDataNames) {
        DataFormat.allDataNames = allDataNames;
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
        if (!allDataNames.contains(dataName)) {
            return;
        }
        dataNameAndValues.put(dataName, value);
    }
}
