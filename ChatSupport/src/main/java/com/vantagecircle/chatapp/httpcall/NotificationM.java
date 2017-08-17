package com.vantagecircle.chatapp.httpcall;

import java.util.ArrayList;

/**
 * Created by bapidas on 12/07/17.
 */

public class NotificationM {
    private String title;
    private String chatType;
    private String messageText;
    private String fileUrl;
    private String senderUsername;
    private String senderUid;
    private String senderFcmToken;
    private String receiverUserName;
    private String receiverUid;
    private String receiverFcmToken;
    private String chatRoom;
    private long timeStamp;
    private String conversationType;
    private ArrayList<String> tokenList;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getChatType() {
        return chatType;
    }

    public void setChatType(String chatType) {
        this.chatType = chatType;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
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

    public String getReceiverUserName() {
        return receiverUserName;
    }

    public void setReceiverUserName(String receiverUserName) {
        this.receiverUserName = receiverUserName;
    }

    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
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

    public String getConversationType() {
        return conversationType;
    }

    public void setConversationType(String conversationType) {
        this.conversationType = conversationType;
    }

    public ArrayList<String> getTokenList() {
        return tokenList;
    }

    public void setTokenList(ArrayList<String> tokenList) {
        this.tokenList = tokenList;
    }
}
