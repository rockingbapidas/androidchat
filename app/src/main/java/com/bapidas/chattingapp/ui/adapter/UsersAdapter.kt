package com.bapidas.chattingapp.ui.adapter

import com.bapidas.chattingapp.data.model.User
import com.bapidas.chattingapp.ui.adapter.callbacks.ClickUser
import com.bapidas.chattingapp.ui.adapter.holder.UserViewHolder
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.Query

/**
 * Created by bapidas on 21/07/17.
 */
class UsersAdapter(modelClass: Class<User>, modelLayout: Int,
                   viewHolderClass: Class<UserViewHolder>,
                   ref: Query?,
                   private var clickUser: ClickUser) :
        FirebaseRecyclerAdapter<User, UserViewHolder>(modelClass, modelLayout, viewHolderClass, ref) {

    override fun populateViewHolder(viewHolder: UserViewHolder, model: User?, position: Int) {
        model?.let { viewHolder.setViewHolder(it, clickUser) }
    }
}