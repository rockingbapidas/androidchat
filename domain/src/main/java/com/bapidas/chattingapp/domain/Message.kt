package com.bapidas.chattingapp.domain

import java.io.Serializable

/**
 * Created by bapidas on 10/07/17.
 */
data class Message(
        var senderName: String = "",
        var receiverName: String = "",
        var senderUid: String = "",
        var receiverUid: String = "",
        var chatType: String = "",
        var messageText: String = "",
        var fileUrl: String = "",
        var timeStamp: Long = 0,
        var isSentSuccessfully: Boolean = false,
        var isReadSuccessfully: Boolean = false,
        var chatRoom: String = "",
        var conversationType: String = "",
        var senderToken: String = "",
        var receiverToken: String = ""
): Serializable