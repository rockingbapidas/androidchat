package com.vantagecircle.chatapp.fcm;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.vantagecircle.chatapp.Support;
import com.vantagecircle.chatapp.data.Config;
import com.vantagecircle.chatapp.utils.NotificationUtils;
import com.vantagecircle.chatapp.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

public class FirebaseMessaging extends FirebaseMessagingService {
    private static final String TAG = FirebaseMessaging.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            JSONObject jsonObject = new JSONObject(remoteMessage.getData());
            Log.d(TAG, "onMessageReceived: " + jsonObject);
            if(Tools.isAppInBackground(Support.getInstance())){
                try {
                    NotificationUtils.setNotification(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                if(Support.isChatWindowActive){
                    sendBroadcast(Config.FIREBASE_DB_SYNC);
                }
            }
        }
    }

    private void sendBroadcast(String receiver) {
        Intent intent = new Intent(Config.FCM_RECEIVER);
        intent.putExtra("status", receiver);
        LocalBroadcastManager.getInstance(Support.getInstance()).sendBroadcast(intent);
    }
}