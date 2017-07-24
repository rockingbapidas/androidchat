package com.vantagecircle.chatapp.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.vantagecircle.chatapp.R;
import com.vantagecircle.chatapp.Support;
import com.vantagecircle.chatapp.data.Config;
import com.vantagecircle.chatapp.model.ChatM;
import com.vantagecircle.chatapp.utils.DateUtils;
import com.vantagecircle.chatapp.utils.Tools;

/**
 * Created by bapidas on 20/07/17.
 */

public class ChatMAdapter extends FirebaseRecyclerAdapter<ChatM, ChatMAdapter.ChatMViewHolder> {
    private static final String TAG = ChatMAdapter.class.getSimpleName();
    private static boolean isChatContinue;

    public ChatMAdapter(DatabaseReference ref) {
        super(ChatM.class, 0, ChatMViewHolder.class, ref);
    }

    @Override
    public int getItemViewType(int position) {
        ChatM model = getItem(position);
        ChatM modelPrevious = getItem(position);
        if (position > 0) {
            modelPrevious = getItem(position - 1);
        }
        isChatContinue = model.getSenderName().equals(modelPrevious.getSenderName()) && position > 0;
        switch (model.getChatType()) {
            case Config.IMAGE_TYPE:
                return R.layout.row_chat_image;
            default:
                return R.layout.row_chat_text;
        }
    }

    @Override
    protected void populateViewHolder(ChatMViewHolder viewHolder, ChatM model, int position) {
        viewHolder.setDataToViews(model);
    }

    static class ChatMViewHolder extends RecyclerView.ViewHolder {
        TextView userName, messageText, dateTime;
        ImageView statusImage, fileImage;
        CardView lyt_thread;
        ProgressBar progressBar;
        LinearLayout lyt_parent;
        Context context;

        public ChatMViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            userName = (TextView) itemView.findViewById(R.id.sender);
            messageText = (TextView) itemView.findViewById(R.id.text_content);
            dateTime = (TextView) itemView.findViewById(R.id.text_time);
            statusImage = (ImageView) itemView.findViewById(R.id.chat_status);
            fileImage = (ImageView) itemView.findViewById(R.id.image_status);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            lyt_parent = (LinearLayout) itemView.findViewById(R.id.lyt_parent);
            lyt_thread = (CardView) itemView.findViewById(R.id.lyt_thread);
        }

        void setDataToViews(ChatM chatM) {
            switch (chatM.getChatType()) {
                case Config.IMAGE_TYPE:
                    if (chatM.getFileUrl() != null) {
                        progressBar.setVisibility(View.GONE);
                        Tools.loadPicasso(context, fileImage, chatM.getFileUrl());
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
                userName.setTextColor(ContextCompat.getColor(context, R.color.colorOrange));
                lyt_parent.setPadding(100, 10, 15, 10);
                lyt_parent.setGravity(Gravity.END);
                statusImage.setVisibility(View.VISIBLE);
                lyt_thread.setCardBackgroundColor(ContextCompat.getColor(context, R.color.chat_background));
            } else {
                userName.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                statusImage.setVisibility(View.GONE);
                lyt_parent.setPadding(15, 10, 100, 10);
                lyt_parent.setGravity(Gravity.START);
                lyt_thread.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white));
            }
        }
    }
}
