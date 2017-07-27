package com.vantagecircle.chatapp.adapter;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.vantagecircle.chatapp.R;
import com.vantagecircle.chatapp.utils.Config;
import com.vantagecircle.chatapp.holder.ChatMViewHolder;
import com.vantagecircle.chatapp.model.ChatM;

/**
 * Created by bapidas on 20/07/17.
 */

public class ChatMAdapter extends FirebaseRecyclerAdapter<ChatM, ChatMViewHolder> {
    private static final String TAG = ChatMAdapter.class.getSimpleName();
    private boolean isChatContinue;

    public ChatMAdapter(Class<ChatM> modelClass, int modelLayout, Class<ChatMViewHolder> viewHolderClass, Query ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);
    }

    public ChatMAdapter(Class<ChatM> modelClass, int modelLayout, Class<ChatMViewHolder> viewHolderClass, DatabaseReference ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);
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
        viewHolder.setDataToViews(model, isChatContinue);
    }
}
