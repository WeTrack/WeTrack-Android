package com.wetrack.model;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.wetrack.database.StringListPersister;

import java.util.LinkedList;
import java.util.List;

@DatabaseTable(tableName = "chats")
public class Chat {
    @DatabaseField(columnName = "id", id = true)
    private String chatId;
    @DatabaseField
    private String name;
    @DatabaseField(columnName = "members", persisterClass = StringListPersister.class)
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

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;

        Chat chat = (Chat) that;

        return chatId != null && chatId.equals(chat.chatId);

    }
}
