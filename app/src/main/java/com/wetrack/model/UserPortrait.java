package com.wetrack.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.types.DateTimeType;
import com.j256.ormlite.table.DatabaseTable;

import org.joda.time.DateTime;

@DatabaseTable(tableName = "portrait")
public class UserPortrait {
    @DatabaseField(id = true)
    private String username;
    @DatabaseField(persisterClass = DateTimeType.class)
    private DateTime updateTime;

    public UserPortrait() {}

    public UserPortrait(String username, DateTime updateTime) {
        this.username = username;
        this.updateTime = updateTime;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public DateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(DateTime updateTime) { this.updateTime = updateTime; }
}
