package com.wetrack.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.wetrack.model.Chat;
import com.wetrack.model.User;

@DatabaseTable(tableName = "user_chat")
public class UserChat {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true, columnName = "username")
    private User owner;

    @DatabaseField(foreign = true, columnName = "chat_id")
    private Chat chat;

    public UserChat() {}

    public UserChat(User owner, Chat chat) {
        this.owner = owner;
        this.chat = chat;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public User getOwner() { return owner; }

    public void setOwner(User owner) { this.owner = owner; }

    public Chat getChat() { return chat; }

    public void setChat(Chat chat) { this.chat = chat; }
}
