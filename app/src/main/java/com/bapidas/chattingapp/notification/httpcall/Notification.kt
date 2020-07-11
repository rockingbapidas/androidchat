package com.bapidas.chattingapp.notification.httpcall

import java.util.*

/**
 * Created by bapidas on 12/07/17.
 */
data class Notification(
        var title: String? = null,
        var chatType: String? = null,
        var messageText: String? = null,
        var fileUrl: String? = null,
        var senderUsername: String? = null,
        var senderUid: String? = null,
        var senderFcmToken: String? = null,
        var receiverUserName: String? = null,
        var receiverUid: String? = null,
        var receiverFcmToken: String? = null,
        var chatRoom: String? = null,
        var timeStamp: Long = 0,
        var conversationType: String? = null,
        var tokenList: ArrayList<String>? = null
)