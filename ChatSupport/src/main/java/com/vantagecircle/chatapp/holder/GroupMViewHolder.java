package com.vantagecircle.chatapp.holder;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.vantagecircle.chatapp.R;
import com.vantagecircle.chatapp.Support;
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
            getLastMessage(groupM);
        } else {
            sub_holder.setVisibility(View.GONE);
            itemView.setVisibility(View.GONE);
        }
    }

    private void getLastMessage(final GroupM groupM) {
        final String room = groupM.getId() + "_" + groupM.getName();
        Support.getChatReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(room)) {
                    Support.getChatReference().child(room).orderByKey().limitToLast(1)
                            .addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    ChatM chatM = dataSnapshot.getValue(ChatM.class);
                                    assert chatM != null;
                                    user_name.setText(groupM.getName());
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
                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                    ChatM chatM = dataSnapshot.getValue(ChatM.class);
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
                                public void onChildRemoved(DataSnapshot dataSnapshot) {

                                }

                                @Override
                                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                } else {
                    user_name.setText(groupM.getName());
                    last_message.setVisibility(View.GONE);
                    lastImage.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("onCancelled == ", databaseError.getMessage());
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
