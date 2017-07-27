package com.vantagecircle.chatapp.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by bapidas on 27/07/17.
 */

public abstract class BaseChatActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(loadView());
        initData();
        initToolBar();
        initView();
        initListener();
        initRecycler();
        getChatHistory();
    }

    protected abstract int loadView();

    protected abstract void initData();

    protected abstract void initToolBar();

    protected abstract void initView();

    protected abstract void initListener();

    protected abstract void initRecycler();

    protected abstract void getChatHistory();
}
