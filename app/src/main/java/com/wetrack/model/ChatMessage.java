package com.wetrack.model;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "messages")
public class ChatMessage extends Notification {
    @DatabaseField(columnName = "chat_id", index = true)
    private String chatId;
    @DatabaseField(columnName = "from")
    @SerializedName("from") private String fromUsername;

    public String getChatId() { return chatId; }
    public void setChatId(String chatId) { this.chatId = chatId; }
    public String getFromUsername() { return fromUsername; }
    public void setFromUsername(String fromUsername) { this.fromUsername = fromUsername; }
}
