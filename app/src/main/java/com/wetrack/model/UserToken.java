package com.wetrack.model;

import org.joda.time.LocalDateTime;

public class UserToken {
    private String token;
    private String username;
    private LocalDateTime expireTime;

    public UserToken() {}

    public UserToken(String token, String username, LocalDateTime expireTime) {
        this.token = token;
        this.username = username;
        this.expireTime = expireTime;
    }

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public LocalDateTime getExpireTime() {
        return expireTime;
    }
    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }
}
