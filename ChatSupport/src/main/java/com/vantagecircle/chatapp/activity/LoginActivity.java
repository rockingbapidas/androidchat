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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.vantagecircle.chatapp.R;
import com.vantagecircle.chatapp.Support;
import com.vantagecircle.chatapp.core.GetParent;
import com.vantagecircle.chatapp.utils.Config;
import com.vantagecircle.chatapp.core.AuthClass;
import com.vantagecircle.chatapp.utils.UpdateParamsM;
import com.vantagecircle.chatapp.model.UserM;
import com.vantagecircle.chatapp.interfacePref.SharedPrefM;
import com.vantagecircle.chatapp.utils.Tools;

/**
 * Created by bapidas on 10/07/17.
 */

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private Activity activity;
    private Context mContext;
    private EditText usernameEdit, passwordEdit;
    private Button btnLogin, btnSignup;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        mContext = getApplicationContext();
        setContentView(R.layout.activity_login);
        initView();
        initListener();
    }

    private void initView() {
        usernameEdit = (AppCompatEditText) findViewById(R.id.usernameEdit);
        passwordEdit = (AppCompatEditText) findViewById(R.id.passwordEdit);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnSignup = (Button) findViewById(R.id.btnSignup);
    }

    private void initListener() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (usernameEdit.getText().toString().length() == 0 &&
                        passwordEdit.getText().toString().length() == 0) {
                    Toast.makeText(mContext, "Username and password is cannot be blank",
                            Toast.LENGTH_SHORT).show();
                } else if (usernameEdit.getText().toString().length() == 0) {
                    Toast.makeText(mContext, "Username cannot be blank",
                            Toast.LENGTH_SHORT).show();
                } else if (passwordEdit.getText().toString().length() == 0) {
                    Toast.makeText(mContext, "Password cannot be blank",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (Tools.isNetworkAvailable(mContext)) {
                        Tools.hideKeyboard(activity);
                        progressDialog = new ProgressDialog(activity);
                        progressDialog.setMessage("Authenticating user");
                        progressDialog.show();
                        userLogin();
                    } else {
                        Toast.makeText(mContext, "Please check your internet connection",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    private void userLogin() {
        String username = usernameEdit.getText().toString();
        String password = passwordEdit.getText().toString();

        AuthClass authClass = new AuthClass(Support.getAuthInstance()) {
            @Override
            public void onSuccess(String t) {
                progressDialog.setMessage("Authentication success");
                UpdateParamsM.updateToken(new SharedPrefM(mContext).getString(Config.FIREBASE_TOKEN));
                getData();
            }

            @Override
            public void onFail(String e) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(mContext, e, Toast.LENGTH_SHORT).show();
            }
        };
        authClass.performLogin(username, password);
    }

    private void getData(){
        GetParent getParent = new GetParent(Support.getUserReference().child(Support.getUserInstance().getUid())) {
            @Override
            public void onDataSuccess(DataSnapshot dataSnapshot) {
                UserM userM = dataSnapshot.getValue(UserM.class);
                if (userM != null) {
                    Support.id = Support.getUserInstance().getUid();
                    Support.userM = userM;
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    if (userM.getUserType().equals(Config._ADMIN)) {
                        Support.getAuthInstance().signOut();
                        Toast.makeText(mContext, "User invalid use another account",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(LoginActivity.this, UserActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Support.getAuthInstance().signOut();
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(mContext, "User global fetch error try again",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDataCancelled(DatabaseError databaseError) {
                Support.getAuthInstance().signOut();
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(mContext, databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        };
        getParent.addSingleListener();
    }
}
