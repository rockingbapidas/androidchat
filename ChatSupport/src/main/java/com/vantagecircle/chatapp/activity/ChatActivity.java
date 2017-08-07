package com.vantagecircle.chatapp.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.vantagecircle.chatapp.core.abstractC.NewBaseActivity;

/**
 * Created by bapidas on 10/07/17.
 */
public class ChatActivity extends NewBaseActivity {
    private static final String TAG = ChatActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }
}
