package com.wetrack.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by moziliang on 16/10/1.
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "WeTrack";
    private static final int DATABASE_VERSION = 2;

    public MySQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
//        for (int i = 0; i < allCreateCommands.length; i++) {
//            db.execSQL(allCreateCommands[i]);
//        }
//        db.execSQL(SQL_DELETE_ENTRIES);
//        onCreate(db);
    }
}