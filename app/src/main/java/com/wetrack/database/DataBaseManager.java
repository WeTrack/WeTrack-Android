package com.wetrack.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by moziliang on 16/10/1.
 */
public class DataBaseManager {
    static private DataBaseManager mDataBaseManager = null;
    private Context mContext = null;
    static public DataBaseManager getInstance(Context context) {
        if (mDataBaseManager == null) {
            mDataBaseManager = new DataBaseManager(context);
        }
        return mDataBaseManager;
    }

    private MySQLiteOpenHelper MyDB;

    private DataBaseManager(Context context) {
        mContext = context;
        MyDB  = new MySQLiteOpenHelper(mContext);
    }

    public boolean createTable(String tableName, ArrayList<String>allDataNames) {
        ArrayList<String>allTableNames = getAllTables();
        if (allTableNames.contains(tableName)) {
            return false;
        }

        String SQL_CREATE_ENTRIES = "CREATE TABLE " + tableName + " (";
        for (int i = 0; i < allDataNames.size(); i++) {
            SQL_CREATE_ENTRIES += allDataNames.get(i) + " TEXT";
            if (i == allDataNames.size() - 1) {
                SQL_CREATE_ENTRIES += ");";
            } else {
                SQL_CREATE_ENTRIES += ", ";
            }
        }
        SQLiteDatabase db = MyDB.getWritableDatabase();
        db.execSQL(SQL_CREATE_ENTRIES);
        return true;
    }

    public ArrayList<String> getAllTables() {
        SQLiteDatabase db = MyDB.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        ArrayList<String>allTableNames = new ArrayList<>();
        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                allTableNames.add(c.getString( c.getColumnIndex("name")));
                c.moveToNext();
            }
        }
        return allTableNames;
    }

    public boolean deleteTable(String tableName) {
        ArrayList<String>allTableNames = getAllTables();
        if (!allTableNames.contains(tableName)) {
            return false;
        }

        String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + tableName;
        SQLiteDatabase db = MyDB.getWritableDatabase();
        db.execSQL(SQL_DELETE_ENTRIES);
        return true;
    }

    public boolean CheckColumnDuplicated(DataFormat data, String checkedColumnName) {
        if (data.getValueByName(checkedColumnName) == null) {
            return false;
        }
        ArrayList<DataFormat> allRows = getAllRows(data.getDatabaseTableName(), data.getAllDataNames());
        for (DataFormat row : allRows) {
            if (row.getValueByName(checkedColumnName) != null &&
                    row.getValueByName(checkedColumnName).equals(data.getValueByName(checkedColumnName))) {
                return true;
            }
        }
        return false;
    }

    public boolean insert(DataFormat data, String keyColumnName) {
        if (CheckColumnDuplicated(data, keyColumnName)) {
            return false;
        }

        SQLiteDatabase db = MyDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        for (int i = 0; i < data.getAllDataNames().size(); i++) {
            String name = data.getAllDataNames().get(i);
            values.put(name, data.getValueByName(name));
        }
        db.insert(data.getDatabaseTableName(), null, values);
        return true;
    }

    public boolean remove(DataFormat data, String keyColumnName) {
        if (!CheckColumnDuplicated(data, keyColumnName)) {
            return false;
        }
        SQLiteDatabase db = MyDB.getWritableDatabase();
        String whereClause = keyColumnName + " = ?";
        String[] whereArgs = new String[] {data.getValueByName(keyColumnName)};
        db.delete(data.getDatabaseTableName(), whereClause, whereArgs);
        return true;
    }

    public boolean update(DataFormat data, String keyColumnName) {
        if (!CheckColumnDuplicated(data, keyColumnName)) {
            return false;
        }
        SQLiteDatabase db = MyDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        for (int i = 0; i < data.getAllDataNames().size(); i++) {
            String name = data.getAllDataNames().get(i);
            values.put(name, data.getValueByName(name));
        }
        String whereClause = keyColumnName + " = ?";
        String[] whereArgs = new String[] {data.getValueByName(keyColumnName)};
        db.update(data.getDatabaseTableName(), values, whereClause, whereArgs);
        return true;
    }

    public ArrayList<DataFormat> getAllRows(String tableName, ArrayList<String> allDataNames) {
        SQLiteDatabase db = MyDB.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.

//        String whereClause = UserDataFormat.KEY_ACCOUNT_NAME + " = ? OR " + UserDataFormat.KEY_CURRENCY_TYPE + " = ?";
//        String[] whereArgs = new String[] {
//                "accountname1",
//                "currencytype1"
//        };

        // How you want the results sorted in the resulting Cursor
//        String sortOrder =
//                UserDataFormat.KEY_ACCOUNT_NAME + " DESC";

        Cursor cursor = db.query(
                tableName,  // The table to query
                (String[])allDataNames.toArray(new String[0]), // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        ArrayList<DataFormat>allRows = new ArrayList<>();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                DataFormat row = new DataFormat();
                for (int i = 0; i < allDataNames.size(); i++) {
                    String name = allDataNames.get(i);
                    row.setValueByName(name, cursor.getString(cursor.getColumnIndexOrThrow(name)));
                }
                allRows.add(row);

            } while (cursor.moveToNext());
        }
        return allRows;
    }
}
