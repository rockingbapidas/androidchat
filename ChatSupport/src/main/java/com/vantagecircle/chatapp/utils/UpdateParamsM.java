package com.vantagecircle.chatapp.utils;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.vantagecircle.chatapp.Support;
import com.vantagecircle.chatapp.core.DataClass;
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
                DataClass dataClass = new DataClass(Support.getUserReference()
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
                dataClass.updateData(hashMap);
            } else {
                Log.e(TAG, "Firebase user is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateLastSeen(long timeStamp) {
        DataClass dataClass = new DataClass(Support.getUserReference()
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
        dataClass.updateData(hashMap);
    }

    public static void updateOnlineStatus(boolean status) {
        DataClass dataClass = new DataClass(Support.getUserReference()
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
        dataClass.updateData(hashMap);
    }

    public static void updateSentStatus(String room, long timeStamp) {
        DataClass dataClass = new DataClass(Support.getChatReference()
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
        dataClass.updateData(hashMap);
    }

    public static void updateReadStatus(String room, long timeStamp) {
        DataClass dataClass = new DataClass(Support.getChatReference()
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
        dataClass.updateData(hashMap);
    }

    public static void updateFileUrl(String room, long timeStamp, String url) {
        DataClass dataClass = new DataClass(Support.getChatReference()
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
        dataClass.updateData(hashMap);
    }
}
