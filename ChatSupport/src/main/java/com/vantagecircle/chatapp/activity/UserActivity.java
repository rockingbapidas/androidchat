package com.vantagecircle.chatapp.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
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
import android.widget.Toast;

import com.google.firebase.database.Query;
import com.google.gson.Gson;
import com.vantagecircle.chatapp.R;
import com.vantagecircle.chatapp.services.SupportService;
import com.vantagecircle.chatapp.adapter.GroupMAdapter;
import com.vantagecircle.chatapp.core.interfaceC.ResultInterface;
import com.vantagecircle.chatapp.holder.GroupMViewHolder;
import com.vantagecircle.chatapp.holder.UserMViewHolder;
import com.vantagecircle.chatapp.adapter.UsersMAdapter;
import com.vantagecircle.chatapp.core.SetDataHandler;
import com.vantagecircle.chatapp.utils.ConfigUtils;
import com.vantagecircle.chatapp.utils.Constants;
import com.vantagecircle.chatapp.utils.ToolsUtils;
import com.vantagecircle.chatapp.utils.UpdateKeyUtils;
import com.vantagecircle.chatapp.adapter.interfaceA.ClickGroup;
import com.vantagecircle.chatapp.adapter.interfaceA.ClickUser;
import com.vantagecircle.chatapp.model.GroupM;
import com.vantagecircle.chatapp.model.UserM;
import com.vantagecircle.chatapp.httpcall.SendNotification;
import com.vantagecircle.chatapp.widget.customview.DividerItemDecoration;

import java.util.Date;

public class UserActivity extends AppCompatActivity {
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
        initPermission();
        initToolbar();
        initView();
        initRecycler();
        setData();
    }

    private void initPermission() {
        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!ConfigUtils.isHasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, Constants.REQUEST_STORAGE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permission granted");
                } else {
                    Log.d(TAG, "Permission not granted");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        assert mActionBar != null;
        mActionBar.setTitle(SupportService.userM.getFullName());
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
        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        recyclerView.addItemDecoration(new DividerItemDecoration(ContextCompat
                .getDrawable(mContext, R.drawable.divider)));

        linearLayoutManager1 = new LinearLayoutManager(mContext);
        linearLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView1.setLayoutManager(linearLayoutManager1);
        recyclerView1.scrollToPosition(0);
        RecyclerView.ItemAnimator animator1 = recyclerView1.getItemAnimator();
        if (animator1 instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator1).setSupportsChangeAnimations(false);
        }
        recyclerView1.addItemDecoration(new DividerItemDecoration(ContextCompat
                .getDrawable(mContext, R.drawable.divider)));
    }

    private void setData(){
        Query myQuery =  SupportService.getUserReference();
        usersMAdapter = new UsersMAdapter(UserM.class, R.layout.row_users,
                UserMViewHolder.class, myQuery, new ClickUser() {
            @Override
            public void onUserClick(int position) {
                Intent intent = new Intent(activity, ChatActivity.class);
                intent.putExtra("isFormBar", false);
                intent.putExtra("isGroup", false);
                intent.putExtra("data", new Gson().toJson(usersMAdapter.getItem(position)));
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(usersMAdapter);

        Query myQuery1 =  SupportService.getGroupReference();
        groupMAdapter = new GroupMAdapter(GroupM.class, R.layout.row_users,
                GroupMViewHolder.class, myQuery1, new ClickGroup() {
            @Override
            public void onGroupClick(int position) {
                Intent intent = new Intent(activity, ChatActivity.class);
                intent.putExtra("isFormBar", false);
                intent.putExtra("isGroup", true);
                intent.putExtra("data", new Gson().toJson(groupMAdapter.getItem(position)));
                startActivity(intent);
            }
        });
        recyclerView1.setAdapter(groupMAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_logout) {
            SupportService.getAuthInstance().signOut();
            Intent intent = new Intent(activity, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        } else if (i == R.id.action_group) {
            showDialog();

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

                    final String id = SupportService.getGroupReference().push().getKey();
                    final String name = editText.getText().toString();

                    GroupM groupM = new GroupM();
                    groupM.setId(id);
                    groupM.setName(name);

                    SetDataHandler setDataHandler = new SetDataHandler();
                    setDataHandler.setDatabaseReference(SupportService.getGroupReference().child(id));
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
            setDataHandler.setDatabaseReference(SupportService.getGroupReference().child(id)
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
            new SendNotification().subscribeToken(userM.getFcmToken(), room);
        }
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
        UpdateKeyUtils.updateOnlineStatus(true);
        UpdateKeyUtils.updateLastSeen(new Date().getTime());
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
        UpdateKeyUtils.updateOnlineStatus(false);
        UpdateKeyUtils.updateLastSeen(new Date().getTime());
    }
}
