package com.vantagecircle.chatapp.holder;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vantagecircle.chatapp.R;
import com.vantagecircle.chatapp.Support;
import com.vantagecircle.chatapp.model.ChatM;
import com.vantagecircle.chatapp.utils.Constants;
import com.vantagecircle.chatapp.utils.DateUtils;
import com.vantagecircle.chatapp.utils.ToolsUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by bapidas on 31/07/17.
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class ChatMViewHolderTest {
    private TextView userName, messageText, dateTime;
    private ImageView statusImage, fileImage;
    private CardView lyt_thread;
    private ProgressBar progressBar;
    private LinearLayout lyt_parent;
    private Context appContext;
    private View view;
    private ChatM chatM;
    private boolean isChatContinue;

    @Before
    public void setUp() throws Exception {
        appContext = InstrumentationRegistry.getTargetContext();
        createData();
        initViews();
        testPreconditions();
        setDataToViews();
    }

    public void createData() throws Exception {
        chatM = new ChatM();
        chatM.setTimeStamp(System.currentTimeMillis());
        chatM.setSenderUid("1");
        chatM.setSenderName("Bapi");
        chatM.setReceiverUid("12");
        chatM.setReceiverName("Sameer");
        chatM.setChatType("text");
        chatM.setFileUrl(null);
        chatM.setMessageText("Good Morning");
        chatM.setReadSuccessfully(false);
        chatM.setSentSuccessfully(false);
        isChatContinue = false;
    }

    @Test
    public void initViews() throws Exception {
        if (chatM.getChatType().equals("text")) {
            view = View.inflate(appContext, R.layout.row_chat_text, null);
            userName = (TextView) view.findViewById(R.id.sender);
            messageText = (TextView) view.findViewById(R.id.text_content);
            dateTime = (TextView) view.findViewById(R.id.text_time);
            statusImage = (ImageView) view.findViewById(R.id.chat_status);
            lyt_parent = (LinearLayout) view.findViewById(R.id.lyt_parent);
            lyt_thread = (CardView) view.findViewById(R.id.lyt_thread);
        } else {
            view = View.inflate(appContext, R.layout.row_chat_image, null);
            userName = (TextView) view.findViewById(R.id.sender);
            dateTime = (TextView) view.findViewById(R.id.text_time);
            statusImage = (ImageView) view.findViewById(R.id.chat_status);
            fileImage = (ImageView) view.findViewById(R.id.image_status);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
            lyt_parent = (LinearLayout) view.findViewById(R.id.lyt_parent);
            lyt_thread = (CardView) view.findViewById(R.id.lyt_thread);
        }
    }

    public void testPreconditions() throws Exception {
        assertNotNull("appContext is null", appContext);
        assertEquals("com.vantagecircle.chatapp", appContext.getPackageName());
        assertNotNull("chatM is null", chatM);
        assertNotNull("view is null", view);
        if (chatM.getChatType().equals("text")) {
            assertNotNull("userName is null", userName);
            assertNotNull("messageText is null", messageText);
            assertNotNull("dateTime is null", dateTime);
            assertNotNull("statusImage is null", statusImage);
            assertNotNull("lyt_parent is null", lyt_parent);
            assertNotNull("lyt_thread is null", lyt_thread);
        } else {
            assertNotNull("userName is null", userName);
            assertNotNull("dateTime is null", dateTime);
            assertNotNull("statusImage is null", statusImage);
            assertNotNull("fileImage is null", fileImage);
            assertNotNull("progressBar is null", progressBar);
            assertNotNull("lyt_parent is null", lyt_parent);
            assertNotNull("lyt_thread is null", lyt_thread);
        }
    }

    @Test
    public void setDataToViews() throws Exception {
        switch (chatM.getChatType()) {
            case Constants.IMAGE_CONTENT:
                if (chatM.getFileUrl() != null) {
                    progressBar.setVisibility(View.GONE);
                    ToolsUtils.loadPicasso(appContext, fileImage, chatM.getFileUrl());
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    fileImage.setImageResource(R.drawable.ic_insert_photo_black_24dp);
                }
                break;
            default:
                messageText.setText(chatM.getMessageText());
                break;
        }

        userName.setText(chatM.getSenderName());
        dateTime.setText(DateUtils.getTimeAgo(chatM.getTimeStamp()));
        if (isChatContinue) {
            userName.setVisibility(View.GONE);
        } else {
            userName.setVisibility(View.VISIBLE);
        }
        if (chatM.isSentSuccessfully()) {
            statusImage.setImageResource(R.drawable.single_tick);
        } else {
            statusImage.setImageResource(R.drawable.ic_msg_wait);
        }
        //change row alignment on basis of user
        if (chatM.getSenderUid().equals(Support.id)) {
            userName.setTextColor(ContextCompat.getColor(appContext, R.color.colorOrange));
            lyt_parent.setPadding(100, 10, 15, 10);
            lyt_parent.setGravity(Gravity.END);
            statusImage.setVisibility(View.VISIBLE);
            lyt_thread.setCardBackgroundColor(ContextCompat.getColor(appContext, R.color.chat_background));
        } else {
            userName.setTextColor(ContextCompat.getColor(appContext, R.color.colorAccent));
            statusImage.setVisibility(View.GONE);
            lyt_parent.setPadding(15, 10, 100, 10);
            lyt_parent.setGravity(Gravity.START);
            lyt_thread.setCardBackgroundColor(ContextCompat.getColor(appContext, R.color.white));
        }
    }
}