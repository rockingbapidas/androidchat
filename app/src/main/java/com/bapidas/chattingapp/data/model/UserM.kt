package com.bapidas.chattingapp.data.model

import androidx.annotation.Keep
import com.bapidas.chattingapp.utils.DateUtils.getTime
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

/**
 * Created by bapidas on 11/07/17.
 */
@IgnoreExtraProperties
@Keep
data class UserM(
        @SerializedName("userId")
        var userId: String = "",
        @SerializedName("username")
        var username: String = "",
        @SerializedName("fullName")
        var fullName: String = "",
        @SerializedName("fcmToken")
        var fcmToken: String = "",
        @SerializedName("notificationCount")
        var notificationCount: Int = 0,
        @SerializedName("isOnline")
        var isOnline: Boolean = false,
        @SerializedName("lastSeenTime")
        var lastSeenTime: Long = 0,
        @SerializedName("userType")
        var userType: String = "",
        @SerializedName("roomMArrayList")
        var roomMArrayList: List<RoomM> = emptyList()
): Serializable {

    @Exclude
    fun isOnlineString(): String = if (isOnline) {
        "Online"
    } else {
        "Last seen on " + getTime(lastSeenTime)
    }
}