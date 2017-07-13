package com.vantagecircle.chatapp.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.vantagecircle.chatapp.R;
import com.vantagecircle.chatapp.Support;
import com.vantagecircle.chatapp.adapter.ClickListener;
import com.vantagecircle.chatapp.adapter.UsersAdapter;
import com.vantagecircle.chatapp.data.ConstantM;
import com.vantagecircle.chatapp.model.UserM;
import com.vantagecircle.chatapp.widget.customview.DividerItemDecoration;

import java.util.ArrayList;

/**
 * Created by bapidas on 13/07/17.
 */

public class AdminActivity extends AppCompatActivity implements ClickListener {
    private static final String TAG = AdminActivity.class.getSimpleName();
    Activity activity;
    Context mContext;
    Toolbar mToolbar;
    ActionBar mActionBar;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    LinearLayout data_layout, no_data_layout;
    ArrayList<UserM> userMs = null;
    UsersAdapter usersAdapter;
    ProgressDialog progressDialog;
    Button btnTry;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        mContext = getApplicationContext();
        activity = this;
        initToolbar();
        initView();
        initRecycler();
        initListener();
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Please wait");
        progressDialog.show();
        initData();
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        assert mActionBar != null;
        mActionBar.setTitle(Support.userM.getFullName());
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        no_data_layout = (LinearLayout) findViewById(R.id.no_data_layout);
        data_layout = (LinearLayout) findViewById(R.id.data_layout);
        btnTry = (Button) findViewById(R.id.btnTry);
    }

    private void initRecycler() {
        linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.scrollToPosition(0);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(ContextCompat
                .getDrawable(mContext, R.drawable.divider)));
    }

    private void initListener() {
        btnTry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(activity);
                progressDialog.setMessage("Please wait getting users");
                progressDialog.show();
                initData();
            }
        });
    }

    private void initData() {
        Support.getUserReference().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                UserM userM = dataSnapshot.getValue(UserM.class);
                if (userM != null) {
                    if (!userM.getLastMessage().equals("") && userM.getLastMessage().length() > 0) {
                        if (userMs == null) {
                            userMs = new ArrayList<>();
                            userMs.add(userM);
                            setupData();
                        } else {
                            userMs.add(userM);
                            usersAdapter.notifyItemInserted(userMs.size());
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                UserM userM = dataSnapshot.getValue(UserM.class);
                if (userM != null) {
                    if (!userM.getLastMessage().equals("") && userM.getLastMessage().length() > 0) {
                        if (userMs != null) {
                            if(userMs.contains(userM)) {
                                int i = userMs.indexOf(userM);
                                usersAdapter.updateLastMessage(i, userM.getLastMessage());
                            } else {
                                userMs.add(userM);
                                usersAdapter.notifyItemInserted(userMs.size());
                            }
                        } else {
                            userMs = new ArrayList<>();
                            userMs.add(userM);
                            setupData();
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                /*if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                no_data_layout.setVisibility(View.VISIBLE);
                data_layout.setVisibility(View.GONE);
                Log.e(TAG, "Database error " + databaseError.getMessage());*/
            }
        });
    }

    private void setupData() {
        if (userMs != null && userMs.size() > 0) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            no_data_layout.setVisibility(View.GONE);
            data_layout.setVisibility(View.VISIBLE);

            usersAdapter = new UsersAdapter(mContext, userMs, this);
            recyclerView.setAdapter(usersAdapter);
        } else {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            no_data_layout.setVisibility(View.VISIBLE);
            data_layout.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                Support.getAuthInstance().signOut();
                Intent intent = new Intent(activity, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int position) {
        UserM userM = userMs.get(position);
        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra("data", new Gson().toJson(userM));
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ConstantM.setOnlineStatus(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ConstantM.setLastSeen(System.currentTimeMillis());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
