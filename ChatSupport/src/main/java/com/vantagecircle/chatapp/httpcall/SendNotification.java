package com.vantagecircle.chatapp.httpcall;

import android.util.Log;

import com.vantagecircle.chatapp.model.ChatM;
import com.vantagecircle.chatapp.utils.Constants;
import com.vantagecircle.chatapp.utils.UpdateKeyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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
    private final String TAG = SendNotification.class.getSimpleName();

    //Http call configuration
    private final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    private final String SERVER_API_KEY = "AAAAutjgRd0:APA91bF-0Eo8AQhDPFv1q0hPCmB0vBrYaMA9_l0IYH_vi6gXdJv9JKM6m9BRgJjbjuBktd07HXLUsOae7sjGZAWDeJ-BE8d1SUVOmcMZFbo4vWzM0tvo-ON-G_JQqfhqPetc6IpxZGXB";
    private final String CONTENT_TYPE = "Content-Type";
    private final String ACCEPT = "Accept";
    private final String APPLICATION_JSON = "application/json";
    private final String AUTHORIZATION = "Authorization";
    private final String AUTH_KEY = "key=" + SERVER_API_KEY;

    private final String FCM_USER_URL = "https://fcm.googleapis.com/fcm/send";
    private final String FCM_GROUP_URL = "https://gcm-http.googleapis.com/gcm/send";

    private final String TOPIC_SUBSCRIBE_URL1 = "https://iid.googleapis.com/iid/v1/";
    private final String TOPIC_SUBSCRIBE_URL2 = "/rel/topics/";

    //Json keys from fcm data
    private final String KEY_TO = "to";
    private final String KEY_DATA = "data";

    private final String KEY_TITLE = "title";
    private final String KEY_TYPE = "type";
    private final String KEY_TEXT = "text";
    private final String KEY_URI = "fileUri";
    private final String KEY_CON_TYPE = "conType";
    private final String KEY_USERNAME = "userName";
    private final String KEY_UID = "userID";
    private final String KEY_FCM_TOKEN = "userToken";

    //Notification model
    private NotificationM notificationM;


    public SendNotification() {
        notificationM = new NotificationM();
    }

    public void prepareNotification(ChatM chatM) {
        try {
            String chatType = chatM.getChatType();
            String messageText = chatM.getMessageText();
            String fileUrl = chatM.getFileUrl();
            long timeStamp = chatM.getTimeStamp();

            notificationM.setTitle(chatM.getSenderName());
            notificationM.setChatType(chatType);
            notificationM.setMessageText(messageText);
            notificationM.setFileUrl(fileUrl);
            notificationM.setTimeStamp(timeStamp);
            notificationM.setChatRoom(chatM.getChatRoom());
            notificationM.setConversationType(chatM.getConvType());

            notificationM.setSenderUsername(chatM.getSenderName());
            notificationM.setReceiverUserName(chatM.getReceiverName());

            notificationM.setSenderUid(chatM.getSenderUid());
            notificationM.setReceiverUid(chatM.getReceiverUid());

            notificationM.setSenderFcmToken(chatM.getSenderToken());
            notificationM.setReceiverFcmToken(chatM.getReceiverToken());

            if (chatM.getConvType().equals(Constants.CONV_GR)) {
                sendToGroup();
            } else {
                sendToSingle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void subscribeToken(String token, String groupName) {
        try {
            String url = TOPIC_SUBSCRIBE_URL1 + token + TOPIC_SUBSCRIBE_URL2 + groupName;
            Log.e(TAG, "subscribeToken ==== " + url);
            RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, "");
            Request request = new Request.Builder()
                    .addHeader(CONTENT_TYPE, APPLICATION_JSON)
                    .addHeader(AUTHORIZATION, AUTH_KEY)
                    .url(url)
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
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendToSingle() {
        try {
            Log.e(TAG, "sendToSingle ==== ");
            RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON,
                    getDataObject().toString());
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
                        UpdateKeyUtils.updateSentStatus(notificationM.getChatRoom(),
                                notificationM.getTimeStamp());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendToGroup() {
        try {
            Log.e(TAG, "sendToGroup ==== ");
            RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON,
                    getDataObject().toString());
            Request request = new Request.Builder()
                    .addHeader(CONTENT_TYPE, APPLICATION_JSON)
                    .addHeader(AUTHORIZATION, AUTH_KEY)
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
                    if (response.code() == 200) {
                        UpdateKeyUtils.updateSentStatus(notificationM.getChatRoom(),
                                notificationM.getTimeStamp());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JSONObject getDataObject() throws JSONException {
        JSONObject parentBody = new JSONObject();
        if (notificationM.getConversationType().equals(Constants.CONV_SN)) {
            parentBody.put(KEY_TO, notificationM.getReceiverFcmToken());
        } else {
            parentBody.put(KEY_TO, "/topics/" + notificationM.getChatRoom());
        }

        JSONObject childData = new JSONObject();
        childData.put(KEY_TITLE, notificationM.getTitle());
        childData.put(KEY_TYPE, notificationM.getChatType());
        childData.put(KEY_TEXT, notificationM.getMessageText());
        childData.put(KEY_URI, notificationM.getFileUrl());
        childData.put(KEY_CON_TYPE, notificationM.getConversationType());

        if (notificationM.getConversationType().equals(Constants.CONV_SN)) {
            childData.put(KEY_USERNAME, notificationM.getSenderUsername());
            childData.put(KEY_UID, notificationM.getSenderUid());
            childData.put(KEY_FCM_TOKEN, notificationM.getSenderFcmToken());
        } else {
            childData.put(KEY_USERNAME, notificationM.getReceiverUserName());
            childData.put(KEY_UID, notificationM.getReceiverUid());
        }

        parentBody.put(KEY_DATA, childData);
        Log.e(TAG, "noti data === " + parentBody);
        return parentBody;
    }
}
