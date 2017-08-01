package com.vantagecircle.chatapp.core;

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
import com.vantagecircle.chatapp.core.interfacep.ChildInterface;
import com.vantagecircle.chatapp.core.interfacep.ResultInterface;
import com.vantagecircle.chatapp.holder.ChatMViewHolder;
import com.vantagecircle.chatapp.core.interfacep.ValueInterface;
import com.vantagecircle.chatapp.model.ChatM;
import com.vantagecircle.chatapp.model.GroupM;
import com.vantagecircle.chatapp.model.NotificationM;
import com.vantagecircle.chatapp.model.UserM;
import com.vantagecircle.chatapp.services.SendNotification;
import com.vantagecircle.chatapp.utils.ConfigUtils;
import com.vantagecircle.chatapp.utils.Constant;
import com.vantagecircle.chatapp.utils.Tools;
import com.vantagecircle.chatapp.utils.UpdateParamsM;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by bapidas on 27/07/17.
 */

public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();
    private ActionBar mActionBar;
    private Toolbar mToolbar;
    private Context mContext;
    private UserM userM;
    private GroupM groupM;
    private EditText et_message;
    private ImageButton btn_send_txt;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private String currentRoom, fileName;
    private File decodeFile;
    private boolean isGroup, isFromNotification;
    private ArrayList<String> tokens;
    private ChatMAdapter chatMAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(loadView());
        mContext = getApplicationContext();
        initToolBar();
        initView();
        initRecycler();
        initListener();
    }

    protected abstract int loadView();

    protected void initToolBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        assert mActionBar != null;
        mActionBar.setDisplayHomeAsUpEnabled(true);
    }

    protected void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_chat);
        et_message = (EditText) findViewById(R.id.et_message);
        btn_send_txt = (ImageButton) findViewById(R.id.btn_send_txt);
    }

    protected void initRecycler() {
        linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.scrollToPosition(0);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    protected void initialize() {
        isFromNotification = getIntent().getBooleanExtra("isFromBar", false);
        isGroup = getIntent().getBooleanExtra("isGroup", false);
        if (isGroup) {
            groupM = new Gson().fromJson(getIntent().getStringExtra("data"), GroupM.class);
            mActionBar.setTitle(groupM.getName());
            getTokens();
        } else {
            userM = new Gson().fromJson(getIntent().getStringExtra("data"), UserM.class);
            mActionBar.setTitle(userM.getFullName());
            if (isFromNotification) {
                UpdateParamsM.updateOnlineStatus(true);
                UpdateParamsM.updateLastSeen(new Date().getTime());
            }
            getOnlineStatus();
        }
        getChatHistory();
    }

    protected void getChatHistory() {
        if(isGroup){
            ConfigUtils.getRoom(groupM, null, new ResultInterface() {
                @Override
                public void onSuccess(String t) {
                    chatMAdapter = new ChatMAdapter(ChatM.class, 0,
                            ChatMViewHolder.class,
                            Support.getChatReference().child(t));
                    recyclerView.setAdapter(chatMAdapter);
                }

                @Override
                public void onFail(String e) {
                    Log.d(TAG, "Error " + e);
                }
            });
        } else {
            ConfigUtils.getRoom(null, userM, new ResultInterface() {
                @Override
                public void onSuccess(String t) {
                    chatMAdapter = new ChatMAdapter(ChatM.class, 0,
                            ChatMViewHolder.class,
                            Support.getChatReference().child(t));
                    recyclerView.setAdapter(chatMAdapter);
                }

                @Override
                public void onFail(String e) {
                    Log.d(TAG, "Error " + e);
                }
            });
        }
    }

    protected void getOnlineStatus() {
        GetDataHandler getDataHandler = new GetDataHandler();
        getDataHandler.setDataReference(Support.getUserReference().child(userM.getUserId()));
        getDataHandler.setValueEventListener(new ValueInterface() {
            @Override
            public void onDataSuccess(DataModel dataModel) {
                UserM model = dataModel.getDataSnapshot().getValue(UserM.class);
                if (model != null) {
                    String statusString = model.isOnlineString();
                    mActionBar.setSubtitle(statusString);
                }
            }

            @Override
            public void onDataCancelled(DataModel databaseError) {
                Log.d(TAG, "Error " + databaseError.getDatabaseError().getMessage());
                mActionBar.setSubtitle("Offline");
            }
        });
    }

    protected void getTokens() {
        tokens = new ArrayList<>();
        GetDataHandler getDataHandler = new GetDataHandler();
        getDataHandler.setDataReference(Support.getGroupReference()
                .child(groupM.getId())
                .child("users"));
        getDataHandler.setChildValueListener(new ChildInterface() {
            @Override
            public void onChildNew(DataModel dataModel) {
                tokens.add(dataModel.getDataSnapshot().child("fcmToken").getValue().toString());
            }

            @Override
            public void onChildModified(DataModel dataModel) {

            }

            @Override
            public void onChildDelete(DataModel dataModel) {

            }

            @Override
            public void onChildRelocate(DataModel dataModel) {

            }

            @Override
            public void onChildCancelled(DataModel dataModel) {

            }
        });
    }

    protected void initListener() {
        btn_send_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_message.getText().toString().length() > 0) {
                    sendMessage(prepareChatModel(et_message.getText().toString(), Constant.TEXT_TYPE, null));
                } else {
                    Toast.makeText(mContext, "Type some message", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    protected ChatM prepareChatModel(String text, String type, String uri) {
        ChatM chatM = null;
        try {
            String senderName = Support.userM.getFullName();
            String senderUid = Support.id;
            String receiverName;
            String receiverUid;
            if (isGroup) {
                receiverName = groupM.getName();
                receiverUid = groupM.getId();
            } else {
                receiverName = userM.getFullName();
                receiverUid = userM.getUserId();
            }
            long timeStamp = System.currentTimeMillis();
            chatM = new ChatM(senderName, receiverName, senderUid, receiverUid,
                    type, text, uri, timeStamp, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatM;
    }

    protected void uploadDataTask(Uri fileUri, ChatM chatM) {
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
                    // Handle progress uploads
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    Log.d(TAG, "Upload is " + progress + "% done");
                }
            }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                    // Handle pause uploads
                    Log.d(TAG, "Upload is paused");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Log.d(TAG, "Upload is failed");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Handle successful uploads on complete
                    String downloadUrl = taskSnapshot.getDownloadUrl().toString();
                    long timeStamp = Long.parseLong(taskSnapshot.getStorage().getName());
                    UpdateParamsM.updateFileUrl(currentRoom, timeStamp, downloadUrl);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void sendMessage(final ChatM chatM) {
        if(isGroup){
            ConfigUtils.getRoom(groupM, null, new ResultInterface() {
                @Override
                public void onSuccess(String t) {
                    currentRoom = t;
                    SetDataHandler setDataHandler = new SetDataHandler();
                    setDataHandler.setDatabaseReference(Support.getChatReference()
                            .child(currentRoom)
                            .child(String.valueOf(chatM.getTimeStamp())));
                    setDataHandler.insertData(chatM, new ResultInterface() {
                        @Override
                        public void onSuccess(String t) {
                            et_message.setText("");
                            if (chatMAdapter != null) {
                                recyclerView.smoothScrollToPosition(chatMAdapter.getItemCount());
                            }
                            sendPushNotification(chatM, currentRoom);
                        }

                        @Override
                        public void onFail(String e) {
                            Log.d(TAG, "Error " + e);
                        }
                    });
                }

                @Override
                public void onFail(String e) {
                    Log.d(TAG, "Error " + e);
                }
            });
        } else {
            ConfigUtils.getRoom(null, userM, new ResultInterface() {
                @Override
                public void onSuccess(String t) {
                    currentRoom = t;
                    SetDataHandler setDataHandler = new SetDataHandler();
                    setDataHandler.setDatabaseReference(Support.getChatReference()
                            .child(currentRoom)
                            .child(String.valueOf(chatM.getTimeStamp())));
                    setDataHandler.insertData(chatM, new ResultInterface() {
                        @Override
                        public void onSuccess(String t) {
                            et_message.setText("");
                            if (chatMAdapter != null) {
                                recyclerView.smoothScrollToPosition(chatMAdapter.getItemCount());
                            }
                            sendPushNotification(chatM, currentRoom);
                        }

                        @Override
                        public void onFail(String e) {
                            Log.d(TAG, "Error " + e);
                        }
                    });
                }

                @Override
                public void onFail(String e) {
                    Log.d(TAG, "Error " + e);
                }
            });
        }
    }

    protected void sendPushNotification(ChatM chatM, String chat_room) {
        try {
            String senderUsername = chatM.getSenderName();
            String senderUserId = chatM.getSenderUid();
            String senderFcmToken = Support.userM.getFcmToken();
            String chatType = chatM.getChatType();
            String messageText = chatM.getMessageText();
            String fileUrl = chatM.getFileUrl();
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

            if (isGroup) {
                notificationM.setReceiverFcmToken(null);
                SendNotification sendNotification = new SendNotification(notificationM);
                sendNotification.sendToGroup();
            } else {
                notificationM.setReceiverFcmToken(userM.getFcmToken());
                SendNotification sendNotification = new SendNotification(notificationM);
                sendNotification.sendToSingle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    protected void showContextMenu(View view) {
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
                    ActivityCompat.requestPermissions(this, PERMISSIONS, Constant.REQUEST_STORAGE_PERMISSION);
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("*/*");
                    startActivityForResult(intent, Constant.REQUEST_CODE_GALLERY);
                }
            } else {
                fileName = null;
                decodeFile = null;
                String[] PERMISSIONS = {Manifest.permission.CAMERA};
                if (!Tools.isHasPermissions(this, PERMISSIONS)) {
                    ActivityCompat.requestPermissions(this, PERMISSIONS, Constant.REQUEST_CAMERA_PERMISSION);
                } else {
                    Calendar c = Calendar.getInstance();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    fileName = "image_" + c.getTimeInMillis() + ".jpg";
                    decodeFile = new File(path, fileName);
                    Uri tempUri = Uri.fromFile(decodeFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                    startActivityForResult(intent, Constant.REQUEST_CODE_CAMERA);
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
            case Constant.REQUEST_STORAGE_PERMISSION:
                try {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("*/*");
                        startActivityForResult(intent, Constant.REQUEST_CODE_GALLERY);
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
            case Constant.REQUEST_CAMERA_PERMISSION:
                try {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Calendar c = Calendar.getInstance();
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                        fileName = "image_" + c.getTimeInMillis() + ".jpg";
                        decodeFile = new File(path, fileName);
                        Uri tempUri = Uri.fromFile(decodeFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                        startActivityForResult(intent, Constant.REQUEST_CODE_CAMERA);
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
            case Constant.REQUEST_CODE_CAMERA:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        try {
                            if (decodeFile != null && decodeFile.exists()) {
                                Uri selectedImage = Uri.fromFile(decodeFile);
                                String mimeType = getContentResolver().getType(selectedImage);
                                if (mimeType == null) {
                                    Toast.makeText(mContext, "There was an error in file",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    ChatM chatM = prepareChatModel(null, Constant.IMAGE_TYPE,
                                            selectedImage.toString());
                                    sendMessage(chatM);
                                    uploadDataTask(selectedImage, chatM);
                                }
                            } else {
                                Toast.makeText(mContext, "File is not exist",
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
            case Constant.REQUEST_CODE_GALLERY:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        try {
                            if (data != null && data.getData() != null) {
                                Uri selectedImage = data.getData();
                                String mimeType = getContentResolver().getType(selectedImage);
                                if (mimeType == null) {
                                    Toast.makeText(mContext, "There was an error in file",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    ChatM chatM = prepareChatModel(null, Constant.IMAGE_TYPE,
                                            selectedImage.toString());
                                    sendMessage(chatM);
                                    uploadDataTask(selectedImage, chatM);
                                }
                            } else {
                                Toast.makeText(mContext, "File is not exist",
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
        if(isFromNotification){
            UpdateParamsM.updateOnlineStatus(false);
            UpdateParamsM.updateLastSeen(new Date().getTime());
        }
    }
}