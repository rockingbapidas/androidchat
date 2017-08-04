package com.vantagecircle.chatapp.model;

import java.io.Serializable;

/**
 * Created by bapidas on 10/07/17.
 */

public class ChatM implements Serializable {
    private String senderName;
    private String receiverName;
    private String senderUid;
    private String receiverUid;
    private String chatType;
    private String messageText;
    private String fileUrl;
    private long timeStamp;
    private boolean isSentSuccessfully;
    private boolean isReadSuccessfully;
    private String chatRoom;
    private String convType;
    private String senderToken;
    private String receiverToken;

    public ChatM() {
    }

    public ChatM(String senderName, String receiverName, String senderUid, String receiverUid,
                 String chatType, String messageText, String fileUrl, long timeStamp,
                 boolean isSentSuccessfully, boolean isReadSuccessfully, String chatRoom,
                 String convType, String senderToken, String receiverToken) {
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.chatType = chatType;
        this.messageText = messageText;
        this.fileUrl = fileUrl;
        this.timeStamp = timeStamp;
        this.isSentSuccessfully = isSentSuccessfully;
        this.isReadSuccessfully = isReadSuccessfully;
        this.chatRoom = chatRoom;
        this.convType = convType;
        this.senderToken = senderToken;
        this.receiverToken = receiverToken;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
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

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isSentSuccessfully() {
        return isSentSuccessfully;
    }

    public void setSentSuccessfully(boolean sentSuccessfully) {
        isSentSuccessfully = sentSuccessfully;
    }

    public boolean isReadSuccessfully() {
        return isReadSuccessfully;
    }

    public void setReadSuccessfully(boolean read) {
        isReadSuccessfully = read;
    }

    public String getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(String chatRoom) {
        this.chatRoom = chatRoom;
    }

    public String getConvType() {
        return convType;
    }

    public void setConvType(String convType) {
        this.convType = convType;
    }

    public String getSenderToken() {
        return senderToken;
    }

    public void setSenderToken(String senderToken) {
        this.senderToken = senderToken;
    }

    public String getReceiverToken() {
        return receiverToken;
    }

    public void setReceiverToken(String receiverToken) {
        this.receiverToken = receiverToken;
    }

    @Override
    public String toString() {
        return "ChatM{" +
                "senderName='" + senderName + '\'' +
                ", receiverName='" + receiverName + '\'' +
                ", senderUid='" + senderUid + '\'' +
                ", receiverUid='" + receiverUid + '\'' +
                ", chatType='" + chatType + '\'' +
                ", messageText='" + messageText + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", timeStamp=" + timeStamp +
                ", isSentSuccessfully=" + isSentSuccessfully +
                ", isReadSuccessfully=" + isReadSuccessfully +
                ", chatRoom='" + chatRoom + '\'' +
                ", convType='" + convType + '\'' +
                ", senderToken='" + senderToken + '\'' +
                ", receiverToken='" + receiverToken + '\'' +
                '}';
    }
}
