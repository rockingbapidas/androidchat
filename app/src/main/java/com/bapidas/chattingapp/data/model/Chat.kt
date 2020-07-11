package com.bapidas.chattingapp.data.model

import androidx.annotation.Keep
import com.google.firebase.database.IgnoreExtraProperties
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by bapidas on 10/07/17.
 */
@IgnoreExtraProperties
@Keep
data class Chat(
        @SerializedName("senderName")
        var senderName: String = "",
        @SerializedName("receiverName")
        var receiverName: String = "",
        @SerializedName("senderUid")
        var senderUid: String = "",
        @SerializedName("receiverUid")
        var receiverUid: String = "",
        @SerializedName("chatType")
        var chatType: String = "",
        @SerializedName("messageText")
        var messageText: String = "",
        @SerializedName("fileUrl")
        var fileUrl: String = "",
        @SerializedName("timeStamp")
        var timeStamp: Long = 0,
        @SerializedName("isSentSuccessfully")
        var isSentSuccessfully: Boolean = false,
        @SerializedName("isReadSuccessfully")
        var isReadSuccessfully: Boolean = false,
        @SerializedName("chatRoom")
        var chatRoom: String = "",
        @SerializedName("convType")
        var convType: String = "",
        @SerializedName("senderToken")
        var senderToken: String = "",
        @SerializedName("receiverToken")
        var receiverToken: String = ""
) : Serializable