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
import com.vantagecircle.chatapp.core.ChildHandler;
import com.vantagecircle.chatapp.core.DataHandler;
import com.vantagecircle.chatapp.core.ValueHandler;
import com.vantagecircle.chatapp.utils.Constant;
import com.vantagecircle.chatapp.interfacePref.ClickUser;
import com.vantagecircle.chatapp.model.ChatM;
import com.vantagecircle.chatapp.model.UserM;

/**
 * Created by bapidas on 27/07/17.
 */

public class UserMViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private TextView user_name, email_id, last_message;
    private LinearLayout sub_holder, lastImage;
    private ClickUser clickUser;

    UserMViewHolder(View itemView) {
        super(itemView);
        user_name = (TextView) itemView.findViewById(R.id.user_name);
        email_id = (TextView) itemView.findViewById(R.id.email_id);
        sub_holder = (LinearLayout) itemView.findViewById(R.id.sub_holder);
        lastImage = (LinearLayout) itemView.findViewById(R.id.lastImage);
        last_message = (TextView) itemView.findViewById(R.id.last_message);
        sub_holder.setOnClickListener(this);
    }

    public void setViewHolder(UserM userM, ClickUser clickUser) {
        if (!Support.id.equals(userM.getUserId())) {
            sub_holder.setVisibility(View.VISIBLE);
            itemView.setVisibility(View.VISIBLE);
            this.clickUser = clickUser;
            getLastMessage(userM);
        } else {
            sub_holder.setVisibility(View.GONE);
            itemView.setVisibility(View.GONE);
        }
    }

    private void getLastMessage(final UserM userM) {
        final String room_type_1 = Support.id + "_" + userM.getUserId();
        final String room_type_2 = userM.getUserId() + "_" + Support.id;
        ValueHandler valueHandler = new ValueHandler(Support.getChatReference()) {
            @Override
            protected void onDataSuccess(DataSnapshot dataSnapshot) {
                String activeRoom;
                if (dataSnapshot.hasChild(room_type_1)){
                    activeRoom = room_type_1;
                } else if (dataSnapshot.hasChild(room_type_2)){
                    activeRoom = room_type_2;
                } else {
                    activeRoom = null;
                }
                ChildHandler childHandler = null;
                if (activeRoom != null) {
                    childHandler = new ChildHandler(Support.getChatReference()
                            .child(activeRoom)
                            .orderByKey()
                            .limitToLast(1)) {
                        @Override
                        protected void onChildNew(DataSnapshot dataSnapshot, String s) {
                            ChatM chatM = dataSnapshot.getValue(ChatM.class);
                            assert chatM != null;
                            user_name.setText(userM.getFullName());
                            email_id.setText(userM.getUsername());
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
                        protected void onChildModified(DataSnapshot dataSnapshot, String s) {
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
                        protected void onChildDelete(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        protected void onChildRelocate(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        protected void onChildCancelled(DatabaseError databaseError) {

                        }
                    };
                }
                if (childHandler != null) {
                    childHandler.addChildQueryListener();
                }
            }

            @Override
            protected void onDataCancelled(DatabaseError databaseError) {

            }
        };
        valueHandler.addContinueListener();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sub_holder) {
            if (clickUser != null) {
                clickUser.onUserClick(getAdapterPosition());
            }
        }
    }
}
