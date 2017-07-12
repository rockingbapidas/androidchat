package com.vantagecircle.chatapp.model;

import java.io.Serializable;

/**
 * Created by bapidas on 10/07/17.
 */

public class UserM implements Serializable {
    private String userId;
    private String username;
    private String fullName;
    private String fcmToken;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
