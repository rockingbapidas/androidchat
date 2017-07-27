package com.vantagecircle.chatapp.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.vantagecircle.chatapp.R;
import com.vantagecircle.chatapp.Support;
import com.vantagecircle.chatapp.adapter.ChatMAdapter;
import com.vantagecircle.chatapp.holder.ChatMViewHolder;
import com.vantagecircle.chatapp.core.DataClass;
import com.vantagecircle.chatapp.core.GetParent;
import com.vantagecircle.chatapp.utils.Config;
import com.vantagecircle.chatapp.utils.UpdateParamsM;
import com.vantagecircle.chatapp.model.ChatM;
import com.vantagecircle.chatapp.model.UserM;
import com.vantagecircle.chatapp.model.NotificationM;
import com.vantagecircle.chatapp.services.SendNotification;
import com.vantagecircle.chatapp.utils.DateUtils;
import com.vantagecircle.chatapp.utils.Tools;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by bapidas on 10/07/17.
 */

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = ChatActivity.class.getSimpleName();
    private ActionBar mActionBar;
    private Toolbar mToolbar;
    private Context mContext;
    private Activity activity;
    private UserM userM;
    private EditText et_message;
    private ImageButton btn_send_txt;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private String room_type_1, room_type_2, currentRoom;
    private String fileName;
    private File decodeFile;
    private ChatMAdapter chatMAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        activity = this;
        setContentView(R.layout.activity_chat);
        userM = new Gson().fromJson(getIntent().getStringExtra("global"), UserM.class);
        room_type_1 = Support.id + "_" + userM.getUserId();
        room_type_2 = userM.getUserId() + "_" + Support.id;
        if (getIntent().getBooleanExtra("isFromBar", false)) {
            UpdateParamsM.setOnlineStatus(true);
            UpdateParamsM.setLastSeen(new Date().getTime());
        }
        initToolbar();
        initView();
        initListener();
        initRecycler();
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        assert mActionBar != null;
        mActionBar.setTitle(userM.getFullName());
        mActionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_chat);
        et_message = (EditText) findViewById(R.id.et_message);
        btn_send_txt = (ImageButton) findViewById(R.id.btn_send_txt);
    }

    private void initRecycler() {
        linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.scrollToPosition(0);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void initListener() {
        btn_send_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_message.getText().toString().length() > 0) {
                    sendMessage(prepareModelForText());
                } else {
                    Toast.makeText(mContext, "Enter some message", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private ChatM prepareModelForText() {
        ChatM chatM = null;
        try {
            String senderName = Support.userM.getFullName();
            String receiverName = userM.getFullName();
            String senderUid = Support.id;
            String receiverUid = userM.getUserId();
            String messageText = et_message.getText().toString();
            long timeStamp = System.currentTimeMillis();
            String chatType = "text";
            String fileUri = null;
            boolean isSent = false;
            boolean isRead = false;
            chatM = new ChatM(senderName, receiverName, senderUid, receiverUid,
                    chatType, messageText, fileUri, timeStamp, isSent, isRead);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatM;
    }

    private ChatM prepareModelForFile(String uri) {
        ChatM chatM = null;
        try {
            String senderName = Support.userM.getFullName();
            String receiverName = userM.getFullName();
            String senderUid = Support.id;
            String receiverUid = userM.getUserId();
            String chatType = "image";
            String messageText = null;
            String fileUri = uri;
            long timeStamp = System.currentTimeMillis();
            boolean isSent = false;
            boolean isRead = false;
            chatM = new ChatM(senderName, receiverName, senderUid, receiverUid,
                    chatType, messageText, fileUri, timeStamp, isSent, isRead);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatM;
    }

    private void uploadDataTask(Uri fileUri, ChatM chatM) {
        try {
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image/jpeg")
                    .build();
            UploadTask uploadTask = Support.getChatImageReference()
                    .child(String.valueOf(chatM.getTimeStamp()))
                    .putFile(fileUri, metadata);

            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    Log.d(TAG, "Upload is " + progress + "% done");
                }
            }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG, "Upload is paused");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Handle successful uploads on complete
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    long timeStamp = Long.parseLong(taskSnapshot.getStorage().getName());
                    UpdateParamsM.updateFileUrl(currentRoom, timeStamp, downloadUrl.toString());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(final ChatM chatM) {
        GetParent getParent = new GetParent(Support.getChatReference()) {
            @Override
            protected void onDataSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(room_type_2)) {
                    DataClass dataClass = new DataClass(Support.getChatReference()
                            .child(room_type_2)
                            .child(String.valueOf(chatM.getTimeStamp()))) {
                        @Override
                        protected void onSuccess(String t) {
                            et_message.setText("");
                            if(chatMAdapter != null){
                                recyclerView.smoothScrollToPosition(chatMAdapter.getItemCount());
                            }
                            currentRoom = room_type_2;
                            sendPushNotification(chatM, room_type_2);
                        }

                        @Override
                        protected void onFail(String e) {

                        }
                    };
                    dataClass.insertData(chatM);
                } else {
                    DataClass dataClass = new DataClass(Support.getChatReference()
                            .child(room_type_1)
                            .child(String.valueOf(chatM.getTimeStamp()))) {
                        @Override
                        protected void onSuccess(String t) {
                            et_message.setText("");
                            if(chatMAdapter != null){
                                recyclerView.smoothScrollToPosition(chatMAdapter.getItemCount());
                            }
                            currentRoom = room_type_1;
                            sendPushNotification(chatM, room_type_1);
                        }

                        @Override
                        protected void onFail(String e) {

                        }
                    };
                    dataClass.insertData(chatM);
                }
            }

            @Override
            protected void onDataCancelled(DatabaseError databaseError) {

            }
        };
        getParent.addSingleListener();
    }

    private void sendPushNotification(ChatM chatM, String chat_room) {
        try {
            String senderUsername = chatM.getSenderName();
            String senderUserId = chatM.getSenderUid();
            String senderFcmToken = Support.userM.getFcmToken();
            String chatType = chatM.getChatType();
            String messageText = chatM.getMessageText();
            String fileUrl = chatM.getFileUrl();
            String receiverFcmToken = userM.getFcmToken();
            long timeStamp = chatM.getTimeStamp();

            NotificationM notificationM = new NotificationM();
            notificationM.setTitle(senderUsername);
            notificationM.setChatType(chatType);
            notificationM.setMessageText(messageText);
            notificationM.setFileUrl(fileUrl);
            notificationM.setSenderUsername(senderUsername);
            notificationM.setSenderUid(senderUserId);
            notificationM.setSenderFcmToken(senderFcmToken);
            notificationM.setTimeStamp(timeStamp);
            notificationM.setChatRoom(chat_room);
            notificationM.setReceiverFcmToken(receiverFcmToken);

            SendNotification sendNotification = new SendNotification(notificationM);
            sendNotification.sendForSingle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getChatHistory() {
        GetParent getParent = new GetParent(Support.getChatReference()) {
            @Override
            protected void onDataSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(room_type_1)) {
                    chatMAdapter = new ChatMAdapter(ChatM.class, 0, ChatMViewHolder.class,
                            Support.getChatReference().child(room_type_1));
                    recyclerView.setAdapter(chatMAdapter);
                } else if (dataSnapshot.hasChild(room_type_2)) {
                    chatMAdapter = new ChatMAdapter(ChatM.class, 0, ChatMViewHolder.class,
                            Support.getChatReference().child(room_type_2));
                    recyclerView.setAdapter(chatMAdapter);
                } else {
                    Log.e(TAG, "No Chat room available yet");
                }
            }

            @Override
            protected void onDataCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }
        };
        getParent.addContinueListener();
    }

    private void getOnlineStatus() {
        GetParent getParent = new GetParent(Support.getUserReference().child(userM.getUserId())) {
            @Override
            protected void onDataSuccess(DataSnapshot dataSnapshot) {
                UserM model = dataSnapshot.getValue(UserM.class);
                if (model != null) {
                    if (model.isOnline()) {
                        mActionBar.setSubtitle("Online");
                        Log.e(TAG, "User Online");
                    } else {
                        mActionBar.setSubtitle("Last seen on " +
                                DateUtils.getTime(model.getLastSeenTime()));
                        Log.e(TAG, "User Offline");
                    }
                }
            }

            @Override
            protected void onDataCancelled(DatabaseError databaseError) {

            }
        };
        getParent.addContinueListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.attach_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_attach:
                showContextMenu(getCurrentFocus());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, 1, 0, "Choose from gallery");
        menu.add(0, 2, 1, "Take picture");
    }

    private void showContextMenu(View view) {
        this.registerForContextMenu(view);
        this.openContextMenu(view);
        this.unregisterForContextMenu(view);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        try {
            if (item.getItemId() == 1) {
                fileName = null;
                decodeFile = null;
                String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                if (!Tools.isHasPermissions(this, PERMISSIONS)) {
                    ActivityCompat.requestPermissions(this, PERMISSIONS, Config.REQUEST_STORAGE_PERMISSION);
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("*/*");
                    startActivityForResult(intent, Config.REQUEST_CODE_GALLERY);
                }
            } else {
                fileName = null;
                decodeFile = null;
                String[] PERMISSIONS = {Manifest.permission.CAMERA};
                if (!Tools.isHasPermissions(this, PERMISSIONS)) {
                    ActivityCompat.requestPermissions(this, PERMISSIONS, Config.REQUEST_CAMERA_PERMISSION);
                } else {
                    Calendar c = Calendar.getInstance();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    fileName = "image_" + c.getTimeInMillis() + ".jpg";
                    decodeFile = new File(path, fileName);
                    Uri tempUri = Uri.fromFile(decodeFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                    startActivityForResult(intent, Config.REQUEST_CODE_CAMERA);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Config.REQUEST_STORAGE_PERMISSION:
                try {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("*/*");
                        startActivityForResult(intent, Config.REQUEST_CODE_GALLERY);
                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                            Toast.makeText(mContext, "Gallery cannot be opened without this permission",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case Config.REQUEST_CAMERA_PERMISSION:
                try {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Calendar c = Calendar.getInstance();
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                        fileName = "image_" + c.getTimeInMillis() + ".jpg";
                        decodeFile = new File(path, fileName);
                        Uri tempUri = Uri.fromFile(decodeFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                        startActivityForResult(intent, Config.REQUEST_CODE_CAMERA);
                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                            Toast.makeText(mContext, "Camera cannot be opened without this permission",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Config.REQUEST_CODE_CAMERA:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        try {
                            if (decodeFile != null && decodeFile.exists()) {
                                Uri selectedImage = Uri.fromFile(decodeFile);
                                String mimeType = getContentResolver().getType(selectedImage);
                                if (mimeType == null) {
                                    Toast.makeText(mContext, "There was an error saving the file",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    ChatM chatM = prepareModelForFile(null);
                                    sendMessage(chatM);
                                    uploadDataTask(selectedImage, chatM);
                                }
                            } else {
                                Toast.makeText(mContext, "There was an error saving the file",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(mContext, "User cancelled operation",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
            case Config.REQUEST_CODE_GALLERY:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        try {
                            if (data != null && data.getData() != null) {
                                Uri selectedImage = data.getData();
                                String mimeType = getContentResolver().getType(selectedImage);
                                if (mimeType == null) {
                                    Toast.makeText(mContext, "There was an error saving the file",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    ChatM chatM = prepareModelForFile(null);
                                    sendMessage(chatM);
                                    uploadDataTask(selectedImage, chatM);
                                }
                            } else {
                                Toast.makeText(mContext, "There was an error saving the file",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(this, "User cancelled operation",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        Support.setIsChatWindowActive(true);
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        Support.setIsChatWindowActive(false);
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
        getOnlineStatus();
        getChatHistory();
    }
}
