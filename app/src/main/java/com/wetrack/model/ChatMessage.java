package com.wetrack.model;

import com.google.gson.annotations.SerializedName;

public class ChatMessage extends Notification {
    private String chatId;
    @SerializedName("from") private String fromUsername;

    public String getChatId() { return chatId; }
    public void setChatId(String chatId) { this.chatId = chatId; }
    public String getFromUsername() { return fromUsername; }
    public void setFromUsername(String fromUsername) { this.fromUsername = fromUsername; }
}
