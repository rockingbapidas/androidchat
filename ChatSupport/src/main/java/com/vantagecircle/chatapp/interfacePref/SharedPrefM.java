package com.vantagecircle.chatapp.interfacePref;

import android.content.Context;
import android.content.SharedPreferences;

import com.vantagecircle.chatapp.utils.Config;

/**
 * Created by bapidas on 11/07/17.
 */

public class SharedPrefM {
    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    public SharedPrefM(Context mContext) {
        this.mContext = mContext;
    }

    public void saveString(String key, String value) {
        mSharedPreferences = mContext.getSharedPreferences(Config.APP_PREFS,
                Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mEditor.putString(key, value);
        mEditor.apply();
    }

    public String getString(String key) {
        mSharedPreferences = mContext.getSharedPreferences(Config.APP_PREFS,
                Context.MODE_PRIVATE);
        return mSharedPreferences.getString(key, null);
    }

    public void clear(){
        mSharedPreferences = mContext.getSharedPreferences(Config.APP_PREFS,
                Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mEditor.clear();
        mEditor.apply();
    }
}
