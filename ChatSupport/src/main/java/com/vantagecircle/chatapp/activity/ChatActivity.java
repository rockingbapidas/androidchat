package com.vantagecircle.chatapp.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.vantagecircle.chatapp.core.abstractC.BaseActivity;
import com.vantagecircle.chatapp.core.abstractC.ParentActivity;
import com.vantagecircle.chatapp.services.SupportService;
import com.vantagecircle.chatapp.utils.ConfigUtils;
import com.vantagecircle.chatapp.utils.Constants;
import com.vantagecircle.chatapp.utils.MainFileUtils;
import com.vantagecircle.chatapp.utils.ToolsUtils;

/**
 * Created by bapidas on 10/07/17.
 */
public class ChatActivity extends ParentActivity {
    private static final String TAG = ChatActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPermission();
    }

    private void initPermission() {
        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!ConfigUtils.isHasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, Constants.REQUEST_STORAGE_PERMISSION);
        } else {
            initialize();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permission granted");
                    initialize();
                } else {
                    Log.d(TAG, "Permission not granted");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
