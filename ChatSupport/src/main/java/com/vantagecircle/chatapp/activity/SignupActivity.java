package com.vantagecircle.chatapp.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.vantagecircle.chatapp.R;
import com.vantagecircle.chatapp.Support;
import com.vantagecircle.chatapp.data.Config;
import com.vantagecircle.chatapp.model.UserM;
import com.vantagecircle.chatapp.other.AdminActivity;
import com.vantagecircle.chatapp.utils.SharedPrefM;
import com.vantagecircle.chatapp.utils.Tools;

/**
 * Created by bapidas on 10/07/17.
 */

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private Activity activity;
    private Context mContext;
    private EditText usernameEdit, passwordEdit, fullNameEdit;
    private Button btnSignup;
    private String username;
    private String fullName;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        mContext = getApplicationContext();
        setContentView(R.layout.activity_signup);
        initView();
        initListener();
    }

    private void initView() {
        usernameEdit = (AppCompatEditText) findViewById(R.id.usernameEdit);
        passwordEdit = (AppCompatEditText) findViewById(R.id.passwordEdit);
        fullNameEdit = (AppCompatEditText) findViewById(R.id.fullNameEdit);
        btnSignup = (Button) findViewById(R.id.btnSignup);
    }

    private void initListener() {
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (usernameEdit.getText().toString().length() == 0 &&
                        passwordEdit.getText().toString().length() == 0 &&
                        fullNameEdit.getText().toString().length() == 0) {
                    Toast.makeText(mContext, "All Details is cannot be blank",
                            Toast.LENGTH_SHORT).show();
                } else if (usernameEdit.getText().toString().length() == 0) {
                    Toast.makeText(mContext, "Username cannot be blank",
                            Toast.LENGTH_SHORT).show();
                } else if (passwordEdit.getText().toString().length() == 0) {
                    Toast.makeText(mContext, "Password cannot be blank",
                            Toast.LENGTH_SHORT).show();
                } else if (fullNameEdit.getText().toString().length() == 0) {
                    Toast.makeText(mContext, "Full name cannot be blank",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (Tools.isNetworkAvailable(mContext)) {
                        Tools.hideKeyboard(activity);
                        progressDialog = new ProgressDialog(activity);
                        progressDialog.setMessage("Validating fields and data");
                        progressDialog.show();
                        userSignup();
                    } else {
                        Toast.makeText(mContext, "Please check your internet connection",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void userSignup() {
        try {
            username = usernameEdit.getText().toString();
            String password = passwordEdit.getText().toString();
            fullName = fullNameEdit.getText().toString();

            Support.getAuthInstance()
                    .createUserWithEmailAndPassword(username, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressDialog.setMessage("Validating fields success");
                                setupData();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(mContext, e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupData() {
        try {
            UserM userM = new UserM();
            userM.setUserId(Support.getUserInstance().getUid());
            userM.setUsername(username);
            userM.setFullName(fullName);
            String fcmToken;
            if (new SharedPrefM(mContext).getString(Config.FIREBASE_TOKEN) != null) {
                fcmToken = new SharedPrefM(mContext).getString(Config.FIREBASE_TOKEN);
            } else {
                fcmToken = FirebaseInstanceId.getInstance().getToken();
                new SharedPrefM(Support.getInstance()).saveString(Config.FIREBASE_TOKEN, fcmToken);
            }
            userM.setFcmToken(fcmToken);
            userM.setLastSeenTime(System.currentTimeMillis());
            userM.setNotificationCount(0);
            userM.setOnline(true);
            userM.setUserType(Config._USER);
            addUserData(userM);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addUserData(final UserM userM){
        try {
            progressDialog.setMessage("Creating account and store data");
            Support.getUserReference().child(userM.getUserId()).setValue(userM)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.e(TAG, "onComplete ");
                            Support.userM = userM;
                            Support.id = userM.getUserId();
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Toast.makeText(mContext, "Account is created successfully",
                                    Toast.LENGTH_SHORT).show();
                            if (userM.getUserType().equals(Config._USER)) {
                                Intent intent = new Intent(SignupActivity.this, UserActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(SignupActivity.this, AdminActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "onFailure ");
                            //delete account and try again
                            Support.getUserInstance().delete();
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Toast.makeText(mContext, "Account creation error try again",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
