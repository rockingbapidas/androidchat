package com.vantagecircle.chatapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.vantagecircle.chatapp.R;
import com.vantagecircle.chatapp.services.SupportService;
import com.vantagecircle.chatapp.core.model.DataModel;
import com.vantagecircle.chatapp.core.GetDataHandler;
import com.vantagecircle.chatapp.core.interfaceC.ValueInterface;
import com.vantagecircle.chatapp.model.RoomM;
import com.vantagecircle.chatapp.model.UserM;

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
        initLibraryMode();
        //initApplicationMode();
    }

    private void initLibraryMode(){
        UserM userM = new UserM();

        userM.setUserId("Yx6xPwD4fuSvqbIshMB39EtsP9q2");
        userM.setUsername("pallab@gmail.com");
        userM.setFullName("Pallab Kakoti");
        userM.setFcmToken("fty6Q-v5oY4:APA91bHYsnjIY0no2xvOh3BqU_" +
                "9kUbXBnAUMgtJCJ1k1aXtkwpgb1Aso_" +
                "KCnc8MHOTcs2NMm9ZYB-czjxUvyKqM_" +
                "Fz4aJqA1mO9Cr4pP-m30MVxm4YVCXTTH9ZWoCegYXaFAxd4GSsXJ");

       /* userM.setUserId("dll6E6arOmXcWNSeKEk5WH7IcnW2");
        userM.setUsername("bapi@gmail.com");
        userM.setFullName("Bapi Das");
        userM.setFcmToken("fl0dEIo-qV0:APA91bETyHJMRRQzz4nxyExsRlQ1I-JTyfPvzEJw1-" +
                "tlMcAkBkq4ZW4Ub1DFL3QlyvXMgSTjD41Bs9eqrbErvw" +
                "SBu3JOjzTNjmxzeO6DNqdckKdXdsbFYIkQDPyGT6d-qmhD0ihwFPl0");*/

        SupportService.init(getApplicationContext(), userM);

        FirebaseMessaging.getInstance().subscribeToTopic("AugustRunTo5k");
        Intent intent = new Intent(SplashActivity.this, ChatActivity.class);
        intent.putExtra("contest_id", "contest001");
        intent.putExtra("contest_name", "August Run To 5k");
        intent.putExtra("contest_room", "AugustRunTo5k");
        startActivity(intent);
        finish();
    }

    private void initApplicationMode(){
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
