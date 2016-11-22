package com.wetrack.database;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.wetrack.model.User;

@DatabaseTable(tableName = "friends")
public class Friend {
    @DatabaseField(id = true)
    private String username;
    @ForeignCollectionField()
    private ForeignCollection<User> friends;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public ForeignCollection<User> getFriends() { return friends; }
    public void setFriends(ForeignCollection<User> friends) { this.friends = friends; }
}
