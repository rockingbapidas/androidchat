package com.vantagecircle.chatapp.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.vantagecircle.chatapp.Support;
import com.vantagecircle.chatapp.core.BaseChatActivity;

/**
 * Created by bapidas on 10/07/17.
 */
public class ChatActivity extends BaseChatActivity {
    private static final String TAG = ChatActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        Support.setIsChatWindowActive(true);
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        Support.setIsChatWindowActive(false);
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }
}
