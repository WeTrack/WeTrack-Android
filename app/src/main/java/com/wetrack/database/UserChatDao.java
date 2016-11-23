package com.wetrack.database;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.wetrack.model.Chat;
import com.wetrack.model.User;

import java.sql.SQLException;
import java.util.Collection;

public class UserChatDao extends RuntimeExceptionDao<UserChat, Integer> {
    private static final String TAG = UserChatDao.class.getCanonicalName();

    private final RuntimeExceptionDao<Chat, String> chatDao;

    private PreparedQuery<Chat> chatsOfUserQuery;
    private PreparedQuery<UserChat> userChatQuery;

    public static UserChatDao of(RuntimeExceptionDao<Chat, String> chatDao,
                                 RuntimeExceptionDao<UserChat, ?> userChatDao) {
        return new UserChatDao(chatDao, (RuntimeExceptionDao<UserChat, Integer>) userChatDao);
    }

    private UserChatDao(RuntimeExceptionDao<Chat, String> chatDao,
                        RuntimeExceptionDao<UserChat, Integer> userChatDao) {
        super(userChatDao);
        this.chatDao = chatDao;
    }

    public Collection<Chat> getUserChatList(User user) {
        try {
            if (chatsOfUserQuery == null)
                chatsOfUserQuery = initChatsOfUserQuery();
            chatsOfUserQuery.setArgumentHolderValue(0, user);
        } catch (SQLException ex) {
            Log.e(TAG, "Failed to create PreparedQuery for chats of user: ", ex);
            throw new RuntimeException("Failed to create PreparedQuery for chats of user: ", ex);
        }
        return chatDao.query(chatsOfUserQuery);
    }

    public boolean userChatExists(UserChat userChat) {
        try {
            if (userChatQuery == null)
                userChatQuery = initUserChatQuery();
            userChatQuery.setArgumentHolderValue(0, userChat.getOwner());
            userChatQuery.setArgumentHolderValue(1, userChat.getChat());
        } catch (SQLException ex) {
            Log.e(TAG, "Failed to create PreparedQuery for user chat: ", ex);
            throw new RuntimeException("Failed to create PreparedQuery for user chat: ", ex);
        }
        return countOf(userChatQuery) > 0;
    }

    private PreparedQuery<UserChat> initUserChatQuery() throws SQLException {
        QueryBuilder<UserChat, Integer> userChatQb = queryBuilder();
        SelectArg userSelectArg = new SelectArg();
        SelectArg chatSelectArg = new SelectArg();
        userChatQb.where().eq("username", userSelectArg).and().eq("chat_id", chatSelectArg);
        return userChatQb.prepare();
    }

    private PreparedQuery<Chat> initChatsOfUserQuery() throws SQLException {
        QueryBuilder<UserChat, Integer> userChatQb = queryBuilder();
        userChatQb.selectColumns("chat_id");
        SelectArg userSelectArg = new SelectArg();
        userChatQb.where().eq("username", userSelectArg);

        QueryBuilder<Chat, String> chatQb = chatDao.queryBuilder();
        chatQb.where().in("id", userChatQb);
        return chatQb.prepare();
    }
}
