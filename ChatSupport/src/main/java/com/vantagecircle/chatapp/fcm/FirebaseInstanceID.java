package com.vantagecircle.chatapp.fcm;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.vantagecircle.chatapp.Support;
import com.vantagecircle.chatapp.data.Config;
import com.vantagecircle.chatapp.utils.SharedPrefM;
import com.vantagecircle.chatapp.utils.Tools;

import java.util.HashMap;

public class FirebaseInstanceID extends FirebaseInstanceIdService {
    private static final String TAG = FirebaseInstanceID.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        new SharedPrefM(Support.getInstance()).saveString(Config.FIREBASE_TOKEN, token);
        if (Tools.isNetworkAvailable(Support.getInstance())) {
            if (Support.getUserInstance() != null) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put(Config.FIREBASE_TOKEN, token);
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
                                //Do error handling for updating token
                            }
                        });
            } else {
                Log.d(TAG, "Firebase user is null");
            }
        } else {
            Log.d(TAG, "No internet connection found");
        }
    }
}