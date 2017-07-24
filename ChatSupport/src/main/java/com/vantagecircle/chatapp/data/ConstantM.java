package com.vantagecircle.chatapp.data;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.vantagecircle.chatapp.Support;
import com.vantagecircle.chatapp.utils.SharedPrefM;

import java.util.HashMap;

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

    public static void updateSentStatus(String room, long timeStamp) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Config.SENT_STATUS, true);
        Support.getChatReference().child(room)
                .child(String.valueOf(timeStamp))
                .updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "Sent status updated successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Sent status updated error " + e.getMessage());
                    }
                });
    }

    public static void updateReadStatus(String room, long timeStamp) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Config.READ_STATUS, true);
        Support.getChatReference().child(room)
                .child(String.valueOf(timeStamp))
                .updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "Read status updated successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Read status updated error " + e.getMessage());
                    }
                });
    }

    public static void updateFileUrl(String room, long timeStamp, String url) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Config.FILE_URL, url);
        Support.getChatReference().child(room)
                .child(String.valueOf(timeStamp))
                .updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "File uri updated successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "File uri updated error " + e.getMessage());
                    }
                });
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
        } catch (Exception e) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
