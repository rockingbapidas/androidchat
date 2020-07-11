package com.bapidas.chattingapp.data.model

import androidx.annotation.Keep
import com.google.firebase.database.IgnoreExtraProperties
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by bapidas on 20/07/17.
 */
@IgnoreExtraProperties
@Keep
data class Group(
        @SerializedName("id")
        var id: String = "",
        @SerializedName("name")
        var name: String = ""
) : Serializable