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
        if (remoteMessage.getData().size() > 0) {
            JSONObject jsonObject = new JSONObject(remoteMessage.getData());
            Log.d(TAG, "onMessageReceived: " + jsonObject);
            if(ToolsUtils.isAppInBackground(SupportService.getInstance())){
                try {
                    new NotificationHandler(SupportService.getInstance()).setNotification(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d(TAG, "New Message arrived ==");
            }
        }
    }
}