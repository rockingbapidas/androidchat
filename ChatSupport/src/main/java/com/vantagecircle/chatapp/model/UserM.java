package com.vantagecircle.chatapp.model;

import java.io.Serializable;

/**
 * Created by bapidas on 11/07/17.
 */

public class UserM implements Serializable{
    private String userId;
    private String username;
    private String fullName;
    private String fcmToken;
    private int notificationCount;
    private boolean isOnline;
    private long lastSeenTime;
    private String userType;

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

    public int getNotificationCount() {
        return notificationCount;
    }

    public void setNotificationCount(int notificationCount) {
        this.notificationCount = notificationCount;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public long getLastSeenTime() {
        return lastSeenTime;
    }

    public void setLastSeenTime(long lastSeenTime) {
        this.lastSeenTime = lastSeenTime;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @Override
    public String toString() {
        return "UserM{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", fcmToken='" + fcmToken + '\'' +
                ", notificationCount=" + notificationCount +
                ", isOnline=" + isOnline +
                ", lastSeenTime=" + lastSeenTime +
                ", userType='" + userType + '\'' +
                '}';
    }
}
