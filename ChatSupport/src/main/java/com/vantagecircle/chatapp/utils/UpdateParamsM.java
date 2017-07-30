package com.vantagecircle.chatapp.utils;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.vantagecircle.chatapp.Support;
import com.vantagecircle.chatapp.core.DataHandler;
import com.vantagecircle.chatapp.interfacePref.SharedPrefM;

import java.util.HashMap;

/**
 * Created by bapidas on 10/07/17.
 */

public class UpdateParamsM {
    private static final String TAG = UpdateParamsM.class.getSimpleName();

    public static void updateTokenToServer(String token) {
        try {
            String fcmToken;
            if (token != null) {
                fcmToken = token;
            } else {
                fcmToken = FirebaseInstanceId.getInstance().getToken();
            }
            new SharedPrefM(Support.getInstance()).saveString(Constant.FIREBASE_TOKEN, fcmToken);

            if (Support.getUserInstance() != null) {
                DataHandler dataHandler = new DataHandler(Support.getUserReference()
                        .child(Support.getUserInstance().getUid())) {
                    @Override
                    protected void onSuccess(String t) {
                        Log.d(TAG, "Firebase Token updated successfully");
                    }

                    @Override
                    protected void onFail(String e) {
                        Log.d(TAG, "Firebase Token updated error " + e);
                    }
                };
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put(Constant.FIREBASE_TOKEN, fcmToken);
                dataHandler.updateData(hashMap);
            } else {
                Log.e(TAG, "Token cannot updated because firebase user is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateLastSeen(long timeStamp) {
        DataHandler dataHandler = new DataHandler(Support.getUserReference()
                .child(Support.getUserInstance().getUid())) {
            @Override
            protected void onSuccess(String t) {
                Log.d(TAG, "Last Seen updated successfully");
            }

            @Override
            protected void onFail(String e) {
                Log.d(TAG, "Last seen updated error " + e);
            }
        };
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constant.LAST_SEEN, timeStamp);
        dataHandler.updateData(hashMap);
    }

    public static void updateOnlineStatus(boolean status) {
        DataHandler dataHandler = new DataHandler(Support.getUserReference()
                .child(Support.getUserInstance().getUid())) {
            @Override
            protected void onSuccess(String t) {
                Log.d(TAG, "Online status updated successfully");
            }

            @Override
            protected void onFail(String e) {
                Log.d(TAG, "Online status updated error " + e);
            }
        };
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constant.ONLINE_STATUS, status);
        dataHandler.updateData(hashMap);
    }

    public static void updateSentStatus(String room, long timeStamp) {
        DataHandler dataHandler = new DataHandler(Support.getChatReference()
                .child(room).child(String.valueOf(timeStamp))) {
            @Override
            protected void onSuccess(String t) {
                Log.d(TAG, "Sent status updated successfully");
            }

            @Override
            protected void onFail(String e) {
                Log.d(TAG, "Sent status updated error " + e);
            }
        };
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constant.SENT_STATUS, true);
        dataHandler.updateData(hashMap);
    }

    public static void updateReadStatus(String room, long timeStamp) {
        DataHandler dataHandler = new DataHandler(Support.getChatReference()
                .child(room).child(String.valueOf(timeStamp))) {
            @Override
            protected void onSuccess(String t) {
                Log.d(TAG, "Read status updated successfully");
            }

            @Override
            protected void onFail(String e) {
                Log.d(TAG, "Read status updated error " + e);
            }
        };
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constant.READ_STATUS, true);
        dataHandler.updateData(hashMap);
    }

    public static void updateFileUrl(String room, long timeStamp, String url) {
        DataHandler dataHandler = new DataHandler(Support.getChatReference()
                .child(room).child(String.valueOf(timeStamp))) {
            @Override
            protected void onSuccess(String t) {
                Log.d(TAG, "File uri updated successfully");
            }

            @Override
            protected void onFail(String e) {
                Log.d(TAG, "File uri updated error " + e);
            }
        };
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constant.FILE_URL, url);
        dataHandler.updateData(hashMap);
    }
}
