package com.bapidas.chattingapp.notification.httpcall

import android.content.Context
import android.util.Log
import com.bapidas.chattingapp.R
import com.bapidas.chattingapp.data.model.Chat
import com.bapidas.chattingapp.utils.Constants
import com.bapidas.chattingapp.utils.UpdateKeyUtils.updateSentStatus
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

/**
 * Created by bapidas on 12/07/17.
 */
class SendNotification(context: Context) {
    //Notification model
    private val notification: Notification = Notification()
    private val mContext: Context = context

    fun subscribeTokenToTopic(token: String, groupName: String) {
        try {
            val url = TOPIC_SUBSCRIBE_URL1 + token + TOPIC_SUBSCRIBE_URL2 + groupName
            Log.e(TAG, "subscribeTokenToTopic ==== $url")
            val request = Request.Builder()
                    .addHeader(CONTENT_TYPE, APPLICATION_JSON)
                    .addHeader(AUTHORIZATION, mContext.resources.getString(R.string.server_key))
                    .url(url)
                    .build()
            OkHttpClient().newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e(TAG, "onFailure: " + e.message)
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    Log.e(TAG, "onResponse body: " + response.body)
                    Log.e(TAG, "onResponse code: " + response.code)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun prepareNotification(chat: Chat) {
        try {
            val chatType = chat.chatType
            val messageText = chat.messageText
            val fileUrl = chat.fileUrl
            val timeStamp = chat.timeStamp
            notification.title = chat.senderName
            notification.chatType = chatType
            notification.messageText = messageText
            notification.fileUrl = fileUrl
            notification.timeStamp = timeStamp
            notification.chatRoom = chat.chatRoom
            notification.conversationType = chat.convType
            notification.senderUsername = chat.senderName
            notification.receiverUserName = chat.receiverName
            notification.senderUid = chat.senderUid
            notification.receiverUid = chat.receiverUid
            notification.senderFcmToken = chat.senderToken
            notification.receiverFcmToken = chat.receiverToken
            sendNotificationToUser()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun sendNotificationToUser() {
        try {
            Log.e(TAG, "sendNotificationToUser ==== ")
            val requestBody = dataObject.toString().toRequestBody(MEDIA_TYPE_JSON)
            val request = Request.Builder()
                    .addHeader(CONTENT_TYPE, APPLICATION_JSON)
                    .addHeader(ACCEPT, MIME_TYPE)
                    .addHeader(AUTHORIZATION, mContext.resources.getString(R.string.server_key))
                    .url(mContext.resources.getString(R.string.fcm_url))
                    .post(requestBody)
                    .build()
            OkHttpClient().newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e(TAG, "onFailure: " + e.message)
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    Log.e(TAG, "onResponse body: " + response.body)
                    Log.e(TAG, "onResponse code: " + response.code)
                    if (response.code == 200) {
                        notification.chatRoom?.let {
                            updateSentStatus(it, notification.timeStamp)
                        }
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //parent json body setup
    @get:Throws(JSONException::class)
    private val dataObject: JSONObject
        get() {
            //parent json body setup
            val parentBody = JSONObject()
            if (notification.conversationType == Constants.CONVERSATION_SINGLE) {
                parentBody.put(KEY_TO, notification.receiverFcmToken)
            } else {
                parentBody.put(KEY_TO, "/topics/" + notification.chatRoom)
            }
            parentBody.put(KEY_DATA, childData)
            Log.e(TAG, "data === $parentBody")
            return parentBody
        }

    //child json body setup
    @get:Throws(JSONException::class)
    private val childData: JSONObject
        get() {
            //child json body setup
            val childData = JSONObject()
            childData.put(KEY_TITLE, notification.title)
            childData.put(KEY_TYPE, notification.chatType)
            childData.put(KEY_TEXT, notification.messageText)
            childData.put(KEY_URI, notification.fileUrl)
            childData.put(KEY_CON_TYPE, notification.conversationType)

            //childData.put(KEY_USERNAME, notificationM.getSenderUsername());
            childData.put(KEY_UID, notification.senderUid)
            //childData.put(KEY_FCM_TOKEN, notificationM.getSenderFcmToken());
            childData.put(KEY_CHAT_FLAG, true)
            if (notification.conversationType == Constants.CONVERSATION_GROUP) {
                childData.put(KEY_CONTEST_ID, notification.receiverUid)
                childData.put(KEY_CONTEST_NAME, notification.receiverUserName)
                childData.put(KEY_ROOM, notification.chatRoom)
            }
            return childData
        }

    companion object {
        private val TAG = SendNotification::class.java.simpleName

        //Http header configuration
        private val MEDIA_TYPE_JSON: MediaType? = "application/json; charset=utf-8".toMediaTypeOrNull()
        private const val CONTENT_TYPE = "Content-Type"
        private const val ACCEPT = "Accept"
        private const val MIME_TYPE = "*/*"
        private const val APPLICATION_JSON = "application/json"
        private const val AUTHORIZATION = "Authorization"

        //topic subscribe url address
        private const val TOPIC_SUBSCRIBE_URL1 = "https://iid.googleapis.com/iid/v1/"
        private const val TOPIC_SUBSCRIBE_URL2 = "/rel/topics/"

        //Json keys from fcm data
        private const val KEY_TO = "to"
        private const val KEY_DATA = "data"
        private const val KEY_TITLE = "title"
        private const val KEY_TYPE = "type"
        private const val KEY_TEXT = "text"
        private const val KEY_URI = "fileUri"
        private const val KEY_CON_TYPE = "conType"
        private const val KEY_USERNAME = "userName"
        private const val KEY_UID = "userID"
        private const val KEY_FCM_TOKEN = "userToken"
        private const val KEY_CHAT_FLAG = "chatSource"
        private const val KEY_CONTEST_ID = "contestId"
        private const val KEY_CONTEST_NAME = "contestName"
        private const val KEY_ROOM = "contestRoom"
    }
}