package com.wetrack.model;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "messages")
public class ChatMessage extends Notification {
    @DatabaseField(columnName = "chat_id", index = true)
    private String chatId;
    @DatabaseField(columnName = "from")
    @SerializedName("from") private String fromUsername;
    @DatabaseField
    private transient boolean acked = true;

    public ChatMessage() {}

    public ChatMessage(String chatId, String fromUsername) {
        this.chatId = chatId;
        this.fromUsername = fromUsername;
        this.acked = false;
    }

    public String getChatId() { return chatId; }
    public void setChatId(String chatId) { this.chatId = chatId; }
    public String getFromUsername() { return fromUsername; }
    public void setFromUsername(String fromUsername) { this.fromUsername = fromUsername; }
    public boolean isAcked() { return acked; }
    public void setAcked(boolean acked) { this.acked = acked; }
}
