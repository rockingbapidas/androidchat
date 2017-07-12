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
    private String messageText;
    private long timeStamp;
    private boolean isSentSuccessfully;

    public ChatM() {
    }

    public ChatM(String senderName, String receiverName, String senderUid,
                 String receiverUid, String messageText, long timeStamp) {
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.messageText = messageText;
        this.timeStamp = timeStamp;
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

    public String getMessageText() {
        return messageText;
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

    @Override
    public String toString() {
        return "ChatM{" +
                "senderName='" + senderName + '\'' +
                ", receiverName='" + receiverName + '\'' +
                ", senderUid='" + senderUid + '\'' +
                ", receiverUid='" + receiverUid + '\'' +
                ", messageText='" + messageText + '\'' +
                ", timeStamp=" + timeStamp +
                '}';
    }
}
