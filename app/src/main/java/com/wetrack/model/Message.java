package com.wetrack.model;

import com.google.gson.annotations.SerializedName;

public class Message {
    @SerializedName("status_code")
    private int statusCode;
    private String message;

    public Message() {}

    public Message(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
