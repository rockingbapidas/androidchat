package com.vantagecircle.chatapp.core;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.vantagecircle.chatapp.R;
import com.vantagecircle.chatapp.services.SupportService;
import com.vantagecircle.chatapp.activity.ChatActivity;
import com.vantagecircle.chatapp.model.GroupM;
import com.vantagecircle.chatapp.model.UserM;
import com.vantagecircle.chatapp.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by bapidas on 12/07/17.
 */

public class NotificationHandler {
    private final String TAG = NotificationHandler.class.getSimpleName();
    private NotificationCompat.Builder mBuilder;
    private Context mContext;

    public NotificationHandler(Context mContext) {
        this.mContext = mContext;
        mBuilder = new NotificationCompat.Builder(mContext);
    }

    public void setNotification(RemoteMessage remoteMessage) throws JSONException {
        JSONObject jsonObject = new JSONObject(remoteMessage.getData());

        Intent intent = new Intent(mContext, ChatActivity.class);
        intent.putExtra("isNotification", true);
        intent.putExtra("contest_id", jsonObject.getString("contestId"));
        intent.putExtra("contest_name", jsonObject.getString("contestName"));
        intent.putExtra("contest_room", jsonObject.getString("contestRoom"));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        stackBuilder.addParentStack(ChatActivity.class);
        stackBuilder.addNextIntent(intent);

        PendingIntent contentIntent = PendingIntent.getActivity(mContext, (int)
                System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setContentTitle(jsonObject.getString("title"));

        if(jsonObject.getString("type").equals(Constants.TEXT_CONTENT)){
            mBuilder.setContentText(jsonObject.getString("text"));
        } else {
            String imageUrl = jsonObject.getString("fileUri");
            Bitmap bitmap = null;
            try {
                if (imageUrl != null && !imageUrl.isEmpty())
                    bitmap = Picasso.with(mContext).load(imageUrl).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap));
        }

        showNotification();
    }

    public void setNotification(JSONObject jsonObject) throws JSONException {
        Intent intent = new Intent(mContext, ChatActivity.class);
        intent.putExtra("isFromBar", true);
        if(jsonObject.getString("conType").equals(Constants.CONV_SN)){
            UserM userM = new UserM();
            userM.setFullName(jsonObject.getString("userName"));
            userM.setUserId(jsonObject.getString("userID"));
            userM.setFcmToken(jsonObject.getString("userToken"));
            intent.putExtra("isGroup", false);
            intent.putExtra("data", new Gson().toJson(userM));
        } else {
            GroupM groupM = new GroupM();
            groupM.setName(jsonObject.getString("userName"));
            groupM.setId(jsonObject.getString("userID"));
            intent.putExtra("isGroup", true);
            intent.putExtra("data", new Gson().toJson(groupM));
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(SupportService.getInstance());
        stackBuilder.addParentStack(ChatActivity.class);
        stackBuilder.addNextIntent(intent);

        PendingIntent contentIntent = PendingIntent.getActivity(SupportService.getInstance(), (int)
                System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setContentTitle(jsonObject.getString("title"));

        if(jsonObject.getString("type").equals(Constants.TEXT_CONTENT)){
            mBuilder.setContentText(jsonObject.getString("text"));
        } else {
            String imageUrl = jsonObject.getString("fileUri");
            Bitmap bitmap = null;
            try {
                if (imageUrl != null && !imageUrl.isEmpty())
                    bitmap = Picasso.with(SupportService.getInstance()).load(imageUrl).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap));
        }

        showNotification();
    }

    private void showNotification() {
        Bitmap largeIcon = BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.ic_chat_black_24dp);
        long[] vibrate = new long[]{1000, 1000, 1000, 1000, 1000};
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        mBuilder.setSmallIcon(R.drawable.ic_chat_black_24dp);
        mBuilder.setLargeIcon(largeIcon);
        mBuilder.setGroup("group_key_emails");
        mBuilder.setWhen(System.currentTimeMillis());
        mBuilder.setAutoCancel(true);
        mBuilder.setOnlyAlertOnce(true);
        mBuilder.setSound(sound);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setCategory(Notification.CATEGORY_MESSAGE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mBuilder.setPriority(Notification.PRIORITY_HIGH);
        }
        mBuilder.setTicker("New Message");
        mBuilder.setVibrate(vibrate);
        Notification notify = mBuilder.build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat
                .from(SupportService.getInstance());
        int MESSAGE_NOTIFICATION_ID = 1;
        notificationManager.notify(MESSAGE_NOTIFICATION_ID, notify);
        PowerManager pm = (PowerManager) SupportService.getInstance().getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP, TAG);
        wl.acquire(15000);
    }
}
