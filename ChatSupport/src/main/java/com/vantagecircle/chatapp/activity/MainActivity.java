package com.vantagecircle.chatapp.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.vantagecircle.chatapp.R;
import com.vantagecircle.chatapp.Support;
import com.vantagecircle.chatapp.adapter.ClickListener;
import com.vantagecircle.chatapp.adapter.ContactsAdapter;
import com.vantagecircle.chatapp.model.ContactsM;
import com.vantagecircle.chatapp.model.UserM;
import com.vantagecircle.chatapp.utils.Tools;
import com.vantagecircle.chatapp.widget.customview.DividerItemDecoration;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    Activity activity;
    Context mContext;
    Toolbar mToolbar;
    ActionBar mActionBar;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    LinearLayout data_layout, no_data_layout;
    ArrayList<ContactsM> contactsMs = null;
    ContactsAdapter contactsAdapter;
    ProgressDialog progressDialog;
    Button btnTry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        activity = this;
        initToolbar();
        initView();
        initRecycler();
        initListener();
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Please wait getting users");
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
        Support.getUserReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<ContactsM> arrayList = new ArrayList<ContactsM>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserM userM = snapshot.getValue(UserM.class);
                    if (userM != null) {
                        if (!userM.getUserId().equals(Support.id)) {
                            ContactsM contactsM = new ContactsM();
                            contactsM.setUserId(userM.getUserId());
                            contactsM.setFcmToken(userM.getFcmToken());
                            contactsM.setFullName(userM.getFullName());
                            contactsM.setUsername(userM.getUsername());
                            arrayList.add(contactsM);
                        }
                    }
                }
                if (contactsMs == null) {
                    contactsMs = new ArrayList<>();
                    contactsMs.addAll(arrayList);
                    setupData();
                } else {
                    contactsMs.clear();
                    contactsMs.addAll(arrayList);
                    contactsAdapter.notifyItemInserted(contactsMs.size());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                no_data_layout.setVisibility(View.VISIBLE);
                data_layout.setVisibility(View.GONE);
                Log.e(TAG, "Database error " + databaseError.getMessage());
            }
        });
    }

    private void setupData() {
        if (contactsMs != null && contactsMs.size() > 0) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            no_data_layout.setVisibility(View.GONE);
            data_layout.setVisibility(View.VISIBLE);
            contactsAdapter = new ContactsAdapter(mContext, contactsMs, this);
            recyclerView.setAdapter(contactsAdapter);
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
        switch (item.getItemId()){
            case R.id.action_logout:
                Support.getAuthInstance().signOut();
                Intent intent = new Intent(activity, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int position) {
        ContactsM contactsM = contactsMs.get(position);
        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra("data", new Gson().toJson(contactsM));
        startActivity(intent);
    }
}
