package com.vantagecircle.chatapp.httpcall;

import android.content.Context;
import android.util.Log;

import com.vantagecircle.chatapp.R;
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

    //Http header configuration
    private final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    private final String CONTENT_TYPE = "Content-Type";
    private final String ACCEPT = "Accept";
    private final String MIME_TYPE = "*/*";
    private final String APPLICATION_JSON = "application/json";
    private final String AUTHORIZATION = "Authorization";

    //topic subscribe url address
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
    private final String KEY_CHAT_FLAG = "chatSource";

    private final String KEY_CONTEST_ID = "contestId";
    private final String KEY_CONTEST_NAME = "contestName";
    private final String KEY_ROOM = "contestRoom";

    //Notification model
    private NotificationM notificationM;
    private Context mContext;


    public SendNotification(Context context) {
        this.notificationM = new NotificationM();
        this.mContext = context;
    }

    public void subscribeTokenToTopic(String token, String groupName) {
        try {
            String url = TOPIC_SUBSCRIBE_URL1 + token + TOPIC_SUBSCRIBE_URL2 + groupName;
            Log.e(TAG, "subscribeTokenToTopic ==== " + url);
            RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, "");
            Request request = new Request.Builder()
                    .addHeader(CONTENT_TYPE, APPLICATION_JSON)
                    .addHeader(AUTHORIZATION, mContext.getResources().getString(R.string.server_key))
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

            sendNotificationToUser();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendNotificationToUser() {
        try {
            Log.e(TAG, "sendNotificationToUser ==== ");
            RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON,
                    getDataObject().toString());
            Request request = new Request.Builder()
                    .addHeader(CONTENT_TYPE, APPLICATION_JSON)
                    .addHeader(ACCEPT, MIME_TYPE)
                    .addHeader(AUTHORIZATION, mContext.getResources().getString(R.string.server_key))
                    .url(mContext.getResources().getString(R.string.fcm_url))
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
        //parent json body setup
        JSONObject parentBody = new JSONObject();
        if (notificationM.getConversationType().equals(Constants.CONV_SN)) {
            parentBody.put(KEY_TO, notificationM.getReceiverFcmToken());
        } else {
            parentBody.put(KEY_TO, "/topics/" + notificationM.getChatRoom());
        }
        parentBody.put(KEY_DATA, getChildData());
        Log.e(TAG, "data === " + parentBody);
        return parentBody;
    }

    private JSONObject getChildData() throws JSONException{
        //child json body setup
        JSONObject childData = new JSONObject();
        childData.put(KEY_TITLE, notificationM.getTitle());
        childData.put(KEY_TYPE, notificationM.getChatType());
        childData.put(KEY_TEXT, notificationM.getMessageText());
        childData.put(KEY_URI, notificationM.getFileUrl());
        childData.put(KEY_CON_TYPE, notificationM.getConversationType());

        //childData.put(KEY_USERNAME, notificationM.getSenderUsername());
        childData.put(KEY_UID, notificationM.getSenderUid());
        //childData.put(KEY_FCM_TOKEN, notificationM.getSenderFcmToken());
        childData.put(KEY_CHAT_FLAG, true);

        if (notificationM.getConversationType().equals(Constants.CONV_GR)) {
            childData.put(KEY_CONTEST_ID, notificationM.getReceiverUid());
            childData.put(KEY_CONTEST_NAME, notificationM.getReceiverUserName());
            childData.put(KEY_ROOM, notificationM.getChatRoom());
        }
        return childData;
    }
}
