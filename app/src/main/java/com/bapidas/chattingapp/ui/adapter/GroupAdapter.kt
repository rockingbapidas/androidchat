package com.bapidas.chattingapp.ui.adapter

import com.bapidas.chattingapp.ChatApplication
import com.bapidas.chattingapp.data.model.Group
import com.bapidas.chattingapp.ui.adapter.callbacks.ClickGroup
import com.bapidas.chattingapp.ui.adapter.holder.GroupViewHolder
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.Query

/**
 * Created by bapidas on 21/07/17.
 */
class GroupAdapter(modelClass: Class<Group>, modelLayout: Int,
                   viewHolderClass: Class<GroupViewHolder>,
                   ref: Query?,
                   private var clickGroup: ClickGroup) :
        FirebaseRecyclerAdapter<Group, GroupViewHolder>(modelClass, modelLayout, viewHolderClass, ref) {

    override fun parseSnapshot(snapshot: DataSnapshot): Group? {
        return if (snapshot.child("users")
                        .hasChild(ChatApplication.applicationContext().id.orEmpty())) {
            super.parseSnapshot(snapshot)
        } else null
    }

    override fun populateViewHolder(viewHolder: GroupViewHolder, model: Group?, position: Int) {
        model?.let { viewHolder.setHolderData(it, clickGroup) }
    }
}