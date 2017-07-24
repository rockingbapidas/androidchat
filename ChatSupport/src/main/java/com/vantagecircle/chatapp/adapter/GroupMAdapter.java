package com.vantagecircle.chatapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.vantagecircle.chatapp.R;
import com.vantagecircle.chatapp.Support;
import com.vantagecircle.chatapp.model.GroupM;

/**
 * Created by bapidas on 21/07/17.
 */

public class GroupMAdapter extends FirebaseRecyclerAdapter<GroupM, GroupMAdapter.GroupMViewHolder> {
    private GroupMViewHolder.ClickGroup clickGroup;

    public GroupMAdapter(DatabaseReference ref, GroupMViewHolder.ClickGroup clickGroup) {
        super(GroupM.class, R.layout.row_users, GroupMViewHolder.class, ref);
        this.clickGroup = clickGroup;
    }

    @Override
    protected GroupM parseSnapshot(DataSnapshot snapshot) {
        if(snapshot.child("users").hasChild(Support.id)){
            return super.parseSnapshot(snapshot);
        }
        return null;
    }

    public GroupMAdapter(Query ref, GroupMViewHolder.ClickGroup clickGroup) {
        super(GroupM.class, R.layout.row_users, GroupMViewHolder.class, ref);
        this.clickGroup = clickGroup;
    }

    @Override
    protected void populateViewHolder(GroupMViewHolder viewHolder, GroupM model, int position) {
        viewHolder.setHolderData(model, clickGroup);
    }

    public static class GroupMViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView user_name;
        TextView email_id;
        TextView last_message;
        LinearLayout sub_holder;
        ClickGroup clickGroup;

        GroupMViewHolder(View itemView) {
            super(itemView);
            user_name = (TextView) itemView.findViewById(R.id.user_name);
            email_id = (TextView) itemView.findViewById(R.id.email_id);
            sub_holder = (LinearLayout) itemView.findViewById(R.id.sub_holder);
            last_message = (TextView) itemView.findViewById(R.id.last_message);
            sub_holder.setOnClickListener(this);
        }

        void setHolderData(GroupM groupM, ClickGroup clickGroup){
            if(groupM != null){
                sub_holder.setVisibility(View.VISIBLE);
                itemView.setVisibility(View.VISIBLE);
                this.clickGroup = clickGroup;
                user_name.setText(groupM.getName());
                email_id.setVisibility(View.GONE);
                last_message.setVisibility(View.GONE);
            } else {
                sub_holder.setVisibility(View.GONE);
                itemView.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.sub_holder){
                if(clickGroup != null){
                    clickGroup.onGroupClick(getAdapterPosition());
                }
            }
        }

        public interface ClickGroup{
            void onGroupClick(int position);
        }
    }
}
