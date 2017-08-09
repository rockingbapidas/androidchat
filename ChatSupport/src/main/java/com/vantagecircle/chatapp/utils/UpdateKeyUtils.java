package com.vantagecircle.chatapp.utils;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.vantagecircle.chatapp.services.SupportService;
import com.vantagecircle.chatapp.core.SetDataHandler;
import com.vantagecircle.chatapp.core.interfaceC.ResultInterface;
import com.vantagecircle.chatapp.pref.SharedPrefM;

import java.util.HashMap;

/**
 * Created by bapidas on 10/07/17.
 */

public class UpdateKeyUtils {
    private static final String TAG = UpdateKeyUtils.class.getSimpleName();

    //Update user status keys
    public static void updateTokenToServer(String token) {
        try {
            String fcmToken;
            if (token != null) {
                fcmToken = token;
            } else {
                fcmToken = FirebaseInstanceId.getInstance().getToken();
            }
            new SharedPrefM(SupportService.getInstance()).saveString(Constants.FIREBASE_TOKEN, fcmToken);

            if (SupportService.getUserInstance() != null) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put(Constants.FIREBASE_TOKEN, fcmToken);

                SetDataHandler setDataHandler = new SetDataHandler();
                setDataHandler.setDatabaseReference(SupportService.getUserReference()
                        .child(SupportService.getUserInstance().getUid()));
                setDataHandler.updateData(hashMap, new ResultInterface() {
                    @Override
                    public void onSuccess(String t) {
                        Log.d(TAG, "Firebase token updated successfully");
                    }

                    @Override
                    public void onFail(String e) {
                        Log.d(TAG, "Firebase token updated error " + e);
                    }
                });
            } else {
                Log.e(TAG, "Token cannot updated because firebase user is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateLastSeen(long timeStamp) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constants.LAST_SEEN, timeStamp);

        if (SupportService.getUserInstance() != null) {
            SetDataHandler setDataHandler = new SetDataHandler();
            setDataHandler.setDatabaseReference(SupportService.getUserReference()
                    .child(SupportService.getUserInstance().getUid()));
            setDataHandler.updateData(hashMap, new ResultInterface() {
                @Override
                public void onSuccess(String t) {
                    Log.d(TAG, "Last seen time updated successfully");
                }

                @Override
                public void onFail(String e) {
                    Log.d(TAG, "Last seen time not updated " + e);
                }
            });
        } else {
            Log.e(TAG, "Last seen cannot updated because firebase user is null");
        }
    }

    public static void updateOnlineStatus(boolean status) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constants.ONLINE_STATUS, status);

        if (SupportService.getUserInstance() != null) {
            SetDataHandler setDataHandler = new SetDataHandler();
            setDataHandler.setDatabaseReference(SupportService.getUserReference()
                    .child(SupportService.getUserInstance().getUid()));
            setDataHandler.updateData(hashMap, new ResultInterface() {
                @Override
                public void onSuccess(String t) {
                    Log.d(TAG, "Online status updated successfully");
                }

                @Override
                public void onFail(String e) {
                    Log.d(TAG, "Online status not updated  " + e);
                }
            });
        } else {
            Log.e(TAG, "Online status cannot updated because firebase user is null");
        }

    }


    //Update Chat status keys
    public static void updateSentStatus(String room, long timeStamp) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constants.SENT_STATUS, true);

        SetDataHandler setDataHandler = new SetDataHandler();
        setDataHandler.setDatabaseReference(SupportService.getChatReference()
                .child(room).child(String.valueOf(timeStamp)));
        setDataHandler.updateData(hashMap, new ResultInterface() {
            @Override
            public void onSuccess(String t) {
                Log.d(TAG, "Sent status updated successfully");
            }

            @Override
            public void onFail(String e) {
                Log.d(TAG, "Sent status not updated  " + e);
            }
        });
    }

    public static void updateReadStatus(String room, long timeStamp) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constants.READ_STATUS, true);

        SetDataHandler setDataHandler = new SetDataHandler();
        setDataHandler.setDatabaseReference(SupportService.getChatReference()
                .child(room).child(String.valueOf(timeStamp)));
        setDataHandler.updateData(hashMap, new ResultInterface() {
            @Override
            public void onSuccess(String t) {
                Log.d(TAG, "Read status updated successfully");
            }

            @Override
            public void onFail(String e) {
                Log.d(TAG, "Read status not updated  " + e);
            }
        });
    }

    public static void updateFileUrl(String room, long timeStamp, String url) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constants.FILE_URL, url);

        SetDataHandler setDataHandler = new SetDataHandler();
        setDataHandler.setDatabaseReference(SupportService.getChatReference()
                .child(room).child(String.valueOf(timeStamp)));
        setDataHandler.updateData(hashMap, new ResultInterface() {
            @Override
            public void onSuccess(String t) {
                Log.d(TAG, "File url updated successfully");
            }

            @Override
            public void onFail(String e) {
                Log.d(TAG, "File url not updated" + e);
            }
        });
    }
}
