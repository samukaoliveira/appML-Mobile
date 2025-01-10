// LoginResponse.java
package com.example.appml.models;

public class LoginResponse {
    private String token;
    private int userId;

    // Getters e setters

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
