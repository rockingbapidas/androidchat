package com.vantagecircle.chatapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.vantagecircle.chatapp.R;
import com.vantagecircle.chatapp.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by bapidas on 10/07/17.
 */

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        try {
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    if (Tools.isNetworkAvailable(getApplicationContext())) {
                        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "No internet connection found",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            };
            new Timer().schedule(timerTask, 2000);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }
}
