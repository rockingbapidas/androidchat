package com.vantagecircle.chatapp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.Query;
import com.google.gson.Gson;
import com.vantagecircle.chatapp.R;
import com.vantagecircle.chatapp.Support;
import com.vantagecircle.chatapp.adapter.GroupMAdapter;
import com.vantagecircle.chatapp.core.interfacep.ResultInterface;
import com.vantagecircle.chatapp.holder.GroupMViewHolder;
import com.vantagecircle.chatapp.holder.UserMViewHolder;
import com.vantagecircle.chatapp.adapter.UsersMAdapter;
import com.vantagecircle.chatapp.core.SetDataHandler;
import com.vantagecircle.chatapp.utils.UpdateParamsM;
import com.vantagecircle.chatapp.interfacePref.ClickGroup;
import com.vantagecircle.chatapp.interfacePref.ClickUser;
import com.vantagecircle.chatapp.model.GroupM;
import com.vantagecircle.chatapp.model.UserM;
import com.vantagecircle.chatapp.services.SendNotification;
import com.vantagecircle.chatapp.widget.customview.DividerItemDecoration;

import java.util.Date;

public class UserActivity extends AppCompatActivity implements ClickUser, ClickGroup {
    private static final String TAG = UserActivity.class.getSimpleName();
    private android.app.Activity activity;
    private Context mContext;
    private Toolbar mToolbar;
    private ActionBar mActionBar;
    private RecyclerView recyclerView;
    private RecyclerView recyclerView1;
    private LinearLayout groupTitle;
    private LinearLayoutManager linearLayoutManager;
    private LinearLayoutManager linearLayoutManager1;
    private UsersMAdapter usersMAdapter;
    private GroupMAdapter groupMAdapter;
    private ProgressDialog progressDialog;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        mContext = getApplicationContext();
        activity = this;
        initToolbar();
        initView();
        initRecycler();
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
        recyclerView1 = (RecyclerView) findViewById(R.id.recyclerView1);
        groupTitle = (LinearLayout) findViewById(R.id.groupTitle);
    }

    private void initRecycler() {
        linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.scrollToPosition(0);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(ContextCompat
                .getDrawable(mContext, R.drawable.divider)));

        linearLayoutManager1 = new LinearLayoutManager(mContext);
        linearLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView1.setLayoutManager(linearLayoutManager1);
        recyclerView1.scrollToPosition(0);
        recyclerView1.setItemAnimator(new DefaultItemAnimator());
        recyclerView1.addItemDecoration(new DividerItemDecoration(ContextCompat
                .getDrawable(mContext, R.drawable.divider)));

        Query myQuery =  Support.getUserReference();
        usersMAdapter = new UsersMAdapter(UserM.class, R.layout.row_users, UserMViewHolder.class, myQuery, this);
        recyclerView.setAdapter(usersMAdapter);

        Query myQuery1 =  Support.getGroupReference();
        groupMAdapter = new GroupMAdapter(GroupM.class, R.layout.row_users, GroupMViewHolder.class, myQuery1, this);
        recyclerView1.setAdapter(groupMAdapter);
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
            case R.id.action_group:
                showDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View view = layoutInflater.inflate(R.layout.layout_create_group, null);
        final EditText editText = (EditText) view.findViewById(R.id.groupnameEdit);
        final Button btnSubmit = (Button) view.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(editText.getText().toString())) {
                    progressDialog = new ProgressDialog(activity);
                    progressDialog.setMessage("Please Wait");
                    progressDialog.show();

                    final String id = Support.getGroupReference().push().getKey();
                    final String name = editText.getText().toString();

                    GroupM groupM = new GroupM();
                    groupM.setId(id);
                    groupM.setName(name);

                    SetDataHandler setDataHandler = new SetDataHandler();
                    setDataHandler.setDatabaseReference(Support.getGroupReference().child(id));
                    setDataHandler.insertData(groupM, new ResultInterface() {
                        @Override
                        public void onSuccess(String t) {
                            alertDialog.dismiss();
                            progressDialog.dismiss();
                            addUsersToGroup(id);
                            final String roomName = id + "_" + name;
                            subscribeGroup(roomName);
                        }

                        @Override
                        public void onFail(String e) {
                            progressDialog.dismiss();
                            Toast.makeText(mContext, e, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(mContext, "Enter Group Name",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setCancelable(true);
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void addUsersToGroup(String id) {
        for (int i = 0; i < usersMAdapter.getItemCount(); i++) {
            UserM userM = usersMAdapter.getItem(i);
            SetDataHandler setDataHandler = new SetDataHandler();
            setDataHandler.setDatabaseReference(Support.getGroupReference().child(id)
                    .child("users").child(userM.getUserId()).child("fcmToken"));
            setDataHandler.insertData(userM.getFcmToken(), new ResultInterface() {
                @Override
                public void onSuccess(String t) {
                    Log.d(TAG, t);
                }

                @Override
                public void onFail(String e) {
                    Log.e(TAG, e);
                }
            });
        }
        alertDialog.dismiss();
        progressDialog.dismiss();
        Toast.makeText(mContext, "Group created successfully",
                Toast.LENGTH_SHORT).show();
    }

    private void subscribeGroup(String room){
        for (int i = 0; i < usersMAdapter.getItemCount(); i++) {
            UserM userM = usersMAdapter.getItem(i);
            new SendNotification(null).subscribeToken(userM.getFcmToken(), room);
        }
    }

    @Override
    public void onUserClick(int position) {
        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra("isFormBar", false);
        intent.putExtra("isGroup", false);
        intent.putExtra("data", new Gson().toJson(usersMAdapter.getItem(position)));
        startActivity(intent);
    }

    @Override
    public void onGroupClick(int position) {
        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra("isFormBar", false);
        intent.putExtra("isGroup", true);
        intent.putExtra("data", new Gson().toJson(groupMAdapter.getItem(position)));
        startActivity(intent);
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
        UpdateParamsM.updateOnlineStatus(true);
        UpdateParamsM.updateLastSeen(new Date().getTime());
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
        UpdateParamsM.updateOnlineStatus(false);
        UpdateParamsM.updateLastSeen(new Date().getTime());
    }
}
