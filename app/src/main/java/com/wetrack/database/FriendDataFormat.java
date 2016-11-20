package com.wetrack.database;

import android.util.Log;

import com.wetrack.BaseApplication;
import com.wetrack.utils.ConstantValues;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by moziliang on 16/11/19.
 */
public class FriendDataFormat extends DataFormat {
    public static String ATTRI_USERNAME = "USERNAME";
    public static String ATTRI_FRIEND_NAME = "FRIEND_NAME";
    public static String ATTRI_FRIEND_GENDER = "FRIEND_GENDER";
    static private String DATABASE_TABLE_NAME = "FRIEND";
    static private String KEY_COLUMN_NAME = ATTRI_FRIEND_NAME;

    static private DataBaseManager mDataBaseManager = null;

    private void init() {
        if (mDataBaseManager == null) {
            setDatabaseTableName(DATABASE_TABLE_NAME);
            setAllDataNames(new ArrayList<String>(
                    Arrays.asList(
                            ATTRI_USERNAME, ATTRI_FRIEND_NAME,
                            ATTRI_FRIEND_GENDER)));
            setKeyColumnName(KEY_COLUMN_NAME);
            mDataBaseManager = DataBaseManager.getInstance(BaseApplication.getContext());
            mDataBaseManager.createTable(getDatabaseTableName(), getAllDataNames());
        }
    }

    public FriendDataFormat(String tableName, ArrayList<String> allDataNames, String keyColumnName) {
        super(tableName, allDataNames, keyColumnName);
    }

    public FriendDataFormat(DataFormat dataFormat) {
        super(DATABASE_TABLE_NAME, new ArrayList<String>(
                Arrays.asList(
                        ATTRI_USERNAME, ATTRI_FRIEND_NAME,
                        ATTRI_FRIEND_GENDER)), KEY_COLUMN_NAME);
        init();
        setValueByName(ATTRI_USERNAME, dataFormat.getValueByName(ATTRI_USERNAME));
        setValueByName(ATTRI_FRIEND_NAME, dataFormat.getValueByName(ATTRI_FRIEND_NAME));
        setValueByName(ATTRI_FRIEND_GENDER, dataFormat.getValueByName(ATTRI_FRIEND_GENDER));
    }

    public FriendDataFormat(String username, String friendName, String friendGender) {
        super(DATABASE_TABLE_NAME, new ArrayList<String>(
                Arrays.asList(
                        ATTRI_USERNAME, ATTRI_FRIEND_NAME,
                        ATTRI_FRIEND_GENDER)), KEY_COLUMN_NAME);
        init();
        setValueByName(ATTRI_USERNAME, username);
        setValueByName(ATTRI_FRIEND_NAME, friendName);
        setValueByName(ATTRI_FRIEND_GENDER, friendGender);
    }

    static public ArrayList<DataFormat> getAllFriend() {
//        init();
        return mDataBaseManager.getAllRows(
                DATABASE_TABLE_NAME,
                new ArrayList<String>(Arrays.asList(
                        ATTRI_USERNAME, ATTRI_FRIEND_NAME,
                        ATTRI_FRIEND_GENDER)),
                KEY_COLUMN_NAME, null);
    }

    public boolean addFriend() {
        init();
        boolean result = mDataBaseManager.insert(this);
        showAllRows();
        return result;
    }

    public boolean removeFriend() {
        init();
        return mDataBaseManager.remove(this);
    }

    public boolean editFriendByName() {
        init();
        return mDataBaseManager.update(this);
    }


    public void showAllRows() {
        init();
        String answer = "";
        for (DataFormat dataFormat : getAllFriend()) {
            answer += dataFormat.toString() + "\n";
        }
        Log.d(ConstantValues.databaseDebug, answer);
    }
}
