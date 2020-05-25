package com.bapidas.chattingapp.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import com.bapidas.chattingapp.R
import com.bapidas.chattingapp.data.model.GroupM
import com.bapidas.chattingapp.data.model.UserM
import com.bapidas.chattingapp.ui.main.ChatActivity
import com.bapidas.chattingapp.utils.Constants
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

/**
 * Created by bapidas on 12/07/17.
 */
class NotificationHandler(private val mContext: Context) {
    private val TAG = NotificationHandler::class.java.simpleName
    private val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(mContext)

    @Throws(JSONException::class)
    fun setNotification(jsonObject: JSONObject) {
        val intent = Intent(mContext, ChatActivity::class.java)
        intent.putExtra("isFromBar", true)
        if (jsonObject.getString("conType") == Constants.CONVERSATION_SINGLE) {
            val userId = jsonObject.getString("userID")
            val userName = jsonObject.getString("userName")
            val fullName = jsonObject.getString("fullName")
            val fcmToken = jsonObject.getString("userToken")
            val userM = UserM(userId, userName, fullName, fcmToken)
            intent.putExtra("isGroup", false)
            intent.putExtra("data", Gson().toJson(userM))
        } else {
            val id = jsonObject.getString("userID")
            val name = jsonObject.getString("userName")
            val groupM = GroupM(id, name)
            intent.putExtra("isGroup", true)
            intent.putExtra("data", Gson().toJson(groupM))
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val stackBuilder = TaskStackBuilder.create(mContext)
        stackBuilder.addParentStack(ChatActivity::class.java)
        stackBuilder.addNextIntent(intent)
        val contentIntent = PendingIntent.getActivity(mContext,
                System.currentTimeMillis().toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)
        mBuilder.setContentIntent(contentIntent)
        mBuilder.setContentTitle(jsonObject.getString("title"))
        if (jsonObject.getString("type") == Constants.TEXT_CONTENT) {
            mBuilder.setContentText(jsonObject.getString("text"))
        } else {
            val imageUrl = jsonObject.getString("fileUri")
            var bitmap: Bitmap? = null
            try {
                if (imageUrl.isNotEmpty()) bitmap = Picasso.get().load(imageUrl).get()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            mBuilder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
        }
        showNotification()
    }

    private fun showNotification() {
        val largeIcon = BitmapFactory.decodeResource(mContext.resources,
                R.drawable.ic_chat_black_24dp)
        val vibrate = longArrayOf(1000, 1000, 1000, 1000, 1000)
        val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        mBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        mBuilder.setSmallIcon(R.drawable.ic_chat_black_24dp)
        mBuilder.setLargeIcon(largeIcon)
        mBuilder.setGroup("group_key_emails")
        mBuilder.setWhen(System.currentTimeMillis())
        mBuilder.setAutoCancel(true)
        mBuilder.setOnlyAlertOnce(true)
        mBuilder.setSound(sound)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setCategory(Notification.CATEGORY_MESSAGE)
        }
        mBuilder.priority = Notification.PRIORITY_HIGH
        mBuilder.setTicker("New Message")
        mBuilder.setVibrate(vibrate)
        val notify = mBuilder.build()
        val notificationManager = NotificationManagerCompat
                .from(mContext)
        val messageNotificationId = 1
        notificationManager.notify(messageNotificationId, notify)
        val pm = (mContext.getSystemService(Context.POWER_SERVICE) as PowerManager)
        val wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK or
                PowerManager.ACQUIRE_CAUSES_WAKEUP, TAG)
        wl.acquire(15000)
    }

}