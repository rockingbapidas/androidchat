package com.vantagecircle.chatapp;

import android.os.Environment;
import android.support.multidex.MultiDexApplication;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.vantagecircle.chatapp.utils.Constants;
import com.vantagecircle.chatapp.model.UserM;

import java.io.File;

/**
 * Created by bapidas on 10/07/17.
 */

public class Support extends MultiDexApplication {
    private static Support mInstance;
    private static boolean isChatWindowActive;
    public static String id = null;
    public static UserM userM = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        makeDir();
        getDatabaseInstance().setPersistenceEnabled(true);
        getUserReference().keepSynced(true);
        getChatReference().keepSynced(true);
    }

    public static synchronized Support getInstance() {
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

    public static synchronized FirebaseStorage getStorageInstance(){
        return FirebaseStorage.getInstance();
    }

    public static synchronized StorageReference getChatImageReference(){
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
