package com.wetrack.service.ws;

import org.joda.time.LocalDateTime;

public class ChatMessageAck extends WsResponse {
    private String messageId;
    private LocalDateTime actualSendTime;

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }
    public LocalDateTime getActualSendTime() { return actualSendTime; }
    public void setActualSendTime(LocalDateTime actualSendTime) { this.actualSendTime = actualSendTime; }
}
