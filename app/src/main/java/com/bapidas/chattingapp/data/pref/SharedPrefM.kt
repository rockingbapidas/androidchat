package com.bapidas.chattingapp.data.pref

import android.content.Context
import android.content.SharedPreferences
import com.bapidas.chattingapp.utils.Constants

/**
 * Created by bapidas on 11/07/17.
 */
class SharedPrefM(private val mContext: Context) {
    private var mSharedPreferences: SharedPreferences? = null
    private var mEditor: SharedPreferences.Editor? = null

    fun saveString(key: String?, value: String?) {
        mSharedPreferences = mContext.getSharedPreferences(Constants.APP_PREFS,
                Context.MODE_PRIVATE)
        mEditor = mSharedPreferences?.edit()
        mEditor?.putString(key, value)
        mEditor?.apply()
    }

    fun getString(key: String?): String? {
        mSharedPreferences = mContext.getSharedPreferences(Constants.APP_PREFS,
                Context.MODE_PRIVATE)
        return mSharedPreferences?.getString(key, null)
    }

    fun clear() {
        mSharedPreferences = mContext.getSharedPreferences(Constants.APP_PREFS,
                Context.MODE_PRIVATE)
        mEditor = mSharedPreferences?.edit()
        mEditor?.clear()
        mEditor?.apply()
    }

}