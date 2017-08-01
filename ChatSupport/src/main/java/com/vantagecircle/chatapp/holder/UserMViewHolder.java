package com.vantagecircle.chatapp.holder;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.vantagecircle.chatapp.R;
import com.vantagecircle.chatapp.Support;
import com.vantagecircle.chatapp.core.DataModel;
import com.vantagecircle.chatapp.core.SetDataHandler;
import com.vantagecircle.chatapp.core.GetDataHandler;
import com.vantagecircle.chatapp.core.interfacep.ChildInterface;
import com.vantagecircle.chatapp.core.interfacep.ValueInterface;
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
        GetDataHandler getDataHandler = new GetDataHandler();
        getDataHandler.setDataReference(Support.getChatReference());
        getDataHandler.setValueEventListener(new ValueInterface() {
            @Override
            public void onDataSuccess(DataModel dataModel) {
                //check room avail
                String activeRoom;
                if (dataModel.getDataSnapshot().hasChild(room_type_1)){
                    activeRoom = room_type_1;
                } else if (dataModel.getDataSnapshot().hasChild(room_type_2)){
                    activeRoom = room_type_2;
                } else {
                    activeRoom = null;
                }
                //set listener
                if (activeRoom != null) {
                    GetDataHandler getDataHandler1 = new GetDataHandler();
                    getDataHandler1.setQueryReference(Support.getChatReference()
                            .child(activeRoom)
                            .orderByKey()
                            .limitToLast(1));
                    getDataHandler1.setChildValueListener(new ChildInterface() {
                        @Override
                        public void onChildNew(DataModel dataModel) {
                            ChatM chatM = dataModel.getDataSnapshot().getValue(ChatM.class);
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
                        public void onChildModified(DataModel dataModel) {
                            ChatM chatM = dataModel.getDataSnapshot().getValue(ChatM.class);
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
                        public void onChildDelete(DataModel dataModel) {

                        }

                        @Override
                        public void onChildRelocate(DataModel dataModel) {

                        }

                        @Override
                        public void onChildCancelled(DataModel dataModel) {
                            Log.d("onDataCancelled", "Error " + dataModel.getDatabaseError().getMessage());
                        }
                    });
                } else {
                    user_name.setText(userM.getFullName());
                    email_id.setText(userM.getUsername());
                    last_message.setVisibility(View.GONE);
                    lastImage.setVisibility(View.GONE);
                }
            }

            @Override
            public void onDataCancelled(DataModel dataModel) {
                Log.d("onDataCancelled", "Error " + dataModel.getDatabaseError().getMessage());
            }
        });
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
