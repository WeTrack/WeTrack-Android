package com.wetrack.model;

import com.google.gson.annotations.SerializedName;

public class FriendInvitation extends Notification {
    @SerializedName("from") private String fromUsername;
    @SerializedName("to") private String toUsername;

    public String getFromUsername() { return fromUsername; }
    public void setFromUsername(String fromUsername) { this.fromUsername = fromUsername; }
    public String getToUsername() { return toUsername; }
    public void setToUsername(String toUsername) { this.toUsername = toUsername; }
}
