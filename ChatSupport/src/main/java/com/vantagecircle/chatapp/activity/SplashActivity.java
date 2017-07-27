package com.vantagecircle.chatapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.vantagecircle.chatapp.R;
import com.vantagecircle.chatapp.Support;
import com.vantagecircle.chatapp.core.GetParent;
import com.vantagecircle.chatapp.model.UserM;

/**
 * Created by bapidas on 10/07/17.
 */

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (Support.getUserInstance() != null) {
            GetParent getParent = new GetParent() {
                @Override
                protected void onDataSuccess(DataSnapshot dataSnapshot) {
                    UserM userM = dataSnapshot.getValue(UserM.class);
                    if (userM != null ) {
                        Support.id = Support.getUserInstance().getUid();
                        Support.userM = userM;
                        Intent intent = new Intent(SplashActivity.this, UserActivity.class);
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
                protected void onDataCancelled(DatabaseError databaseError) {
                    Support.getAuthInstance().signOut();
                    Toast.makeText(getApplicationContext(), databaseError.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            };
            getParent.addSingleListener(Support.getUserReference().child(Support.getUserInstance().getUid()));
        } else {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
