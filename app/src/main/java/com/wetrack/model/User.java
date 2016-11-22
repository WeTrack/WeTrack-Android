package com.wetrack.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.wetrack.database.LocalDateTimePersister;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.joda.time.LocalDate;

@DatabaseTable(tableName = "users")
public class User {
    @DatabaseField(id = true)
    private String username;
    private String password;
    @DatabaseField
    private String nickname;
    @DatabaseField(columnName = "icon_url")
    private String iconUrl;
    @DatabaseField
    private String email;
    @DatabaseField
    private Gender gender;
    @DatabaseField(columnName = "birth_date", persisterClass = LocalDateTimePersister.class)
    private LocalDate birthDate;

    public User() {}

    public User(String username, String password, String nickname) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public String getIconUrl() {
        return iconUrl;
    }
    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public Gender getGender() {
        return gender;
    }
    public void setGender(Gender gender) {
        this.gender = gender;
    }
    public LocalDate getBirthDate() {
        return birthDate;
    }
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public enum Gender {
        Male, Female
    }

    @Override
    public boolean equals(Object that) {
        return EqualsBuilder.reflectionEquals(this, that, password);
    }
}
