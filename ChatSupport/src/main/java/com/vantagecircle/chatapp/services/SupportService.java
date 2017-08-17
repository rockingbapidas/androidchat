package com.vantagecircle.chatapp.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.vantagecircle.chatapp.R;
import com.vantagecircle.chatapp.utils.Constants;
import com.vantagecircle.chatapp.model.UserM;
import com.vantagecircle.chatapp.utils.MainFileUtils;
import com.vantagecircle.chatapp.utils.ToolsUtils;

import org.json.JSONObject;

import java.io.File;

/**
 * Created by bapidas on 10/07/17.
 */

public class SupportService extends Service {
    private static final String TAG = SupportService.class.getSimpleName();
    private static SupportService mInstance;
    private static boolean isChatWindowActive;

    public static String id = null;
    public static UserM userM = null;

    public static void init(Context context, UserM model) {
        userM = model;
        id = userM.getUserId();

        //start global service class
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

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        if (mInstance == null) {
            mInstance = this;
        }
        if (getDatabaseInstance() == null) {
            getDatabaseInstance().setPersistenceEnabled(true);
            getUserReference().keepSynced(true);
            getChatReference().keepSynced(true);
        }
        makeDir();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind");
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.e(TAG, "onRebind");
        super.onRebind(intent);
    }

    @Override
    public void onLowMemory() {
        Log.e(TAG, "onLowMemory");
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        Log.e(TAG, "onTrimMemory");
        super.onTrimMemory(level);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e(TAG, "onTaskRemoved");
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.e(TAG, "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }

    public static synchronized SupportService getInstance() {
        return mInstance;
    }

    public static synchronized FirebaseAuth getAuthInstance() {
        return FirebaseAuth.getInstance();
    }

    public static synchronized FirebaseUser getUserInstance() {
        return getAuthInstance().getCurrentUser();
    }

    public static synchronized FirebaseDatabase getDatabaseInstance() {
        return FirebaseDatabase.getInstance();
    }

    public static synchronized DatabaseReference getUserReference() {
        return getDatabaseInstance().getReference(Constants.DATABASE_USER_REF);
    }

    public static synchronized DatabaseReference getChatReference() {
        return getDatabaseInstance().getReference(Constants.DATABASE_CHAT_REF);
    }

    public static synchronized DatabaseReference getGroupReference() {
        return getDatabaseInstance().getReference(Constants.DATABASE_GROUP_REF);
    }

    public static synchronized FirebaseStorage getStorageInstance() {
        return FirebaseStorage.getInstance();
    }

    public static synchronized StorageReference getChatImageReference() {
        return getStorageInstance().getReference(Constants.STORAGE_CHAT_IMAGE_REF);
    }

    public static synchronized void setIsChatWindowActive(boolean active) {
        isChatWindowActive = active;
    }

    public static synchronized boolean getIsChatWindowActive() {
        return isChatWindowActive;
    }

    public static void makeDir() {
        File appFile = new File(Environment.getExternalStorageDirectory() + File.separator + Constants.APPNAME);
        try {
            if (!appFile.isDirectory()) {
                appFile.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            makeInnerDir();
        }
    }

    public static void makeInnerDir() {
        String s_folder[] = mInstance.getResources().getStringArray(R.array.folders_name);
        for (String aS_folder : s_folder) {
            File appFile = new File(Environment.getExternalStorageDirectory() + File.separator +
                    Constants.APPNAME + File.separator + aS_folder);
            try {
                if (!appFile.isDirectory()) {
                    appFile.mkdir();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
