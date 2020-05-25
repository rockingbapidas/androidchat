package com.bapidas.chattingapp.ui.adapter

import com.bapidas.chattingapp.R
import com.bapidas.chattingapp.data.model.ChatM
import com.bapidas.chattingapp.ui.adapter.holder.ChatMViewHolder
import com.bapidas.chattingapp.utils.Constants
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.DatabaseReference

/**
 * Created by bapidas on 20/07/17.
 */
class ChatMAdapter(modelClass: Class<ChatM>,
                   modelLayout: Int,
                   viewHolder: Class<ChatMViewHolder>,
                   reference: DatabaseReference?) :
        FirebaseRecyclerAdapter<ChatM, ChatMViewHolder>(modelClass, modelLayout, viewHolder, reference) {
    private var isChatContinue = false

    override fun getItemViewType(position: Int): Int {
        val model = getItem(position)
        if (model != null) {
            var modelPrevious = getItem(position)
            if (position > 0) {
                modelPrevious = getItem(position - 1)
            }
            if (modelPrevious != null)
                isChatContinue = model.senderName == modelPrevious.senderName && position > 0
            return if (super.getItemViewType(position) == 0) {
                when (model.chatType) {
                    Constants.IMAGE_CONTENT -> R.layout.row_chat_image
                    else -> R.layout.row_chat_text
                }
            } else {
                super.getItemViewType(position)
            }
        }
        return super.getItemViewType(position)
    }

    override fun populateViewHolder(viewHolder: ChatMViewHolder, model: ChatM?, position: Int) {
        model?.let { viewHolder.setDataToViews(it, isChatContinue) }
    }
}