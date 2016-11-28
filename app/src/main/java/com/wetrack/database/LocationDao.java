package com.wetrack.database;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.wetrack.model.Location;
import com.wetrack.utils.Tags;

import org.joda.time.LocalDateTime;

import java.sql.SQLException;
import java.util.List;

public class LocationDao extends RuntimeExceptionDao<Location, Integer> {
    private static final String TAG = Tags.Dao.LOCATION;

    public LocationDao(Dao<Location, ?> dao) {
        super((Dao<Location, Integer>) dao);
    }

    public List<Location> getUserLocationsSince(String username, LocalDateTime sinceTime) {
        PreparedQuery<Location> query;
        try {
            query = queryBuilder().orderBy("time", true).where().eq("username", username)
                    .and().ge("time", sinceTime).prepare();
        } catch (SQLException ex) {
            Log.e(TAG, "Failed to create PreparedQuery for locations of user: ", ex);
            throw new RuntimeException("Failed to create PreparedQuery for locations of user: ", ex);
        }
        return query(query);
    }

    public Location getUserLatestLocation(String username) {
        PreparedQuery<Location> query;
        try {
            query = queryBuilder().orderBy("time", false).where().eq("username", username).prepare();
        } catch (SQLException ex) {
            Log.e(TAG, "Failed to create PreparedQuery for latest location of user: ", ex);
            throw new RuntimeException("Failed to create PreparedQuery for latest location of user: ", ex);
        }
        return queryForFirst(query);
    }
}
