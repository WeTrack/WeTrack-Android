package com.wetrack.database;

import android.util.Log;

import com.wetrack.BaseApplication;
import com.wetrack.utils.ConstantValues;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by moziliang on 16/10/1.
 */
public class UserDataFormat extends DataFormat {
    public static String ATTRI_USERNAME = "USERNAME";
    public static String ATTRI_PASSWORD = "PASSWORD";
    public static String ATTRI_NICKNAME = "NICKNAME";
    public static String ATTRI_ICONURL = "ICONURL";
    public static String ATTRI_EMAIL = "EMAIL";
    public static String ATTRI_GENDER = "GENDER";
    public static String ATTRI_BIRTHDATE = "BIRTHDATE";

    static private String DATABASE_TABLE_NAME = "USER";
    static private String KEY_COLUMN_NAME = ATTRI_USERNAME;

    private DataBaseManager mDataBaseManager = null;

    private void init() {
        if (mDataBaseManager == null) {
            setDatabaseTableName(DATABASE_TABLE_NAME);
            setAllDataNames(new ArrayList<String>(
                    Arrays.asList(
                            ATTRI_USERNAME, ATTRI_PASSWORD, ATTRI_NICKNAME,
                            ATTRI_ICONURL, ATTRI_EMAIL, ATTRI_GENDER,
                            ATTRI_BIRTHDATE)));
            setKeyColumnName(KEY_COLUMN_NAME);
            mDataBaseManager = DataBaseManager.getInstance(BaseApplication.getContext());
            mDataBaseManager.createTable(getDatabaseTableName(), getAllDataNames());
        }
    }

    public UserDataFormat(String username) {
        super(DATABASE_TABLE_NAME, new ArrayList<String>(
                Arrays.asList(
                        ATTRI_USERNAME, ATTRI_PASSWORD, ATTRI_NICKNAME,
                        ATTRI_ICONURL, ATTRI_EMAIL, ATTRI_GENDER,
                        ATTRI_BIRTHDATE)), ATTRI_USERNAME);
        init();

        setValueByName(ATTRI_USERNAME, username);
    }

    public UserDataFormat(String username, String password) {
        super(DATABASE_TABLE_NAME, new ArrayList<String>(
                Arrays.asList(
                        ATTRI_USERNAME, ATTRI_PASSWORD, ATTRI_NICKNAME,
                        ATTRI_ICONURL, ATTRI_EMAIL, ATTRI_GENDER,
                        ATTRI_BIRTHDATE)), ATTRI_USERNAME);
        init();

        setValueByName(ATTRI_USERNAME, username);
        setValueByName(ATTRI_PASSWORD, password);
    }

    public UserDataFormat(String tableName, ArrayList<String> allDataNames, String keyColumnName) {
        super(tableName, allDataNames, keyColumnName);
    }

    public UserDataFormat(String username, String password, String nickname,
                          String iconUrl, String email, String gender, String birthDate) {
        super(DATABASE_TABLE_NAME, new ArrayList<String>(
                Arrays.asList(
                        ATTRI_USERNAME, ATTRI_PASSWORD, ATTRI_NICKNAME,
                        ATTRI_ICONURL, ATTRI_EMAIL, ATTRI_GENDER,
                        ATTRI_BIRTHDATE)), ATTRI_USERNAME);
        init();

        setValueByName(ATTRI_USERNAME, username);
        setValueByName(ATTRI_PASSWORD, password);
        setValueByName(ATTRI_NICKNAME, nickname);
        setValueByName(ATTRI_ICONURL, iconUrl);
        setValueByName(ATTRI_EMAIL, email);
        setValueByName(ATTRI_GENDER, gender);
        setValueByName(ATTRI_BIRTHDATE, birthDate);
    }

    public ArrayList<DataFormat> getAllUser() {
        init();
        return mDataBaseManager.getAllRows(getDatabaseTableName(), getAllDataNames(), getKeyColumnName(), null);
    }

    public void getDataByUsername() {
        init();
        Map<String, String> whereMap = new HashMap<>();
        whereMap.put(ATTRI_USERNAME, getValueByName(ATTRI_USERNAME));

        ArrayList<DataFormat> dataFormats = mDataBaseManager.getAllRows(
                getDatabaseTableName(), getAllDataNames(), getKeyColumnName(), whereMap);
        try {
            if (dataFormats.size() == 1) {
                DataFormat dataFormat = dataFormats.get(0);
                setDataNameAndValues(dataFormat.getDataNameAndValues());
                setAllDataNames(dataFormat.getAllDataNames());
                setKeyColumnName(dataFormat.getKeyColumnName());
                setDatabaseTableName(dataFormat.getDatabaseTableName());
            } else {
                throw new Exception("dataFormats.size() != 1");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean authentication() {
        init();
        Map<String, String> whereMap = new HashMap<>();
        whereMap.put(ATTRI_USERNAME, getValueByName(ATTRI_USERNAME));

        ArrayList<DataFormat> dataFormats = mDataBaseManager.getAllRows(
                getDatabaseTableName(), getAllDataNames(), getKeyColumnName(), whereMap);
        DataFormat dataFormat = null;
        try {
            if (dataFormats.size() == 1) {
                dataFormat = dataFormats.get(0);
            } else {
                throw new Exception("dataFormats.size() != 1");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String password = dataFormat.getValueByName(ATTRI_PASSWORD);
//        Log.d(ConstantValues.debugTab, "password: " + password);

        return getValueByName(ATTRI_PASSWORD).equals(password);
    }

    public boolean addUser() {
        init();
        boolean result = mDataBaseManager.insert(this);
//        showAllRows();
        return result;
    }

    public boolean removeUser() {
        init();
        return mDataBaseManager.remove(this);
    }

    public boolean editAccountByName() {
        init();
        return mDataBaseManager.update(this);
    }

    public void showAllRows() {
        init();
        String answer = "";
        for (DataFormat dataFormat : getAllUser()) {
            answer += dataFormat.toString() + "\n";
        }
        Log.d(ConstantValues.databaseDebug, answer);
    }
}
