package com.vantagecircle.chatapp.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.vantagecircle.chatapp.R;
import com.vantagecircle.chatapp.Support;
import com.vantagecircle.chatapp.adapter.ChatMAdapter;
import com.vantagecircle.chatapp.holder.ChatMViewHolder;
import com.vantagecircle.chatapp.core.DataClass;
import com.vantagecircle.chatapp.core.GetParent;
import com.vantagecircle.chatapp.utils.Config;
import com.vantagecircle.chatapp.utils.UpdateParamsM;
import com.vantagecircle.chatapp.model.ChatM;
import com.vantagecircle.chatapp.model.UserM;
import com.vantagecircle.chatapp.model.NotificationM;
import com.vantagecircle.chatapp.services.SendNotification;
import com.vantagecircle.chatapp.utils.DateUtils;
import com.vantagecircle.chatapp.utils.Tools;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by bapidas on 10/07/17.
 */
public class ChatActivity extends BaseChatActivity {
    private static final String TAG = ChatActivity.class.getSimpleName();

    @Override
    protected void initializeUser() {
        super.initializeUser();
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
