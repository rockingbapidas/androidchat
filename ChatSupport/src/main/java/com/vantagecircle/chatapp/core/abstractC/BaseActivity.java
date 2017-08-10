package com.vantagecircle.chatapp.core.abstractC;

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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.vantagecircle.chatapp.R;
import com.vantagecircle.chatapp.services.SupportService;
import com.vantagecircle.chatapp.adapter.ChatMAdapter;
import com.vantagecircle.chatapp.core.model.DataModel;
import com.vantagecircle.chatapp.core.GetDataHandler;
import com.vantagecircle.chatapp.core.SetDataHandler;
import com.vantagecircle.chatapp.core.interfaceC.ChildInterface;
import com.vantagecircle.chatapp.core.interfaceC.ResultInterface;
import com.vantagecircle.chatapp.holder.ChatMViewHolder;
import com.vantagecircle.chatapp.core.interfaceC.ValueInterface;
import com.vantagecircle.chatapp.model.ChatM;
import com.vantagecircle.chatapp.model.GroupM;
import com.vantagecircle.chatapp.model.UserM;
import com.vantagecircle.chatapp.httpcall.SendNotification;
import com.vantagecircle.chatapp.utils.ConfigUtils;
import com.vantagecircle.chatapp.utils.Constants;
import com.vantagecircle.chatapp.utils.ToolsUtils;
import com.vantagecircle.chatapp.utils.UpdateKeyUtils;

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


    //cast and bind view from layout
    protected int loadView(){
        return R.layout.activity_chat;
    }

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
        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
    }

    protected void initListener() {
        btn_send_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_message.getText().toString().length() > 0) {
                    pushMessage(prepareChatModel(et_message.getText().toString(),
                            Constants.TEXT_CONTENT, null));
                } else {
                    Toast.makeText(mContext, "Type some message", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    //initialize app for chat
    protected void initialize() {
        //initialize app data and set user online if user came from notification bar
        isFromNotification = getIntent().getBooleanExtra("isFromBar", false);
        if (isFromNotification) {
            ConfigUtils.initializeApp(mContext);
            UpdateKeyUtils.updateOnlineStatus(true);
            UpdateKeyUtils.updateLastSeen(new Date().getTime());
        }

        //check intent is group chat or not
        isGroup = getIntent().getBooleanExtra("isGroup", false);
        if (isGroup) {
            groupM = new Gson().fromJson(getIntent().getStringExtra("data"), GroupM.class);
            mActionBar.setTitle(groupM.getName());
            getTokens();
        } else {
            userM = new Gson().fromJson(getIntent().getStringExtra("data"), UserM.class);
            mActionBar.setTitle(userM.getFullName());
            getOnlineStatus();
        }

        //get all chat history
        getChatHistory();
    }


    //get all messages from firebase db and bind
    protected void getChatHistory() {
        if (isGroup) {
            ConfigUtils.checkRooms(groupM, new ResultInterface() {
                @Override
                public void onSuccess(String t) {
                    if (t.equals(Constants.NO_ROOM)) {
                        Log.d(TAG, "Current Room created");
                        currentRoom = ConfigUtils.createRoom(groupM);
                    } else {
                        Log.d(TAG, "Current Room updated");
                        currentRoom = t;
                        if (chatMAdapter == null) {
                            chatMAdapter = new ChatMAdapter(ChatM.class, 0, ChatMViewHolder.class,
                                    SupportService.getChatReference().child(currentRoom));
                            recyclerView.setAdapter(chatMAdapter);
                        }
                    }
                }

                @Override
                public void onFail(String e) {
                    Log.d(TAG, "Current room Error " + e);
                }
            });
        } else {
            ConfigUtils.checkRooms(userM, new ResultInterface() {
                @Override
                public void onSuccess(String t) {
                    if (t.equals(Constants.NO_ROOM)) {
                        Log.d(TAG, "Current Room created");
                        currentRoom = ConfigUtils.createRoom(userM);
                    } else {
                        Log.d(TAG, "Current Room updated");
                        currentRoom = t;
                        if (chatMAdapter == null) {
                            chatMAdapter = new ChatMAdapter(ChatM.class, 0, ChatMViewHolder.class,
                                    SupportService.getChatReference().child(currentRoom));
                            recyclerView.setAdapter(chatMAdapter);
                        }
                    }
                }

                @Override
                public void onFail(String e) {
                    Log.d(TAG, "Current room Error " + e);
                }
            });
        }
    }

    protected void getOnlineStatus() {
        GetDataHandler getDataHandler = new GetDataHandler();
        getDataHandler.setDataReference(SupportService.getUserReference().child(userM.getUserId()));
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
        getDataHandler.setDataReference(SupportService.getGroupReference()
                .child(groupM.getId())
                .child("users"));
        getDataHandler.setChildValueListener(new ChildInterface() {
            @Override
            public void onChildNew(DataModel dataModel) {
                tokens.add(dataModel.getDataSnapshot()
                        .child("fcmToken")
                        .getValue().toString());
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


    //push message to firebase db
    protected ChatM prepareChatModel(String text, String type, String uri) {
        Log.d(TAG, "Current Room === " + currentRoom);
        ChatM chatM = null;
        try {
            String senderName = SupportService.userM.getFullName();
            String senderUid = SupportService.id;
            String receiverName;
            String receiverUid;
            String convType;
            if (isGroup) {
                receiverName = groupM.getName();
                receiverUid = groupM.getId();
                convType = Constants.CONV_GR;
            } else {
                receiverName = userM.getFullName();
                receiverUid = userM.getUserId();
                convType = Constants.CONV_SN;
            }
            long timeStamp = new Date().getTime();

            chatM = new ChatM(senderName, receiverName, senderUid, receiverUid,
                    type, text, uri, timeStamp, false, false, currentRoom, convType,
                    SupportService.userM.getFcmToken(), isGroup ? null : userM.getFcmToken());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatM;
    }

    protected void pushMessage(final ChatM chatM) {
        //clear edit text and scroll recycler view to bottom
        et_message.setText("");

        //config handler and push chat data to current room
        SetDataHandler setDataHandler = new SetDataHandler();
        setDataHandler.setDatabaseReference(SupportService.getChatReference()
                .child(currentRoom)
                .child(String.valueOf(chatM.getTimeStamp())));
        setDataHandler.insertData(chatM, new ResultInterface() {
            @Override
            public void onSuccess(String t) {
                recyclerView.smoothScrollToPosition(chatMAdapter.getItemCount() == 0 ? 0 :
                        chatMAdapter.getItemCount() - 1);
                //send push notification to the user if chat type is text type
                if (chatM.getChatType().equals(Constants.TEXT_CONTENT)) {
                    SendNotification sendNotification = new SendNotification();
                    sendNotification.prepareNotification(chatM);
                } else {
                    Log.d(TAG, "Notification will be sent after file upload");
                }
            }

            @Override
            public void onFail(String e) {
                Log.d(TAG, "Error " + e);
            }
        });
    }


    //file choose config and function
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.attach_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            finish();
        } else if (i == R.id.action_attach) {
            showContextMenu(getCurrentFocus());

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
                if (!ToolsUtils.isHasPermissions(this, PERMISSIONS)) {
                    ActivityCompat.requestPermissions(this, PERMISSIONS, Constants.REQUEST_STORAGE_PERMISSION);
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("*/*");
                    startActivityForResult(intent, Constants.REQUEST_CODE_GALLERY);
                }
            } else {
                fileName = null;
                decodeFile = null;
                String[] PERMISSIONS = {Manifest.permission.CAMERA};
                if (!ToolsUtils.isHasPermissions(this, PERMISSIONS)) {
                    ActivityCompat.requestPermissions(this, PERMISSIONS, Constants.REQUEST_CAMERA_PERMISSION);
                } else {
                    Calendar c = Calendar.getInstance();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    fileName = "image_" + c.getTimeInMillis() + ".jpg";
                    decodeFile = new File(path, fileName);
                    Uri tempUri = Uri.fromFile(decodeFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                    startActivityForResult(intent, Constants.REQUEST_CODE_CAMERA);
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
            case Constants.REQUEST_STORAGE_PERMISSION:
                try {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("*/*");
                        startActivityForResult(intent, Constants.REQUEST_CODE_GALLERY);
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
            case Constants.REQUEST_CAMERA_PERMISSION:
                try {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Calendar c = Calendar.getInstance();
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                        fileName = "image_" + c.getTimeInMillis() + ".jpg";
                        decodeFile = new File(path, fileName);
                        Uri tempUri = Uri.fromFile(decodeFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                        startActivityForResult(intent, Constants.REQUEST_CODE_CAMERA);
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
            case Constants.REQUEST_CODE_CAMERA:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        try {
                            if (decodeFile != null && decodeFile.exists()) {
                                Uri selectedImage = Uri.fromFile(decodeFile);
                                String mimeType = getContentResolver().getType(selectedImage);
                                Log.d(TAG, "File Mime Type === " + mimeType);
                                if (mimeType == null) {
                                    Toast.makeText(mContext, "There was an error in file",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    ChatM chatM = prepareChatModel(null, Constants.IMAGE_CONTENT,
                                            selectedImage.toString());
                                    pushMessage(chatM);
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
            case Constants.REQUEST_CODE_GALLERY:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        try {
                            if (data != null && data.getData() != null) {
                                Uri selectedImage = data.getData();
                                String mimeType = getContentResolver().getType(selectedImage);
                                Log.d(TAG, "File Mime Type === " + mimeType);
                                if (mimeType == null) {
                                    Toast.makeText(mContext, "There was an error in file",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    ChatM chatM = prepareChatModel(null, Constants.IMAGE_CONTENT,
                                            selectedImage.toString());
                                    pushMessage(chatM);
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


    //handle activity life cycle
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
        SupportService.setIsChatWindowActive(true);
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
        SupportService.setIsChatWindowActive(false);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        if (isFromNotification) {
            UpdateKeyUtils.updateOnlineStatus(false);
            UpdateKeyUtils.updateLastSeen(new Date().getTime());
        }
    }
}