package com.vantagecircle.chatapp.services;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.vantagecircle.chatapp.utils.Tools;

/**
 * Created by b on 27-07-2016.
 */
@SuppressLint("StaticFieldLeak")
public class SyncService extends IntentService {
    private static final String TAG = SyncService.class.getName();

    public SyncService() {
        super(TAG);
    }

    public static void startService(Context ctx) {
        if (Tools.isNetworkAvailable(ctx)) {
            Intent intent = new Intent(ctx, SyncService.class);
            ctx.startService(intent);
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

        }
    }
}
