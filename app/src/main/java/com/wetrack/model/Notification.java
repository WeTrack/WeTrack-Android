package com.wetrack.model;

import org.joda.time.LocalDateTime;

public abstract class Notification {
    private String id;
    private String content;
    private LocalDateTime sendTime;

    /* Getters and Setters */
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getSendTime() { return sendTime; }
    public void setSendTime(LocalDateTime sendTime) { this.sendTime = sendTime; }
}
