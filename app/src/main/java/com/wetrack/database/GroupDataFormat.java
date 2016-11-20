package com.wetrack.database;

import android.util.Log;

import com.wetrack.BaseApplication;
import com.wetrack.utils.ConstantValues;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by moziliang on 16/11/20.
 */
public class GroupDataFormat extends DataFormat {
    public static String ATTRI_ID = "ID";
    public static String ATTRI_NAME = "NAME";
    public static String ATTRI_MEMBERS = "MEMBERS";
    static private String DATABASE_TABLE_NAME = "CHAT_GROUP";
    static private String KEY_COLUMN_NAME = ATTRI_ID;

    static private DataBaseManager mDataBaseManager = null;

    public ArrayList<String> getGroupMembers() {
        String result = getValueByName(ATTRI_MEMBERS);
        ArrayList<String> allMemberName = new ArrayList<>();
        for (String memberName : result.split("\\" + ConstantValues.NAME_SEPERATE_STRING)) {
            if (!memberName.equals("")) {
                allMemberName.add(memberName);
            }
        }
        return allMemberName;
    }

    public void setGroupMembers(ArrayList<String> groupMembers) {
        String result = "";
        for (String memberName : groupMembers) {
            result += memberName + ConstantValues.NAME_SEPERATE_STRING;
        }
        setValueByName(ATTRI_MEMBERS, result);
    }

    private void init() {
        if (mDataBaseManager == null) {
            setDatabaseTableName(DATABASE_TABLE_NAME);
            setAllDataNames(new ArrayList<String>(
                    Arrays.asList(
                            ATTRI_ID, ATTRI_NAME,
                            ATTRI_MEMBERS)));
            setKeyColumnName(KEY_COLUMN_NAME);
            mDataBaseManager = DataBaseManager.getInstance(BaseApplication.getContext());
            mDataBaseManager.createTable(getDatabaseTableName(), getAllDataNames());
        }
    }

    public GroupDataFormat(DataFormat dataFormat) {
        super(DATABASE_TABLE_NAME, new ArrayList<String>(
                Arrays.asList(
                        ATTRI_ID, ATTRI_NAME,
                        ATTRI_MEMBERS)), KEY_COLUMN_NAME);
        init();
        setValueByName(ATTRI_ID, dataFormat.getValueByName(ATTRI_ID));
        setValueByName(ATTRI_NAME, dataFormat.getValueByName(ATTRI_NAME));
        setValueByName(ATTRI_MEMBERS, dataFormat.getValueByName(ATTRI_MEMBERS));
    }

    public GroupDataFormat(String groupName, ArrayList<String> groupMembers) {
        super(DATABASE_TABLE_NAME, new ArrayList<String>(
                Arrays.asList(
                        ATTRI_ID, ATTRI_NAME,
                        ATTRI_MEMBERS)), KEY_COLUMN_NAME);
        init();

        ArrayList<DataFormat> dataFormats;
        String randomId;
        do {
            randomId = getRandomId();
            Map<String, String> whereMap = new HashMap<>();
            whereMap.put(ATTRI_ID, randomId);

            dataFormats = mDataBaseManager.getAllRows(
                    getDatabaseTableName(), getAllDataNames(), getKeyColumnName(), whereMap);
        } while (dataFormats.size() != 0);


        setValueByName(ATTRI_ID, randomId);
        setValueByName(ATTRI_NAME, groupName);
        setGroupMembers(groupMembers);
    }

    public String getRandomId() {
        String id = "";
        int idLength = 16;
        String possibleIdChar = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < idLength; i++) {
            id += possibleIdChar.charAt(random.nextInt(possibleIdChar.length()));
        }
        return id;
    }

    static public ArrayList<DataFormat> getAllGroups() {
//        init();
        return mDataBaseManager.getAllRows(
                DATABASE_TABLE_NAME,
                new ArrayList<String>(Arrays.asList(
                        ATTRI_ID, ATTRI_NAME,
                        ATTRI_MEMBERS)),
                KEY_COLUMN_NAME, null);
    }

    public boolean addGroup() {
        init();
        boolean result = mDataBaseManager.insert(this);
        showAllRows();
        return result;
    }

    public boolean removeGroup() {
        init();
        boolean result = mDataBaseManager.remove(this);
        showAllRows();
        return result;
    }

    public boolean editGroup() {
        init();
        return mDataBaseManager.update(this);
    }


    public void showAllRows() {
        init();
        String answer = "\nGROUP:\n";
        for (DataFormat dataFormat : getAllGroups()) {
            answer += dataFormat.toString() + "\n";
        }
        Log.d(ConstantValues.databaseDebug, answer);
    }
}