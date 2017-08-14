package com.vantagecircle.chatapp.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.vantagecircle.chatapp.R;
import com.vantagecircle.chatapp.services.SupportService;
import com.vantagecircle.chatapp.core.model.DataModel;
import com.vantagecircle.chatapp.core.GetDataHandler;
import com.vantagecircle.chatapp.core.interfaceC.ValueInterface;
import com.vantagecircle.chatapp.model.RoomM;
import com.vantagecircle.chatapp.model.UserM;
import com.vantagecircle.chatapp.utils.ConfigUtils;
import com.vantagecircle.chatapp.utils.Constants;
import com.vantagecircle.chatapp.utils.MainFileUtils;
import com.vantagecircle.chatapp.utils.ToolsUtils;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by bapidas on 10/07/17.
 */

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initTest();
        //initApp();
    }

    private void initTest(){
        UserM userM = new UserM();
        userM.setUserId("dll6E6arOmXcWNSeKEk5WH7IcnW2");
        userM.setUsername("bapi@gmail.com");
        userM.setFullName("Bapi Das");
        userM.setFcmToken("fl0dEIo-qV0:APA91bETyHJMRRQzz4nxyExsRlQ1I-JTyfPvzEJw1-tlMcAkBkq4ZW4Ub1DFL3QlyvXMgSTjD41Bs9eqrbErvwSBu3JOjzTNjmxzeO6DNqdckKdXdsbFYIkQDPyGT6d-qmhD0ihwFPl0");
        userM.setLastSeenTime(new Date().getTime());
        userM.setNotificationCount(0);
        userM.setOnline(true);
        userM.setUserType("user");
        ArrayList<RoomM> arrayList = new ArrayList<>();
        RoomM roomM = new RoomM();
        roomM.setRoomId("CAOL5K");
        roomM.setRoomName("CAOL5K_QWERTY");
        arrayList.add(roomM);
        userM.setRoomMArrayList(arrayList);

        SupportService.init(getApplicationContext(), userM);
        Intent intent = new Intent(SplashActivity.this, ChatActivity.class);
        intent.putExtra("isContest", true);
        intent.putExtra("contest_id", "CAOL5K");
        startActivity(intent);
        finish();
    }

    private void initApp(){
        if (SupportService.getUserInstance() != null) {
            GetDataHandler getDataHandler = new GetDataHandler();
            getDataHandler.setDataReference(SupportService.getUserReference()
                    .child(SupportService.getUserInstance().getUid()));
            getDataHandler.setSingleValueEventListener(new ValueInterface() {
                @Override
                public void onDataSuccess(DataModel dataModel) {
                    UserM userM = dataModel.getDataSnapshot().getValue(UserM.class);
                    if (userM != null ) {
                        SupportService.init(getApplicationContext(), userM);

                        Intent intent = new Intent(SplashActivity.this, UserActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        //logout from firebase and try again
                        SupportService.getAuthInstance().signOut();
                        Toast.makeText(getApplicationContext(), "User data not found please login",
                                Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onDataCancelled(DataModel dataModel) {
                    SupportService.getAuthInstance().signOut();
                    Toast.makeText(getApplicationContext(), dataModel.getDatabaseError().getMessage(),
                            Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        } else {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }
}
