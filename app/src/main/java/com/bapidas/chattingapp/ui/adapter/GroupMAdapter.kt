package com.bapidas.chattingapp.ui.adapter

import com.bapidas.chattingapp.ChatApplication
import com.bapidas.chattingapp.data.model.GroupM
import com.bapidas.chattingapp.ui.adapter.callbacks.ClickGroup
import com.bapidas.chattingapp.ui.adapter.holder.GroupMViewHolder
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query

/**
 * Created by bapidas on 21/07/17.
 */
class GroupMAdapter(modelClass: Class<GroupM>, modelLayout: Int,
                    viewHolderClass: Class<GroupMViewHolder>,
                    ref: Query?,
                    private var clickGroup: ClickGroup) :
        FirebaseRecyclerAdapter<GroupM, GroupMViewHolder>(modelClass, modelLayout, viewHolderClass, ref) {

    override fun parseSnapshot(snapshot: DataSnapshot): GroupM? {
        return if (snapshot.child("users")
                        .hasChild(ChatApplication.applicationContext().id.orEmpty())) {
            super.parseSnapshot(snapshot)
        } else null
    }

    override fun populateViewHolder(viewHolder: GroupMViewHolder, model: GroupM?, position: Int) {
        model?.let { viewHolder.setHolderData(it, clickGroup) }
    }
}