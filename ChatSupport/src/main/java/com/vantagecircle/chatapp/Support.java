package com.vantagecircle.chatapp;

import android.support.multidex.MultiDexApplication;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.vantagecircle.chatapp.data.Config;
import com.vantagecircle.chatapp.model.UserM;

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
        return getDatabaseInstance().getReference(Config.DATABASE_USER_REF);
    }

    public static synchronized DatabaseReference getChatReference() {
        return getDatabaseInstance().getReference(Config.DATABASE_CHAT_REF);
    }

    public static synchronized DatabaseReference getGroupReference() {
        return getDatabaseInstance().getReference(Config.DATABASE_GROUP_REF);
    }

    public static synchronized FirebaseStorage getStorageInstance(){
        return FirebaseStorage.getInstance();
    }

    public static synchronized StorageReference getChatImageReference(){
        return getStorageInstance().getReference(Config.STORAGE_CHAT_IMAGE_REF);
    }

    public static synchronized StorageReference getChatFileReference(){
        return getStorageInstance().getReference().child(Config.STORAGE_CHAT_FILE_REF);
    }

    public static synchronized StorageReference getChatVideoReference(){
        return getStorageInstance().getReference().child(Config.STORAGE_CHAT_VIDEO_REF);
    }

    public static synchronized StorageReference getUserImageReference(){
        return getStorageInstance().getReference().child(Config.STORAGE_USER_IMAGE_REF);
    }

    public static synchronized void setIsChatWindowActive(boolean active) {
        isChatWindowActive = active;
    }

    public static synchronized boolean getIsChatWindowActive() {
        return isChatWindowActive;
    }
}
