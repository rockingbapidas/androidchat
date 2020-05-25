package com.bapidas.chattingapp.ui.adapter

import com.bapidas.chattingapp.data.model.UserM
import com.bapidas.chattingapp.ui.adapter.callbacks.ClickUser
import com.bapidas.chattingapp.ui.adapter.holder.UserMViewHolder
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query

/**
 * Created by bapidas on 21/07/17.
 */
class UsersMAdapter(modelClass: Class<UserM>, modelLayout: Int,
                    viewHolderClass: Class<UserMViewHolder>,
                    ref: Query?,
                    private var clickUser: ClickUser) :
        FirebaseRecyclerAdapter<UserM, UserMViewHolder>(modelClass, modelLayout, viewHolderClass, ref) {

    override fun populateViewHolder(viewHolder: UserMViewHolder, model: UserM?, position: Int) {
        model?.let { viewHolder.setViewHolder(it, clickUser) }
    }
}