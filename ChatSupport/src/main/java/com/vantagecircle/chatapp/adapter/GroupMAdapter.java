package com.vantagecircle.chatapp.adapter;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.vantagecircle.chatapp.Support;
import com.vantagecircle.chatapp.holder.GroupMViewHolder;
import com.vantagecircle.chatapp.interfacePref.ClickGroup;
import com.vantagecircle.chatapp.model.GroupM;

/**
 * Created by bapidas on 21/07/17.
 */

public class GroupMAdapter extends FirebaseRecyclerAdapter<GroupM, GroupMViewHolder> {
    private ClickGroup clickGroup;

    public GroupMAdapter(Class<GroupM> modelClass, int modelLayout, Class<GroupMViewHolder> viewHolderClass,
                         Query ref, ClickGroup clickGroup) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.clickGroup = clickGroup;
    }

    public GroupMAdapter(Class<GroupM> modelClass, int modelLayout, Class<GroupMViewHolder> viewHolderClass,
                         DatabaseReference ref, ClickGroup clickGroup) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.clickGroup = clickGroup;
    }

    @Override
    protected GroupM parseSnapshot(DataSnapshot snapshot) {
        if(snapshot.child("users").hasChild(Support.id)){
            return super.parseSnapshot(snapshot);
        }
        return null;
    }

    @Override
    protected void populateViewHolder(GroupMViewHolder viewHolder, GroupM model, int position) {
        viewHolder.setHolderData(model, clickGroup);
    }
}
