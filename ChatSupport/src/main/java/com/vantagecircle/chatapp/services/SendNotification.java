package com.vantagecircle.chatapp.services;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.vantagecircle.chatapp.Support;
import com.vantagecircle.chatapp.data.ConstantM;
import com.vantagecircle.chatapp.model.NotificationM;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by bapidas on 12/07/17.
 */

public class SendNotification {
    private static final String TAG = SendNotification.class.getSimpleName();

    //Http call configuration
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String SERVER_API_KEY = "AAAAutjgRd0:APA91bF-0Eo8AQhDPFv1q0hPCmB0vBrYaMA9_l0IYH_vi6gXdJv9JKM6m9BRgJjbjuBktd07HXLUsOae7sjGZAWDeJ-BE8d1SUVOmcMZFbo4vWzM0tvo-ON-G_JQqfhqPetc6IpxZGXB";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String ACCEPT = "Accept";
    private static final String APPLICATION_JSON = "application/json";
    private static final String AUTHORIZATION = "Authorization";
    private static final String AUTH_KEY = "key=" + SERVER_API_KEY;
    private static final String FCM_USER_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String FCM_GROUP_URL = "https://gcm-http.googleapis.com/gcm/send";

    //Json keys from fcm data
    private static final String KEY_TO = "to";
    private static final String KEY_DATA = "data";

    private static final String KEY_TITLE = "title";
    private static final String KEY_TYPE= "type";
    private static final String KEY_TEXT = "text";
    private static final String KEY_URI = "fileUri";
    private static final String KEY_USERNAME = "senderUsername";
    private static final String KEY_UID = "senderUid";
    private static final String KEY_FCM_TOKEN = "senderToken";

    //Notification model
    private NotificationM notificationM;


    public SendNotification(NotificationM notificationM) {
        this.notificationM = notificationM;
    }

    public void sendForSingle() {
        try {
            RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON,
                    getSingleObject().toString());
            Request request = new Request.Builder()
                    .addHeader(CONTENT_TYPE, APPLICATION_JSON)
                    .addHeader(AUTHORIZATION, AUTH_KEY)
                    .url(FCM_USER_URL)
                    .post(requestBody)
                    .build();
            Call call = new OkHttpClient().newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "onFailure: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.e(TAG, "onResponse body: " + response.body().string());
                    Log.e(TAG, "onResponse code: " + response.code());
                    if (response.code() == 200) {
                        ConstantM.updateSentStatus(notificationM.getChatRoom(),
                                notificationM.getTimeStamp());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendForGroup(){
        try {
            RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON,
                    getGroupObject().toString());
            Request request = new Request.Builder()
                    .addHeader(ACCEPT, APPLICATION_JSON)
                    .addHeader(AUTHORIZATION, AUTH_KEY)
                    .addHeader(CONTENT_TYPE, APPLICATION_JSON)
                    .url(FCM_GROUP_URL)
                    .post(requestBody)
                    .build();
            Call call = new OkHttpClient().newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "onFailure: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.e(TAG, "onResponse body: " + response.body().string());
                    Log.e(TAG, "onResponse code: " + response.code());
                    /*if (response.code() == 200) {
                        ConstantM.updateSentStatus(notificationM.getChatRoom(),
                                notificationM.getTimeStamp());
                    }*/
                    ConstantM.updateSentStatus(notificationM.getChatRoom(),
                            notificationM.getTimeStamp());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JSONObject getSingleObject() throws JSONException {
        JSONObject parentBody = new JSONObject();
        parentBody.put(KEY_TO, notificationM.getReceiverFcmToken());

        JSONObject childData = new JSONObject();
        childData.put(KEY_TITLE, notificationM.getTitle());
        childData.put(KEY_TYPE, notificationM.getChatType());
        childData.put(KEY_TEXT, notificationM.getMessageText());
        childData.put(KEY_URI, notificationM.getFileUrl());

        childData.put(KEY_USERNAME, notificationM.getSenderUsername());
        childData.put(KEY_UID, notificationM.getSenderUid());
        childData.put(KEY_FCM_TOKEN, notificationM.getSenderFcmToken());

        parentBody.put(KEY_DATA, childData);
        return parentBody;
    }

    private JSONObject getGroupObject() throws JSONException {
        JSONObject parentBody = new JSONObject();
        parentBody.put(KEY_TO, notificationM.getReceiverFcmToken());

        JSONObject childData = new JSONObject();
        childData.put(KEY_TITLE, notificationM.getTitle());
        childData.put(KEY_TYPE, notificationM.getChatType());
        childData.put(KEY_TEXT, notificationM.getMessageText());
        childData.put(KEY_URI, notificationM.getFileUrl());

        childData.put(KEY_USERNAME, notificationM.getSenderUsername());
        childData.put(KEY_UID, notificationM.getSenderUid());
        childData.put(KEY_FCM_TOKEN, notificationM.getSenderFcmToken());

        parentBody.put(KEY_DATA, childData);
        return parentBody;
    }
}
