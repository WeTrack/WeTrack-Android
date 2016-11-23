package com.wetrack.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.wetrack.database.LocalDateTimePersister;

import org.joda.time.LocalDateTime;

@DatabaseTable(tableName = "locations")
public class Location {
    @DatabaseField(index = true)
    private String username;
    @DatabaseField
    private double longitude;
    @DatabaseField
    private double latitude;
    @DatabaseField(persisterClass = LocalDateTimePersister.class)
    private LocalDateTime time;

    public Location() {}

    public Location(String username, double longitude, double latitude, LocalDateTime time) {
        this.username = username;
        this.longitude = longitude;
        this.latitude = latitude;
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
