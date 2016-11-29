package com.wetrack.service.ws;

import com.google.gson.annotations.SerializedName;

import org.joda.time.LocalDateTime;

public class ChatMessageAck extends WsResponse {
    @SerializedName("id") private String messageId;
    private LocalDateTime actualSendTime;

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }
    public LocalDateTime getActualSendTime() { return actualSendTime; }
    public void setActualSendTime(LocalDateTime actualSendTime) { this.actualSendTime = actualSendTime; }
}
