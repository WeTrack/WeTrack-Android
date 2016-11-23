package com.wetrack.database;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.wetrack.model.User;

import java.sql.SQLException;
import java.util.Collection;

public class FriendDao extends RuntimeExceptionDao<Friend, Integer> {
    private static final String TAG = FriendDao.class.getCanonicalName();

    private final RuntimeExceptionDao<User, String> userDao;

    public FriendDao(RuntimeExceptionDao<User, String> userDao,
                     Dao<Friend, ?> friendDao) {
        super((Dao<Friend, Integer>) friendDao);
        this.userDao = userDao;
    }

    public Collection<User> getUserFriendList(User user) {
        PreparedQuery<User> friendsOfUserQuery;
        try {
            friendsOfUserQuery = initFriendsOfUserQuery();
            friendsOfUserQuery.setArgumentHolderValue(0, user);
        } catch (SQLException ex) {
            Log.e(TAG, "Failed to create PreparedQuery for friends of user: ", ex);
            throw new RuntimeException("Failed to create PreparedQuery for friends of user: ", ex);
        }
        return userDao.query(friendsOfUserQuery);
    }

    private PreparedQuery<User> initFriendsOfUserQuery() throws SQLException {
        QueryBuilder<Friend, Integer> friendQb = queryBuilder();
        friendQb.selectColumns("friend");
        SelectArg userSelectArg = new SelectArg();
        friendQb.where().eq("owner", userSelectArg);

        QueryBuilder<User, String> userQb = userDao.queryBuilder();
        userQb.where().in("username", friendQb);
        return userQb.prepare();
    }
}
