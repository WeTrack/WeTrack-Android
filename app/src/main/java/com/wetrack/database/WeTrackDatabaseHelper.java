package com.wetrack.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.wetrack.model.Chat;
import com.wetrack.model.ChatMessage;
import com.wetrack.model.Location;
import com.wetrack.model.User;

import java.sql.SQLException;

public class WeTrackDatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String TAG = WeTrackDatabaseHelper.class.getCanonicalName();

    private static final String DATABASE_NAME = "wetrack.db";
    private static final int DATABASE_VERSION = 4;

    private RuntimeExceptionDao<User, String> userDao;
    private RuntimeExceptionDao<Location, Integer> locationDao;
    private RuntimeExceptionDao<Chat, String> chatDao;
    private RuntimeExceptionDao<ChatMessage, Integer> chatMessageDao;
    private FriendDao friendDao;
    private UserChatDao userChatDao;

    public WeTrackDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, User.class);
            TableUtils.createTable(connectionSource, Location.class);
            TableUtils.createTable(connectionSource, Chat.class);
            TableUtils.createTable(connectionSource, UserChat.class);
            TableUtils.createTable(connectionSource, ChatMessage.class);
            TableUtils.createTable(connectionSource, Friend.class);
        } catch (SQLException e) {
            Log.e(TAG, "Failed to create tables: ", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, User.class, true);
            TableUtils.dropTable(connectionSource, Location.class, true);
            TableUtils.dropTable(connectionSource, Chat.class, true);
            TableUtils.dropTable(connectionSource, ChatMessage.class, true);
            TableUtils.dropTable(connectionSource, Friend.class, true);
            TableUtils.dropTable(connectionSource, UserChat.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            Log.e(TAG, "Failed to drop tables: ", e);
        }
    }

    public RuntimeExceptionDao<User, String> getUserDao() {
        if (userDao == null)
            userDao = getRuntimeExceptionDao(User.class);
        return userDao;
    }

    public RuntimeExceptionDao<Location, Integer> getLocationDao() {
        if (locationDao == null)
            locationDao = getRuntimeExceptionDao(Location.class);
        return locationDao;
    }

    public RuntimeExceptionDao<Chat, String> getChatDao() {
        if (chatDao == null)
            chatDao = getRuntimeExceptionDao(Chat.class);
        return chatDao;
    }

    public RuntimeExceptionDao<ChatMessage, Integer> getChatMessageDao() {
        if (chatMessageDao == null)
            chatMessageDao = getRuntimeExceptionDao(ChatMessage.class);
        return chatMessageDao;
    }

    public FriendDao getFriendDao() {
        if (friendDao == null)
            friendDao = new FriendDao(getUserDao(), getRuntimeExceptionDao(Friend.class));
        return friendDao;
    }

    public UserChatDao getUserChatDao() {
        if (userChatDao == null)
            userChatDao = UserChatDao.of(getChatDao(), getRuntimeExceptionDao(UserChat.class));
        return userChatDao;
    }
}
