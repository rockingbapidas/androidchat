package com.vantagecircle.chatapp.model;

/**
 * Created by bapidas on 12/07/17.
 */

public class NotificationM {
    private String title;
    private String messageText;
    private String senderUsername;
    private String senderUid;
    private String senderFcmToken;
    private String receiverFcmToken;
    private String chatRoom;
    private long timeStamp;

    public NotificationM(String title, String messageText, String senderUsername, String senderUid,
                         String senderFcmToken, String receiverFcmToken, String chatRoom, long timeStamp) {
        this.title = title;
        this.messageText = messageText;
        this.senderUsername = senderUsername;
        this.senderUid = senderUid;
        this.senderFcmToken = senderFcmToken;
        this.receiverFcmToken = receiverFcmToken;
        this.chatRoom = chatRoom;
        this.timeStamp = timeStamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getSenderFcmToken() {
        return senderFcmToken;
    }

    public void setSenderFcmToken(String senderFcmToken) {
        this.senderFcmToken = senderFcmToken;
    }

    public String getReceiverFcmToken() {
        return receiverFcmToken;
    }

    public void setReceiverFcmToken(String receiverFcmToken) {
        this.receiverFcmToken = receiverFcmToken;
    }

    public String getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(String chatRoom) {
        this.chatRoom = chatRoom;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
