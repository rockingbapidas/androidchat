package com.vantagecircle.chatapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vantagecircle.chatapp.R;
import com.vantagecircle.chatapp.model.ContactsM;

import java.util.ArrayList;

/**
 * Created by bapidas on 11/07/17.
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder> {
    private Context mContext;
    private ArrayList<ContactsM> arrayList;
    private ClickListener clickListener;

    public ContactsAdapter(Context mContext, ArrayList<ContactsM> arrayList, ClickListener clickListener) {
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
        ContactsM contactsM = arrayList.get(position);
        holder.user_name.setText(contactsM.getFullName());
        holder.email_id.setText(contactsM.getUsername());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ContactsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ClickListener clickListener;
        TextView user_name, email_id;
        LinearLayout sub_holder;

        ContactsViewHolder(View itemView, ClickListener clickListener) {
            super(itemView);
            user_name = (TextView) itemView.findViewById(R.id.user_name);
            email_id = (TextView) itemView.findViewById(R.id.email_id);
            sub_holder = (LinearLayout) itemView.findViewById(R.id.sub_holder);
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
