package com.vantagecircle.chatapp.holder;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vantagecircle.chatapp.R;
import com.vantagecircle.chatapp.Support;
import com.vantagecircle.chatapp.core.DataModel;
import com.vantagecircle.chatapp.core.GetDataHandler;
import com.vantagecircle.chatapp.core.interfacep.ChildInterface;
import com.vantagecircle.chatapp.core.interfacep.ResultInterface;
import com.vantagecircle.chatapp.utils.ConfigUtils;
import com.vantagecircle.chatapp.utils.Constant;
import com.vantagecircle.chatapp.interfacePref.ClickGroup;
import com.vantagecircle.chatapp.model.ChatM;
import com.vantagecircle.chatapp.model.GroupM;

/**
 * Created by bapidas on 27/07/17.
 */

public class GroupMViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private TextView user_name;
    private TextView email_id;
    private TextView last_message;
    private LinearLayout sub_holder, lastImage;
    private ClickGroup clickGroup;

    GroupMViewHolder(View itemView) {
        super(itemView);
        user_name = (TextView) itemView.findViewById(R.id.user_name);
        email_id = (TextView) itemView.findViewById(R.id.email_id);
        sub_holder = (LinearLayout) itemView.findViewById(R.id.sub_holder);
        lastImage = (LinearLayout) itemView.findViewById(R.id.lastImage);
        last_message = (TextView) itemView.findViewById(R.id.last_message);
        sub_holder.setOnClickListener(this);
    }

    public void setHolderData(GroupM groupM, ClickGroup clickGroup){
        if(groupM != null){
            sub_holder.setVisibility(View.VISIBLE);
            itemView.setVisibility(View.VISIBLE);
            this.clickGroup = clickGroup;
            email_id.setVisibility(View.GONE);
            user_name.setText(groupM.getName());

            getLastMessage(groupM);
        } else {
            sub_holder.setVisibility(View.GONE);
            itemView.setVisibility(View.GONE);
        }
    }

    private void getLastMessage(final GroupM groupM) {
        ConfigUtils.checkRooms(groupM, new ResultInterface() {
            @Override
            public void onSuccess(String t) {
                if(t.equals(Constant.NO_ROOM)){
                    last_message.setVisibility(View.GONE);
                    lastImage.setVisibility(View.GONE);
                } else {
                    bindLastMessage(t);
                }
            }

            @Override
            public void onFail(String e) {
                Log.d("onFail", "Error " + e);
            }
        });
    }

    private void bindLastMessage(String room){
        GetDataHandler getDataHandler1 = new GetDataHandler();
        getDataHandler1.setQueryReference(Support.getChatReference()
                .child(room)
                .orderByKey()
                .limitToLast(1));
        getDataHandler1.setChildValueListener(new ChildInterface() {
            @Override
            public void onChildNew(DataModel dataModel) {
                ChatM chatM = dataModel.getDataSnapshot().getValue(ChatM.class);
                assert chatM != null;
                if(chatM.getChatType().equals(Constant.TEXT_TYPE)){
                    lastImage.setVisibility(View.GONE);
                    last_message.setVisibility(View.VISIBLE);
                    last_message.setText(chatM.getMessageText());
                } else {
                    lastImage.setVisibility(View.VISIBLE);
                    last_message.setVisibility(View.GONE);
                }
            }

            @Override
            public void onChildModified(DataModel dataModel) {
                ChatM chatM = dataModel.getDataSnapshot().getValue(ChatM.class);
                assert chatM != null;
                if(chatM.getChatType().equals(Constant.TEXT_TYPE)){
                    lastImage.setVisibility(View.GONE);
                    last_message.setVisibility(View.VISIBLE);
                    last_message.setText(chatM.getMessageText());
                } else {
                    lastImage.setVisibility(View.VISIBLE);
                    last_message.setVisibility(View.GONE);
                }
            }

            @Override
            public void onChildDelete(DataModel dataModel) {

            }

            @Override
            public void onChildRelocate(DataModel dataModel) {

            }

            @Override
            public void onChildCancelled(DataModel dataModel) {
                Log.d("onChildCancelled", "Error " + dataModel.getDatabaseError().getMessage());
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.sub_holder){
            if(clickGroup != null){
                clickGroup.onGroupClick(getAdapterPosition());
            }
        }
    }
}
