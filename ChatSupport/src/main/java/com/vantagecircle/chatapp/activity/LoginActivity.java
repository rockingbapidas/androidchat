package com.vantagecircle.chatapp.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vantagecircle.chatapp.R;
import com.vantagecircle.chatapp.services.SupportService;
import com.vantagecircle.chatapp.core.model.DataModel;
import com.vantagecircle.chatapp.core.GetDataHandler;
import com.vantagecircle.chatapp.core.interfaceC.ResultInterface;
import com.vantagecircle.chatapp.core.interfaceC.ValueInterface;
import com.vantagecircle.chatapp.utils.Constants;
import com.vantagecircle.chatapp.core.AuthHandler;
import com.vantagecircle.chatapp.utils.UpdateKeyUtils;
import com.vantagecircle.chatapp.model.UserM;
import com.vantagecircle.chatapp.pref.SharedPrefM;
import com.vantagecircle.chatapp.utils.ToolsUtils;

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
                    if (ToolsUtils.isNetworkAvailable(mContext)) {
                        ToolsUtils.hideKeyboard(activity);
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

        AuthHandler authHandler = new AuthHandler();
        authHandler.setFirebaseAuth(SupportService.getAuthInstance());
        authHandler.performLogin(username, password, new ResultInterface() {
            @Override
            public void onSuccess(String t) {
                progressDialog.setMessage("Authentication success");
                UpdateKeyUtils.updateTokenToServer(new SharedPrefM(mContext).getString(Constants.FIREBASE_TOKEN));
                getData();
            }

            @Override
            public void onFail(String e) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(mContext, e, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getData(){
        GetDataHandler getDataHandler = new GetDataHandler();
        getDataHandler.setDataReference(SupportService.getUserReference().child(SupportService.getUserInstance().getUid()));
        getDataHandler.setSingleValueEventListener(new ValueInterface() {
            @Override
            public void onDataSuccess(DataModel dataModel) {
                UserM userM = dataModel.getDataSnapshot().getValue(UserM.class);
                if (userM != null) {
                    SupportService.init(getApplicationContext(), userM);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    if (userM.getUserType().equals(Constants._ADMIN)) {
                        SupportService.getAuthInstance().signOut();
                        Toast.makeText(mContext, "User invalid use another account",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(LoginActivity.this, UserActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    SupportService.getAuthInstance().signOut();
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(mContext, "User data fetch error try again",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDataCancelled(DataModel databaseError) {
                SupportService.getAuthInstance().signOut();
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(mContext, databaseError.getDatabaseError().getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
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
