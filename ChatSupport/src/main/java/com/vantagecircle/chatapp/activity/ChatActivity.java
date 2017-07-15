package com.vantagecircle.chatapp.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.vantagecircle.chatapp.R;
import com.vantagecircle.chatapp.Support;
import com.vantagecircle.chatapp.adapter.ChatAdapter;
import com.vantagecircle.chatapp.data.ConstantM;
import com.vantagecircle.chatapp.model.ChatM;
import com.vantagecircle.chatapp.model.UserM;
import com.vantagecircle.chatapp.model.NotificationM;
import com.vantagecircle.chatapp.services.SendNotification;
import com.vantagecircle.chatapp.utils.DateUtils;

import java.util.ArrayList;

/**
 * Created by bapidas on 10/07/17.
 */

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = ChatActivity.class.getSimpleName();
    ActionBar mActionBar;
    Toolbar mToolbar;
    Context mContext;
    Activity activity;
    UserM userM;
    EditText et_message;
    ImageButton btn_send_txt;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    ChatAdapter chatAdapter;
    ArrayList<ChatM> chatMs;
    int lastPosition;
    String room_type_1, room_type_2;
    boolean isSentFromMeNow = false;
    ProgressDialog progressDialog;

    ValueEventListener sendEventListener, getEventListener, statusEventListener;
    ChildEventListener childEventListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        activity = this;
        setContentView(R.layout.activity_chat);
        userM = new Gson().fromJson(getIntent().getStringExtra("data"), UserM.class);
        room_type_1 = Support.id + "_" + userM.getUserId();
        room_type_2 = userM.getUserId() + "_" + Support.id;

        initToolbar();
        initView();
        initRecycler();
        initListener();

        /*getOnlineStatus();
        getMessages();*/
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
                    prepareModel();
                } else {
                    Toast.makeText(mContext, "Enter some message", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void prepareModel() {
        try {
            String senderName = Support.userM.getFullName();
            String receiverName = userM.getFullName();
            String senderUid = Support.id;
            String receiverUid = userM.getUserId();
            String messageText = et_message.getText().toString();
            long timeStamp = System.currentTimeMillis();
            ChatM chatM = new ChatM(senderName, receiverName, senderUid,
                    receiverUid, messageText, timeStamp);
            insertToAdapter(chatM);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertToAdapter(ChatM chatM) {
        try {
            if (chatMs == null) {
                chatMs = new ArrayList<>();
                chatMs.add(chatM);
                chatAdapter = new ChatAdapter(mContext, chatMs);
                recyclerView.setAdapter(chatAdapter);
            } else {
                chatMs.add(chatM);
                chatAdapter.notifyItemInserted(chatMs.size());
            }
            recyclerView.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
            lastPosition = chatAdapter.getItemCount() - 1;
            et_message.setText("");
            isSentFromMeNow = true;
            sendMessage(chatM);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(final ChatM chatM) {
        try {
            sendEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(room_type_1)) {
                        Support.getChatReference().child(room_type_1)
                                .child(String.valueOf(chatM.getTimeStamp()))
                                .setValue(chatM)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.e(TAG, "onComplete ");
                                        if (task.isSuccessful()) {
                                            chatAdapter.toggleStatus(lastPosition);
                                            sendPushNotification(chatM, room_type_1);
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, "onFailure " + e.getMessage());
                                    }
                                });
                    } else if (dataSnapshot.hasChild(room_type_2)) {
                        Support.getChatReference().child(room_type_2)
                                .child(String.valueOf(chatM.getTimeStamp()))
                                .setValue(chatM)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.e(TAG, "onComplete ");
                                        if (task.isSuccessful()) {
                                            chatAdapter.toggleStatus(lastPosition);
                                            sendPushNotification(chatM, room_type_2);
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, "onFailure " + e.getMessage());
                                    }
                                });
                    } else {
                        Support.getChatReference().child(room_type_1)
                                .child(String.valueOf(chatM.getTimeStamp()))
                                .setValue(chatM)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.e(TAG, "onComplete ");
                                        if (task.isSuccessful()) {
                                            chatAdapter.toggleStatus(lastPosition);
                                            sendPushNotification(chatM, room_type_1);
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, "onFailure " + e.getMessage());
                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "onCancelled " + databaseError.getMessage());
                }
            };
            Support.getChatReference().addListenerForSingleValueEvent(sendEventListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendPushNotification(ChatM chatM, String chat_room) {
        try {
            String senderUsername = chatM.getSenderName();
            String senderUserId = chatM.getSenderUid();
            String senderFcmToken = Support.userM.getFcmToken();
            String messageText = chatM.getMessageText();
            String receiverFcmToken = userM.getFcmToken();
            long timeStamp = chatM.getTimeStamp();

            NotificationM notificationM = new NotificationM();
            notificationM.setTitle(senderUsername);
            notificationM.setMessageText(messageText);
            notificationM.setSenderUsername(senderUsername);
            notificationM.setSenderUid(senderUserId);
            notificationM.setSenderFcmToken(senderFcmToken);
            notificationM.setTimeStamp(timeStamp);
            notificationM.setChatRoom(chat_room);
            notificationM.setReceiverFcmToken(receiverFcmToken);

            SendNotification sendNotification = new SendNotification(notificationM);
            sendNotification.send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getMessages() {
        try {
            getEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(room_type_1)) {
                        childEventListener = new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                Log.e(TAG, "onChildAdded ");
                                setListToAdapter(dataSnapshot);
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                Log.e(TAG, "onChildChanged ");
                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {
                                Log.e(TAG, "onChildRemoved ");
                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                                Log.e(TAG, "onChildMoved ");
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, "onCancelled " + databaseError.getMessage());
                            }
                        };
                        Support.getChatReference().child(room_type_1).addChildEventListener(childEventListener);
                    } else if (dataSnapshot.hasChild(room_type_2)) {
                        childEventListener = new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                Log.e(TAG, "onChildAdded ");
                                setListToAdapter(dataSnapshot);
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                Log.e(TAG, "onChildChanged ");
                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {
                                Log.e(TAG, "onChildRemoved ");
                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                                Log.e(TAG, "onChildMoved ");
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, "onCancelled " + databaseError.getMessage());
                            }
                        };
                        Support.getChatReference().child(room_type_2).addChildEventListener(childEventListener);
                    } else {
                        Log.e(TAG, "No such chat data available");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "onCancelled Database error " + databaseError.getMessage());
                }
            };
            Support.getChatReference().addListenerForSingleValueEvent(getEventListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getOnlineStatus() {
        try {
            statusEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
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
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled " + databaseError.getMessage());
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
        Support.getUserReference().child(userM.getUserId())
                .addValueEventListener(statusEventListener);
    }

    private void setListToAdapter(DataSnapshot dataSnapshot) {
        if (!isSentFromMeNow) {
            ChatM chatM = dataSnapshot.getValue(ChatM.class);
            if (chatMs == null) {
                chatMs = new ArrayList<>();
                chatMs.add(chatM);
                chatAdapter = new ChatAdapter(mContext, chatMs);
                recyclerView.setAdapter(chatAdapter);
            } else {
                chatMs.add(chatM);
                chatAdapter.notifyItemInserted(chatMs.size());
            }
            recyclerView.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
        } else {
            isSentFromMeNow = false;

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Support.setIsChatWindowActive(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Support.setIsChatWindowActive(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getOnlineStatus();
        getMessages();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (sendEventListener != null) {
            Support.getChatReference()
                    .removeEventListener(sendEventListener);
        }
        if (getEventListener != null) {
            Support.getChatReference()
                    .removeEventListener(getEventListener);
        }
        if (statusEventListener != null) {
            Support.getUserReference().child(userM.getUserId())
                    .removeEventListener(statusEventListener);
        }

        if (childEventListener != null) {
            Support.getChatReference().child(room_type_1)
                    .removeEventListener(childEventListener);
            Support.getChatReference().child(room_type_2)
                    .removeEventListener(childEventListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
