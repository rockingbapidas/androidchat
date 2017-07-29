package com.vantagecircle.chatapp.fcm;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.vantagecircle.chatapp.utils.UpdateParamsM;

public class FirebaseInstanceID extends FirebaseInstanceIdService {
    private static final String TAG = FirebaseInstanceID.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "onTokenRefresh: " + refreshedToken);
        UpdateParamsM.updateTokenToServer(refreshedToken);
    }
}