package com.vantagecircle.chatapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vantagecircle.chatapp.R;
import com.vantagecircle.chatapp.model.UserM;

import java.util.ArrayList;

/**
 * Created by bapidas on 11/07/17.
 */

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ContactsViewHolder> {
    private Context mContext;
    private ArrayList<UserM> arrayList;
    private ClickListener clickListener;

    public UsersAdapter(Context mContext, ArrayList<UserM> arrayList, ClickListener clickListener) {
        this.mContext = mContext;
        this.arrayList = arrayList;
        this.clickListener = clickListener;
    }

    @Override
    public ContactsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_users, parent, false);
        return new ContactsViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(ContactsViewHolder holder, int position) {
        UserM userM = arrayList.get(position);
        holder.user_name.setText(userM.getFullName());
        holder.email_id.setText(userM.getUsername());
        holder.last_message.setText(userM.getLastMessage());
    }

    public void updateLastMessage(int position, String message){
        UserM m = arrayList.get(position);
        m.setLastMessage(message);
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ContactsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ClickListener clickListener;
        TextView user_name, email_id, last_message;
        LinearLayout sub_holder;

        ContactsViewHolder(View itemView, ClickListener clickListener) {
            super(itemView);
            user_name = (TextView) itemView.findViewById(R.id.user_name);
            email_id = (TextView) itemView.findViewById(R.id.email_id);
            sub_holder = (LinearLayout) itemView.findViewById(R.id.sub_holder);
            last_message = (TextView) itemView.findViewById(R.id.last_message);
            this.clickListener = clickListener;
            sub_holder.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.onItemClick(getAdapterPosition());
            }
        }
    }
}
