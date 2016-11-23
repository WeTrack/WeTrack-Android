package com.wetrack.model;

import com.j256.ormlite.field.DatabaseField;
import com.wetrack.database.LocalDateTimePersister;

import org.joda.time.LocalDateTime;

public abstract class Notification {
    @DatabaseField(id = true)
    private String id;
    @DatabaseField
    private String content;
    @DatabaseField(columnName = "send_time", persisterClass = LocalDateTimePersister.class)
    private LocalDateTime sendTime;

    /* Getters and Setters */
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getSendTime() { return sendTime; }
    public void setSendTime(LocalDateTime sendTime) { this.sendTime = sendTime; }
}
