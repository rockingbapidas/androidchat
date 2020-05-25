package com.bapidas.chattingapp.utils

import android.util.Log
import com.bapidas.chattingapp.ChatApplication
import com.bapidas.chattingapp.data.core.SetDataHandler
import com.bapidas.chattingapp.data.core.callbacks.ResultInterface
import com.bapidas.chattingapp.data.pref.SharedPrefM
import com.google.firebase.iid.FirebaseInstanceId
import java.util.*

/**
 * Created by bapidas on 10/07/17.
 */
object UpdateKeyUtils {
    private val TAG = UpdateKeyUtils::class.java.simpleName

    //Update user status keys
    @JvmStatic
    fun updateTokenToServer(token: String?) {
        try {
            val fcmToken: String? = token ?: FirebaseInstanceId.getInstance().token
            SharedPrefM(ChatApplication.applicationContext()).saveString(Constants.FIREBASE_TOKEN, fcmToken)
            if (ChatApplication.applicationContext().userInstance != null) {
                val hashMap = HashMap<String?, Any?>()
                hashMap[Constants.FIREBASE_TOKEN] = fcmToken
                val setDataHandler = SetDataHandler()
                setDataHandler.databaseReference = ChatApplication.applicationContext().userReference
                        .child(ChatApplication.applicationContext().userInstance?.uid.orEmpty())
                setDataHandler.updateData(hashMap, object : ResultInterface {
                    override fun onSuccess(t: String) {
                        Log.d(TAG, "Firebase token updated successfully")
                    }

                    override fun onFail(e: String) {
                        Log.d(TAG, "Firebase token updated error $e")
                    }
                })
            } else {
                Log.e(TAG, "Token cannot updated because firebase user is null")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun updateLastSeen(timeStamp: Long) {
        val hashMap = HashMap<String?, Any?>()
        hashMap[Constants.LAST_SEEN] = timeStamp
        if (ChatApplication.applicationContext().userInstance != null) {
            val setDataHandler = SetDataHandler()
            setDataHandler.databaseReference = (ChatApplication.applicationContext().userReference
                    .child(ChatApplication.applicationContext().userInstance?.uid.orEmpty()))
            setDataHandler.updateData(hashMap, object : ResultInterface {
                override fun onSuccess(t: String) {
                    Log.d(TAG, "Last seen time updated successfully")
                }

                override fun onFail(e: String) {
                    Log.d(TAG, "Last seen time not updated $e")
                }
            })
        } else {
            Log.e(TAG, "Last seen cannot updated because firebase user is null")
        }
    }

    @JvmStatic
    fun updateOnlineStatus(status: Boolean) {
        val hashMap = HashMap<String?, Any?>()
        hashMap[Constants.ONLINE_STATUS] = status
        if (ChatApplication.applicationContext().userInstance != null) {
            val setDataHandler = SetDataHandler()
            setDataHandler.databaseReference = (ChatApplication.applicationContext().userReference
                    .child(ChatApplication.applicationContext().userInstance?.uid.orEmpty()))
            setDataHandler.updateData(hashMap, object : ResultInterface {
                override fun onSuccess(t: String) {
                    Log.d(TAG, "Online status updated successfully")
                }

                override fun onFail(e: String) {
                    Log.d(TAG, "Online status not updated  $e")
                }
            })
        } else {
            Log.e(TAG, "Online status cannot updated because firebase user is null")
        }
    }

    //Update Chat status keys
    @JvmStatic
    fun updateSentStatus(room: String, timeStamp: Long) {
        val hashMap = HashMap<String?, Any?>()
        hashMap[Constants.SENT_STATUS] = true
        val setDataHandler = SetDataHandler()
        setDataHandler.databaseReference = (ChatApplication.applicationContext().chatReference
                .child(room).child(timeStamp.toString()))
        setDataHandler.updateData(hashMap, object : ResultInterface {
            override fun onSuccess(t: String) {
                Log.d(TAG, "Sent status updated successfully")
            }

            override fun onFail(e: String) {
                Log.d(TAG, "Sent status not updated  $e")
            }
        })
    }

    @JvmStatic
    fun updateReadStatus(room: String, timeStamp: Long) {
        val hashMap = HashMap<String?, Any?>()
        hashMap[Constants.READ_STATUS] = true
        val setDataHandler = SetDataHandler()
        setDataHandler.databaseReference = (ChatApplication.applicationContext().chatReference
                .child(room).child(timeStamp.toString()))
        setDataHandler.updateData(hashMap, object : ResultInterface {
            override fun onSuccess(t: String) {
                Log.d(TAG, "Read status updated successfully")
            }

            override fun onFail(e: String) {
                Log.d(TAG, "Read status not updated  $e")
            }
        })
    }

    @JvmStatic
    fun updateFileUrl(room: String, timeStamp: Long, url: String) {
        val hashMap = HashMap<String?, Any?>()
        hashMap[Constants.FILE_URL] = url
        val setDataHandler = SetDataHandler()
        setDataHandler.databaseReference = (ChatApplication.applicationContext().chatReference
                .child(room).child(timeStamp.toString()))
        setDataHandler.updateData(hashMap, object : ResultInterface {
            override fun onSuccess(t: String) {
                Log.d(TAG, "File url updated successfully")
            }

            override fun onFail(e: String) {
                Log.d(TAG, "File url not updated$e")
            }
        })
    }
}