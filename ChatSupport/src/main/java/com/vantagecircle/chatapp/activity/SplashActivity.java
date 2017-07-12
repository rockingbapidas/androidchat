package com.vantagecircle.chatapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.vantagecircle.chatapp.R;
import com.vantagecircle.chatapp.Support;
import com.vantagecircle.chatapp.model.UserM;
import com.vantagecircle.chatapp.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by bapidas on 10/07/17.
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (Tools.isNetworkAvailable(getApplicationContext())) {
            if (Support.getUserInstance() != null) {
                setupData();
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

    private void setupData(){
        try {
            Support.getUserReference()
                    .child(Support.getUserInstance().getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            UserM userM = dataSnapshot.getValue(UserM.class);
                            if (userM != null) {
                                Support.id = Support.getUserInstance().getUid();
                                Support.userM = userM;
                                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                //logout from firebase and try again
                                Support.getAuthInstance().signOut();
                                Toast.makeText(getApplicationContext(), "User data fetch error try again",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //logout from firebase and try again
                            Support.getAuthInstance().signOut();
                            Toast.makeText(getApplicationContext(), databaseError.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
