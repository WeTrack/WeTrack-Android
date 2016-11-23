package com.wetrack.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.wetrack.model.User;

@DatabaseTable(tableName = "friends")
public class Friend {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true, columnName = "owner", index = true)
    private User owner;

    @DatabaseField(foreign = true, columnName = "friend")
    private User friend;

    public Friend() {}

    public Friend(User owner, User friend) {
        this.owner = owner;
        this.friend = friend;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }
    public User getFriend() { return friend; }
    public void setFriend(User friend) { this.friend = friend; }
}
