package com.vantagecircle.chatapp.fcm;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.vantagecircle.chatapp.Support;
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
                if(Support.getIsChatWindowActive()){
                    Log.d(TAG, "New Message arrived");
                } else {
                    Toast.makeText(Support.getInstance(), "New Message arrived", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}