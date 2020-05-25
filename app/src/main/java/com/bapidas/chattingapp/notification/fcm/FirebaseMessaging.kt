package com.bapidas.chattingapp.notification.fcm

import android.util.Log
import com.bapidas.chattingapp.ChatApplication
import com.bapidas.chattingapp.notification.NotificationHandler
import com.bapidas.chattingapp.utils.ToolsUtils.isAppInBackground
import com.bapidas.chattingapp.utils.UpdateKeyUtils.updateTokenToServer
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONException
import org.json.JSONObject

class FirebaseMessaging : FirebaseMessagingService() {
    override fun onNewToken(s: String) {
        super.onNewToken(s)
        Log.d(TAG, "onTokenRefresh: $s")
        updateTokenToServer(s)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "onMessageReceived: " + remoteMessage.data)
        if (remoteMessage.data.containsKey("chatSource") &&
                remoteMessage.data["userId"] != ChatApplication.applicationContext().id) {
            if (isAppInBackground(this)) {
                try {
                     NotificationHandler(this)
                        .setNotification(JSONObject(remoteMessage.data as Map<*, *>))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                Log.d(TAG, "New Message arrived ====== ")
            }
        } else {
            Log.d(TAG, "This message is send by this user ======")
        }
    }

    companion object {
        private val TAG = FirebaseMessaging::class.java.simpleName
    }
}