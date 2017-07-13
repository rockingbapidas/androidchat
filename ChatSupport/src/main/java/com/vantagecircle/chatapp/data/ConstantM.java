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

    public static void updateToken(String token) {
        try {
            String fcmToken;
            if (token != null) {
                fcmToken = token;
            } else {
                fcmToken = FirebaseInstanceId.getInstance().getToken();
                new SharedPrefM(Support.getInstance()).saveString(Config.FIREBASE_TOKEN, fcmToken);
            }
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setLastMessage(String message) {
        try {
            if (Support.getUserInstance() != null) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put(Config.LAST_MESSAGE, message);
                Support.getUserReference().child(Support.getUserInstance()
                        .getUid())
                        .updateChildren(hashMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d(TAG, "Last Message updated successfully");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "Last Message updated error " + e.getMessage());
                            }
                        });
            } else {
                Log.d(TAG, "Firebase user is null");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void setLastSeen(long timeStamp) {
        try {
            if (Support.getUserInstance() != null) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put(Config.LAST_SEEN, timeStamp);
                Support.getUserReference().child(Support.getUserInstance().getUid())
                        .updateChildren(hashMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d(TAG, "Last Seen updated successfully");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "Last seen updated error " + e.getMessage());
                            }
                        });
            } else {
                Log.d(TAG, "Firebase user is null");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void setOnlineStatus(boolean status) {
        try {
            if (Support.getUserInstance() != null) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put(Config.ONLINE_STATUS, status);
                Support.getUserReference().child(Support.getUserInstance()
                        .getUid())
                        .updateChildren(hashMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d(TAG, "Online status updated successfully");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "Online status updated error " + e.getMessage());
                            }
                        });
            } else {
                Log.d(TAG, "Firebase user is null");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
