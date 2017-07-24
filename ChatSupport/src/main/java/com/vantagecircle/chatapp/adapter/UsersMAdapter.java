package com.vantagecircle.chatapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.vantagecircle.chatapp.R;
import com.vantagecircle.chatapp.Support;
import com.vantagecircle.chatapp.model.ChatM;
import com.vantagecircle.chatapp.model.UserM;

import java.util.List;

/**
 * Created by bapidas on 21/07/17.
 */

public class UsersMAdapter extends FirebaseRecyclerAdapter<UserM, UsersMAdapter.UsersMViewHolder> {
    private UsersMViewHolder.ClickUser clickUser;

    public UsersMAdapter(DatabaseReference ref, UsersMViewHolder.ClickUser clickUser) {
        super(UserM.class, R.layout.row_users, UsersMViewHolder.class, ref);
        this.clickUser = clickUser;
    }

    public UsersMAdapter(Query ref, UsersMViewHolder.ClickUser clickUser) {
        super(UserM.class, R.layout.row_users, UsersMViewHolder.class, ref);
        this.clickUser = clickUser;
    }

    @Override
    protected void populateViewHolder(UsersMViewHolder viewHolder, UserM model, int position) {
        viewHolder.setViewHolder(model, clickUser);
    }

    public static class UsersMViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView user_name, email_id, last_message;
        LinearLayout sub_holder;
        ClickUser clickUser;

        UsersMViewHolder(View itemView) {
            super(itemView);
            user_name = (TextView) itemView.findViewById(R.id.user_name);
            email_id = (TextView) itemView.findViewById(R.id.email_id);
            sub_holder = (LinearLayout) itemView.findViewById(R.id.sub_holder);
            last_message = (TextView) itemView.findViewById(R.id.last_message);
            sub_holder.setOnClickListener(this);
        }

        void setViewHolder(UserM userM, ClickUser clickUser) {
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

        void getLastMessage(final UserM userM) {
            final String room_type_1 = Support.id + "_" + userM.getUserId();
            final String room_type_2 = userM.getUserId() + "_" + Support.id;
            Support.getChatReference().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(room_type_1)) {
                        Support.getChatReference().child(room_type_1).orderByKey().limitToLast(1)
                                .addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                        ChatM chatM = dataSnapshot.getValue(ChatM.class);
                                        assert chatM != null;
                                        user_name.setText(userM.getFullName());
                                        email_id.setText(userM.getUsername());
                                        last_message.setText(chatM.getMessageText());
                                    }

                                    @Override
                                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                        ChatM chatM = dataSnapshot.getValue(ChatM.class);
                                        assert chatM != null;
                                        last_message.setText(chatM.getMessageText());
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
                    } else if (dataSnapshot.hasChild(room_type_2)) {
                        Support.getChatReference().child(room_type_2).orderByKey().limitToLast(1)
                                .addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                        ChatM chatM = dataSnapshot.getValue(ChatM.class);
                                        assert chatM != null;
                                        user_name.setText(userM.getFullName());
                                        email_id.setText(userM.getUsername());
                                        last_message.setText(chatM.getMessageText());
                                    }

                                    @Override
                                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                        ChatM chatM = dataSnapshot.getValue(ChatM.class);
                                        assert chatM != null;
                                        last_message.setText(chatM.getMessageText());
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
                        user_name.setText(userM.getFullName());
                        email_id.setText(userM.getUsername());
                        last_message.setVisibility(View.GONE);
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
            if (v.getId() == R.id.sub_holder) {
                if (clickUser != null) {
                    clickUser.onUserClick(getAdapterPosition());
                }
            }
        }

        public interface ClickUser {
            void onUserClick(int position);
        }
    }
}
