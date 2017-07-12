package com.vantagecircle.chatapp.data;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.vantagecircle.chatapp.Support;
import com.vantagecircle.chatapp.utils.SharedPrefM;
import com.vantagecircle.chatapp.utils.Tools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by bapidas on 10/07/17.
 */

public class ConstantM {
    private static final String TAG = ConstantM.class.getSimpleName();
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;

    public static void updateToken(String token) {
        String fcmToken;
        if (token != null) {
            fcmToken = token;
        } else {
            fcmToken = FirebaseInstanceId.getInstance().getToken();
            new SharedPrefM(Support.getInstance()).saveString(Config.FIREBASE_TOKEN, fcmToken);
        }
        if (Tools.isNetworkAvailable(Support.getInstance())) {
            if (Support.getUserInstance() != null) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put(Config.FIREBASE_TOKEN, fcmToken);
                Support.getUserReference().child(Support.getUserInstance()
                        .getUid())
                        .updateChildren(hashMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d(TAG, "Firebase Token updated successfully");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "Firebase Token updated error " + e.getMessage());
                            }
                        });
            } else {
                Log.d(TAG, "Firebase user is null");
            }
        } else {
            Log.d(TAG, "No internet connection found");
        }
    }

    public static String getTimeAgo(long time) {
        long timestamp = time;
        if (time < 1000000000000L) {
            time *= 1000;
        }
        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "Just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "Yesterday";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yy", Locale.ENGLISH);
            Date netDate = new Date(timestamp * 1000);
            return sdf.format(netDate);
        }
    }
}
