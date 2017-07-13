package com.vantagecircle.chatapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.vantagecircle.chatapp.R;
import com.vantagecircle.chatapp.Support;
import com.vantagecircle.chatapp.data.Config;
import com.vantagecircle.chatapp.model.UserM;

/**
 * Created by bapidas on 10/07/17.
 */

public class SplashActivity extends AppCompatActivity {
    ValueEventListener valueEventListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (Support.getUserInstance() != null) {
            setupData();
        } else {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void setupData() {
        try {
            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserM userM = dataSnapshot.getValue(UserM.class);
                    if (userM != null) {
                        Support.id = Support.getUserInstance().getUid();
                        Support.userM = userM;
                        if (userM.getUserType().equals(Config._ADMIN)) {
                            Intent intent = new Intent(SplashActivity.this, AdminActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(SplashActivity.this, UserActivity.class);
                            startActivity(intent);
                            finish();
                        }
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
                    Support.getAuthInstance().signOut();
                    Toast.makeText(getApplicationContext(), databaseError.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            };
            Support.getUserReference().child(Support.getUserInstance().getUid())
                    .addValueEventListener(valueEventListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (valueEventListener != null) {
            Support.getUserReference().removeEventListener(valueEventListener);
        }
    }
}
