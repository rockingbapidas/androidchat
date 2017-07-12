package com.vantagecircle.chatapp;

import android.support.multidex.MultiDexApplication;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vantagecircle.chatapp.data.Config;
import com.vantagecircle.chatapp.model.UserM;

/**
 * Created by bapidas on 10/07/17.
 */

public class Support extends MultiDexApplication {
    private static Support mInstance;
    public static String id = null;
    public static UserM userM = null;
    public static boolean isChatWindowActive;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized Support getInstance(){
        return mInstance;
    }

    public static synchronized FirebaseAuth getAuthInstance(){
        return FirebaseAuth.getInstance();
    }

    public static synchronized FirebaseUser getUserInstance(){
        return getAuthInstance().getCurrentUser();
    }

    public static synchronized FirebaseDatabase getDatabaseInstance(){
        return FirebaseDatabase.getInstance();
    }

    public static synchronized DatabaseReference getUserReference(){
        return getDatabaseInstance().getReference(Config.USER_REF);
    }

    public static synchronized DatabaseReference getChatReference(){
        return getDatabaseInstance().getReference(Config.CHAT_REF);
    }

    public static synchronized void setIsChatWindowActive(boolean active){
        isChatWindowActive = active;
    }
}
