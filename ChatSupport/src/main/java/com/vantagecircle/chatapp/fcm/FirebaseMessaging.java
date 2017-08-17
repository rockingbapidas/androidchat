package com.vantagecircle.chatapp.fcm;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.vantagecircle.chatapp.services.SupportService;
import com.vantagecircle.chatapp.core.NotificationHandler;
import com.vantagecircle.chatapp.utils.ToolsUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class FirebaseMessaging extends FirebaseMessagingService {
    private static final String TAG = FirebaseMessaging.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "onMessageReceived: " + remoteMessage.getData());
        if(SupportService.isChatNotification(remoteMessage)){
            if(ToolsUtils.isAppInBackground(SupportService.getInstance())){
                try {
                    new NotificationHandler(SupportService.getInstance())
                            .setNotification(remoteMessage);
               /* new NotificationHandler(SupportService.getInstance())
                        .setNotification(new JSONObject(remoteMessage.getData()));*/
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d(TAG, "New Message arrived ====== ");
            }
        } else {
            Log.d(TAG, "New Message arrived ======");
        }
    }
}