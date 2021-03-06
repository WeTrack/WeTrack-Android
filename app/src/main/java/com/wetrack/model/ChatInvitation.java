package com.wetrack.model;

import com.google.gson.annotations.SerializedName;

public class ChatInvitation extends Notification {
    private String chatId;
    @SerializedName("from") private String fromUsername;
    @SerializedName("to") private String toUsername;

    public String getChatId() { return chatId; }
    public void setChatId(String chatId) { this.chatId = chatId; }
    public String getFromUsername() { return fromUsername; }
    public void setFromUsername(String fromUsername) { this.fromUsername = fromUsername; }
    public String getToUsername() { return toUsername; }
    public void setToUsername(String toUsername) { this.toUsername = toUsername; }
}
