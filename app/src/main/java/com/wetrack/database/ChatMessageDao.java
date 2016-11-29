package com.wetrack.database;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.Where;
import com.wetrack.model.ChatMessage;

import org.joda.time.LocalDateTime;

import java.sql.SQLException;
import java.util.List;

public class ChatMessageDao extends RuntimeExceptionDao<ChatMessage, String> {
    private static final String TAG = ChatMessageDao.class.getCanonicalName();

    public ChatMessageDao(Dao<ChatMessage, ?> dao) {
        super((Dao<ChatMessage, String>) dao);
    }

    public List<ChatMessage> getMessageSince(String chatId, LocalDateTime sinceTime, LocalDateTime beforeTime) {
        PreparedQuery<ChatMessage> query;

        try {
            Where<ChatMessage, String> builder = queryBuilder().orderBy("send_time", true)
                    .where().eq("chat_id", chatId).and().gt("send_time", sinceTime);
            if (beforeTime != null)
                builder.and().lt("send_time", beforeTime);
            query = builder.prepare();
        } catch (SQLException ex) {
            Log.e(TAG, "Failed to create PreparedQuery for chat message: ", ex);
            throw new RuntimeException("Failed to create PreparedQuery for chat message: ", ex);
        }

        return query(query);
    }

    public List<ChatMessage> getMessageBefore(String chatId, LocalDateTime beforeTime, Long limit) {
        PreparedQuery<ChatMessage> query;
        try {
            query = queryBuilder().orderBy("send_time", true).limit(limit)
                    .where().eq("chat_id", chatId).and().lt("send_time", beforeTime).prepare();
        } catch (SQLException ex) {
            Log.e(TAG, "Failed to create PreparedQuery for chat message: ", ex);
            throw new RuntimeException("Failed to create PreparedQuery for chat message: ", ex);
        }

        return query(query);
    }

    public LocalDateTime getLatestReceivedMessageTime() {
        PreparedQuery<ChatMessage> query;
        try {
            query = queryBuilder().orderBy("send_time", false).prepare();
        } catch (SQLException ex) {
            Log.e(TAG, "Failed to create PreparedQuery for chat message: ", ex);
            throw new RuntimeException("Failed to create PreparedQuery for chat message: ", ex);
        }
        ChatMessage messageInDB = queryForFirst(query);
        return messageInDB == null ? null : messageInDB.getSendTime();
    }
}
