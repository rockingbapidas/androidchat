package com.vantagecircle.chatapp.utils;

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
import android.util.Log;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.vantagecircle.chatapp.R;
import com.vantagecircle.chatapp.Support;
import com.vantagecircle.chatapp.activity.ChatActivity;
import com.vantagecircle.chatapp.model.GroupM;
import com.vantagecircle.chatapp.model.UserM;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by bapidas on 12/07/17.
 */

public class NotificationUtils {
    private static final String TAG = NotificationUtils.class.getSimpleName();
    private static NotificationCompat.Builder mBuilder;

    public static void setNotification(JSONObject jsonObject) throws JSONException {
        mBuilder = new NotificationCompat.Builder(Support.getInstance());
        UserM userM;
        GroupM groupM;
        Intent intent = new Intent(Support.getInstance(), ChatActivity.class);
        intent.putExtra("isFromBar", true);
        if(jsonObject.getString("conType").equals(Constant.CONV_SN)){
            userM = new UserM();
            userM.setFullName(jsonObject.getString("userName"));
            userM.setUserId(jsonObject.getString("userID"));
            userM.setFcmToken(jsonObject.getString("userToken"));
            intent.putExtra("isGroup", false);
            intent.putExtra("data", new Gson().toJson(userM));
        } else {
            groupM = new GroupM();
            groupM.setName(jsonObject.getString("userName"));
            groupM.setId(jsonObject.getString("userID"));
            intent.putExtra("isGroup", true);
            intent.putExtra("data", new Gson().toJson(groupM));
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(Support.getInstance());
        stackBuilder.addParentStack(ChatActivity.class);
        stackBuilder.addNextIntent(intent);

        PendingIntent contentIntent = PendingIntent.getActivity(Support.getInstance(), (int)
                System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setContentTitle(jsonObject.getString("title"));

        if(jsonObject.getString("type").equals(Constant.TEXT_TYPE)){
            mBuilder.setContentText(jsonObject.getString("text"));
        } else {
            String imageUrl = jsonObject.getString("fileUri");
            Log.d(TAG, "Image Url:" + imageUrl);
            Bitmap bitmap = null;
            try {
                if (imageUrl != null && !imageUrl.isEmpty())
                    bitmap = Picasso.with(Support.getInstance()).load(imageUrl).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap));
        }

        showNotification();
    }

    private static void showNotification() {
        Bitmap largeIcon = BitmapFactory.decodeResource(Support.getInstance().getResources(),
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
                .from(Support.getInstance());
        int MESSAGE_NOTIFICATION_ID = 1;
        notificationManager.notify(MESSAGE_NOTIFICATION_ID, notify);
        PowerManager pm = (PowerManager) Support.getInstance().getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP, TAG);
        wl.acquire(15000);
    }
}
