package com.vantagecircle.chatapp.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.vantagecircle.chatapp.R;
import com.vantagecircle.chatapp.Support;
import com.vantagecircle.chatapp.core.AuthClass;
import com.vantagecircle.chatapp.core.DataClass;
import com.vantagecircle.chatapp.data.Config;
import com.vantagecircle.chatapp.model.UserM;
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
        username = usernameEdit.getText().toString();
        String password = passwordEdit.getText().toString();
        fullName = fullNameEdit.getText().toString();

        AuthClass authClass = new AuthClass(Support.getAuthInstance()) {
            @Override
            protected void onSuccess(String t) {
                progressDialog.setMessage("Creating account and store data");
                setupData();
            }

            @Override
            protected void onFail(String e) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(mContext, e, Toast.LENGTH_SHORT).show();
            }
        };
        authClass.performSignup(username, password);
    }

    private void setupData() {
            final UserM userM = new UserM();
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

            DataClass dataClass = new DataClass(Support.getUserReference().child(userM.getUserId())) {
                @Override
                protected void onSuccess(String t) {
                    Support.userM = userM;
                    Support.id = userM.getUserId();
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(mContext, "Account is created successfully",
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignupActivity.this, UserActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }

                @Override
                protected void onFail(String e) {
                    Support.getUserInstance().delete();
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(mContext, e, Toast.LENGTH_SHORT).show();
                }
            };
            dataClass.insertData(userM);
    }
}
