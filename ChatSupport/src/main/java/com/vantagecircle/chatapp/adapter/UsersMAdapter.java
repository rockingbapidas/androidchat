package com.vantagecircle.chatapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.vantagecircle.chatapp.R;
import com.vantagecircle.chatapp.Support;
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
    protected UserM parseSnapshot(DataSnapshot snapshot) {
        UserM userM = snapshot.getValue(UserM.class);
        if(!Support.id.equals(userM.getUserId())){
            return super.parseSnapshot(snapshot);
        }
        return null;
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

        void setViewHolder(UserM userM, ClickUser clickUser){
            if(userM != null){
                sub_holder.setVisibility(View.VISIBLE);
                itemView.setVisibility(View.VISIBLE);
                this.clickUser = clickUser;
                user_name.setText(userM.getFullName());
                email_id.setText(userM.getUsername());
                if(userM.getNotificationCount() != 0) {
                    last_message.setVisibility(View.VISIBLE);
                    String count = String.valueOf(userM.getNotificationCount()) + " New Message";
                    last_message.setText(count);
                } else {
                    last_message.setVisibility(View.GONE);
                }
            } else {
                sub_holder.setVisibility(View.GONE);
                itemView.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.sub_holder){
                if(clickUser != null){
                    clickUser.onUserClick(getAdapterPosition());
                }
            }
        }

        public interface ClickUser{
            void onUserClick(int position);
        }
    }
}
