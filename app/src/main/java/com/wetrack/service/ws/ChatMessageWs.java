package com.wetrack.service.ws;

import com.wetrack.model.ChatMessage;

import org.joda.time.LocalDateTime;

public class ChatMessageWs extends WsResponse {
    private String id;
    private String chatId;
    private String from;
    private String content;
    private LocalDateTime sendTime;

    public ChatMessage toChatMessage() {
        ChatMessage result = new ChatMessage();
        result.setId(id);
        result.setChatId(chatId);
        result.setFromUsername(from);
        result.setContent(content);
        result.setSendTime(sendTime);
        return result;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getChatId() { return chatId; }
    public void setChatId(String chatId) { this.chatId = chatId; }
    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getSendTime() { return sendTime; }
    public void setSendTime(LocalDateTime sendTime) { this.sendTime = sendTime; }
}
