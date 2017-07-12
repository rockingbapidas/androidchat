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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.vantagecircle.chatapp.R;
import com.vantagecircle.chatapp.Support;
import com.vantagecircle.chatapp.data.Config;
import com.vantagecircle.chatapp.data.ConstantM;
import com.vantagecircle.chatapp.model.UserM;
import com.vantagecircle.chatapp.utils.SharedPrefM;
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
                    Tools.hideKeyboard(activity);
                    progressDialog = new ProgressDialog(activity);
                    progressDialog.setMessage("Authenticating user");
                    progressDialog.show();
                    userLogin();
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
        try {
            String username = usernameEdit.getText().toString();
            String password = passwordEdit.getText().toString();

            Support.getAuthInstance().signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressDialog.setMessage("Authentication success");
                                ConstantM.updateToken(new SharedPrefM(mContext)
                                        .getString(Config.FIREBASE_TOKEN));
                                setupData();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
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
            Support.getUserReference().child(Support.getUserInstance().getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            UserM userM = dataSnapshot.getValue(UserM.class);
                            if (userM != null) {
                                Support.id = Support.getUserInstance().getUid();
                                Support.userM = userM;
                                if (progressDialog != null && progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                //logout from firebase and try again
                                Support.getAuthInstance().signOut();
                                if (progressDialog != null && progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                                Toast.makeText(mContext, "User data fetch error try again",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //logout from firebase and try again
                            Support.getAuthInstance().signOut();
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Toast.makeText(mContext, databaseError.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
