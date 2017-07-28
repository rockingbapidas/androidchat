package com.vantagecircle.chatapp.adapter;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.vantagecircle.chatapp.Support;
import com.vantagecircle.chatapp.core.GetChild;
import com.vantagecircle.chatapp.holder.UserMViewHolder;
import com.vantagecircle.chatapp.interfacePref.ClickUser;
import com.vantagecircle.chatapp.model.UserM;

/**
 * Created by bapidas on 21/07/17.
 */

public class UsersMAdapter extends FirebaseRecyclerAdapter<UserM, UserMViewHolder> {
    private ClickUser clickUser;

    public UsersMAdapter(Class<UserM> modelClass, int modelLayout, Class<UserMViewHolder> viewHolderClass,
                         Query ref, ClickUser clickUser) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.clickUser = clickUser;
    }

    public UsersMAdapter(Class<UserM> modelClass, int modelLayout, Class<UserMViewHolder> viewHolderClass,
                         DatabaseReference ref, ClickUser clickUser) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.clickUser = clickUser;
    }

    @Override
    protected void populateViewHolder(UserMViewHolder viewHolder, UserM model, int position) {
        viewHolder.setViewHolder(model, clickUser);
    }
}
