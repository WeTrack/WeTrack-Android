package com.wetrack.database;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.wetrack.model.ChatMessage;

import org.joda.time.LocalDateTime;

import java.sql.SQLException;
import java.util.List;

public class ChatMessageDao extends RuntimeExceptionDao<ChatMessage, String> {
    private static final String TAG = ChatMessageDao.class.getCanonicalName();

    public ChatMessageDao(Dao<ChatMessage, ?> dao) {
        super((Dao<ChatMessage, String>) dao);
    }

    public List<ChatMessage> getMessageSince(LocalDateTime sinceTime) {
        PreparedQuery<ChatMessage> query;

        try {
            query = queryBuilder().orderBy("send_time", true)
                    .where().gt("send_time", sinceTime).prepare();
        } catch (SQLException ex) {
            Log.e(TAG, "Failed to create PreparedQuery for chat message: ", ex);
            throw new RuntimeException("Failed to create PreparedQuery for chat message: ", ex);
        }

        return query(query);
    }
}
