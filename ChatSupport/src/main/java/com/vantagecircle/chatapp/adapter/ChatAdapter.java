package com.vantagecircle.chatapp.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vantagecircle.chatapp.R;
import com.vantagecircle.chatapp.Support;
import com.vantagecircle.chatapp.data.ConstantM;
import com.vantagecircle.chatapp.model.ChatM;

import java.util.ArrayList;

/**
 * Created by bapidas on 11/07/17.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private Context mContext;
    private ArrayList<ChatM> arrayList;
    private boolean isChatContinue = false;

    public ChatAdapter(Context mContext, ArrayList<ChatM> arrayList) {
        this.mContext = mContext;
        this.arrayList = arrayList;
    }

    @Override
    public int getItemViewType(int position) {
        //check continue chat
        ChatM model = arrayList.get(position);
        ChatM modelPrevious = arrayList.get(position);
        if (position > 0) {
            modelPrevious = arrayList.get(position - 1);
        }
        isChatContinue = model.getReceiverName().equals(modelPrevious.getReceiverName()) && position > 0;
        return super.getItemViewType(position);
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        try {
            ChatM chatM = arrayList.get(position);
            holder.userName.setText(chatM.getSenderName());
            holder.messageText.setText(chatM.getMessageText());
            holder.dateTime.setText(ConstantM.getTimeAgo(chatM.getTimeStamp()));
            if (isChatContinue) {
                holder.userName.setVisibility(View.GONE);
            } else {
                holder.userName.setVisibility(View.VISIBLE);
            }
            if(chatM.isSentSuccessfully()){
                holder.statusImage.setImageResource(R.drawable.tick_icon);
            } else {
                holder.statusImage.setImageResource(R.drawable.ic_msg_wait);
            }
            if (chatM.getSenderUid().equals(Support.id)) {
                holder.userName.setTextColor(ContextCompat.getColor(mContext, R.color.colorOrange));
                holder.lyt_parent.setPadding(100, 10, 15, 10);
                holder.lyt_parent.setGravity(Gravity.END);
                holder.lyt_thread.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.chat_background));
            } else {
                holder.userName.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
                holder.statusImage.setVisibility(View.GONE);
                holder.lyt_parent.setPadding(15, 10, 100, 10);
                holder.lyt_parent.setGravity(Gravity.START);
                holder.lyt_thread.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toggleStatus(int position){
        ChatM chatM = arrayList.get(position);
        if (chatM.isSentSuccessfully()) {
            chatM.setSentSuccessfully(false);
        } else {
            chatM.setSentSuccessfully(true);
        }
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView userName, messageText, dateTime;
        ImageView statusImage;
        CardView lyt_thread;
        LinearLayout lyt_parent;

        ChatViewHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.sender);
            messageText = (TextView) itemView.findViewById(R.id.text_content);
            dateTime = (TextView) itemView.findViewById(R.id.text_time);
            statusImage = (ImageView) itemView.findViewById(R.id.chat_status);
            lyt_parent = (LinearLayout) itemView.findViewById(R.id.lyt_parent);
            lyt_thread = (CardView) itemView.findViewById(R.id.lyt_thread);
        }
    }
}
