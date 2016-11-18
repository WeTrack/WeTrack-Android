package com.wetrack.model;

import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;
import java.util.List;

public class Chat {
    private String chatId;
    private String name;
    @SerializedName("members") private List<String> memberNames;

    public Chat() {}

    public Chat(String name) {
        this.chatId = "";
        this.name = name;
        this.memberNames = new LinkedList<>();
    }

    public String getChatId() {
        return chatId;
    }
    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<String> getMemberNames() {
        return memberNames;
    }
    public void setMemberNames(List<String> memberNames) {
        this.memberNames = memberNames;
    }
}
