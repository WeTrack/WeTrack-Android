package com.wetrack.database;

import com.wetrack.BaseApplication;

import java.util.ArrayList;
import java.util.Arrays;

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

    static public void setDatabaseTableName() {
        setDatabaseTableName("USER");
    }
    static public void setAllDataNames() {
        setAllDataNames(new ArrayList<String>(
                Arrays.asList(ATTRI_USERNAME, ATTRI_PASSWORD,
                        ATTRI_NICKNAME, ATTRI_ICONURL,
                        ATTRI_EMAIL, ATTRI_GENDER,ATTRI_BIRTHDATE)));
    }

    static private DataBaseManager mDataBaseManager;
    public UserDataFormat(String username, String password, String nickname,
        String iconUrl, String email, String gender, String birthDate) {
        setValueByName(ATTRI_USERNAME, username);
        setValueByName(ATTRI_PASSWORD, password);
        setValueByName(ATTRI_NICKNAME, nickname);
        setValueByName(ATTRI_ICONURL, iconUrl);
        setValueByName(ATTRI_EMAIL, email);
        setValueByName(ATTRI_GENDER, gender);
        setValueByName(ATTRI_BIRTHDATE, birthDate);

        setDatabaseTableName();
        setAllDataNames();

        mDataBaseManager = DataBaseManager.getInstance(BaseApplication.getContext());
        mDataBaseManager.createTable(getDatabaseTableName(), getAllDataNames());
    }

    static public ArrayList<DataFormat> getAllUser() {
        return mDataBaseManager.getAllRows(getDatabaseTableName(), getAllDataNames());
    }

    public boolean addUser() {
        return mDataBaseManager.insert(this, ATTRI_USERNAME);
    }

    public boolean removeUser() {
        return mDataBaseManager.remove(this, ATTRI_USERNAME);
    }

    public boolean editAccountByName() {
        return mDataBaseManager.update(this, ATTRI_USERNAME);
    }
}
