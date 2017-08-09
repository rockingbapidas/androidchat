package com.vantagecircle.chatapp.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by bapidas on 09/08/17.
 */

public class SupportReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent pushIntent = new Intent(context, SupportService.class);
            context.startService(pushIntent);
        }
    }
}
