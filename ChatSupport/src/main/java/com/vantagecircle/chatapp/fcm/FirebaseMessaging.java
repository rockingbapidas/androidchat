package com.vantagecircle.chatapp.fcm;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessaging extends FirebaseMessagingService {
    private static final String TAG = FirebaseMessaging.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "remoteMessage: " + remoteMessage.getData());
        }
    }
}