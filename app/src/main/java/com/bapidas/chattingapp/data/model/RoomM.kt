package com.bapidas.chattingapp.data.model

import androidx.annotation.Keep
import com.google.firebase.database.IgnoreExtraProperties
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by bapidas on 07/08/17.
 */
@IgnoreExtraProperties
@Keep
data class RoomM(
        @SerializedName("roomId")
        var roomId: String = "",
        @SerializedName("roomName")
        var roomName: String = ""
) : Serializable