package com.vantagecircle.chatapp.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.vantagecircle.chatapp.utils.ToolsUtils;

/**
 * Created by bapidas on 09/08/17.
 */

public class SupportReceiver extends BroadcastReceiver {
    private final String TAG = SupportReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            if (ToolsUtils.isMyServiceRunning(context, SupportService.class)) {
                Log.d(TAG, "Service is already running");
                context.stopService(new Intent(context, SupportService.class));
                Intent pushIntent = new Intent(context, SupportService.class);
                context.startService(pushIntent);
            } else {
                Log.d(TAG, "Service is not running");
                Intent pushIntent = new Intent(context, SupportService.class);
                context.startService(pushIntent);
            }
        }
    }
}
