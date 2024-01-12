package com.japco.tablerototal.model;

public class AuthUser {

    private String userId, name, username, profile;

    public AuthUser(String userId, String name, String username, String profile) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.profile = profile;
    }

    public AuthUser() {}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
