package com.bapidas.chattingapp.domain

import java.io.Serializable

/**
 * Created by bapidas on 11/07/17.
 */
data class User(
        var userId: String = "",
        var username: String = "",
        var fullName: String = "",
        var fcmToken: String = "",
        var notificationCount: Int = 0,
        var isOnline: Boolean = false,
        var lastSeenTime: Long = 0,
        var userType: String = "",
        var conversationArrayList: List<Conversation> = emptyList()
): Serializable