package com.bapidas.chattingapp.ui.adapter.holder

import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bapidas.chattingapp.ChatApplication
import com.bapidas.chattingapp.R
import com.bapidas.chattingapp.data.core.GetDataHandler
import com.bapidas.chattingapp.data.core.callbacks.ChildInterface
import com.bapidas.chattingapp.data.core.callbacks.ResultInterface
import com.bapidas.chattingapp.data.core.model.DataModel
import com.bapidas.chattingapp.data.model.ChatM
import com.bapidas.chattingapp.data.model.UserM
import com.bapidas.chattingapp.ui.adapter.callbacks.ClickUser
import com.bapidas.chattingapp.utils.ConfigUtils.checkRooms
import com.bapidas.chattingapp.utils.Constants

/**
 * Created by bapidas on 27/07/17.
 */
class UserMViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
    private val userName: TextView = itemView.findViewById<View>(R.id.user_name) as TextView
    private val emailId: TextView = itemView.findViewById<View>(R.id.email_id) as TextView
    private val lastMessage: TextView = itemView.findViewById<View>(R.id.last_message) as TextView
    private val subHolder: LinearLayout = itemView.findViewById<View>(R.id.sub_holder) as LinearLayout
    private val lastImage: LinearLayout = itemView.findViewById<View>(R.id.lastImage) as LinearLayout

    private var clickUser: ClickUser? = null

    override fun onClick(v: View) {
        if (v.id == R.id.sub_holder) {
            if (clickUser != null) {
                clickUser?.onUserClick(adapterPosition)
            }
        }
    }

    init {
        subHolder.setOnClickListener(this)
    }

    fun setViewHolder(userM: UserM, clickUser: ClickUser?) {
        if (ChatApplication.applicationContext().id != userM.userId) {
            subHolder.visibility = View.VISIBLE
            itemView.visibility = View.VISIBLE
            this.clickUser = clickUser
            userName.text = userM.fullName
            emailId.text = userM.username
            getLastMessage(userM)
        } else {
            subHolder.visibility = View.GONE
            itemView.visibility = View.GONE
        }
    }

    private fun getLastMessage(userM: UserM) {
        checkRooms(userM, object : ResultInterface {
            override fun onSuccess(t: String) {
                if (t == Constants.NO_ROOM) {
                    lastMessage.visibility = View.GONE
                    lastImage.visibility = View.GONE
                } else {
                    bindLastMessage(t)
                }
            }

            override fun onFail(e: String) {
                Log.d("onFail", "Error $e")
            }
        })
    }

    private fun bindLastMessage(room: String) {
        val getDataHandler1 = GetDataHandler()
        val ref = ChatApplication.applicationContext().chatReference
                .child(room).orderByKey().limitToLast(1)
        getDataHandler1.setChildValueListener(ref, object : ChildInterface {
            override fun onChildNew(dataModel: DataModel) {
                val chatM = dataModel.dataSnapshot?.getValue(ChatM::class.java)
                if (chatM != null) {
                    if (chatM.chatType == Constants.TEXT_CONTENT) {
                        lastImage.visibility = View.GONE
                        lastMessage.visibility = View.VISIBLE
                        lastMessage.text = chatM.messageText
                    } else {
                        lastImage.visibility = View.VISIBLE
                        lastMessage.visibility = View.GONE
                    }
                }
            }

            override fun onChildModified(dataModel: DataModel) {
                val chatM = dataModel.dataSnapshot?.getValue(ChatM::class.java)
                if (chatM != null) {
                    if (chatM.chatType == Constants.TEXT_CONTENT) {
                        lastImage.visibility = View.GONE
                        lastMessage.visibility = View.VISIBLE
                        lastMessage.text = chatM.messageText
                    } else {
                        lastImage.visibility = View.VISIBLE
                        lastMessage.visibility = View.GONE
                    }
                }
            }

            override fun onChildDelete(dataModel: DataModel) {

            }
            override fun onChildRelocate(dataModel: DataModel) {

            }
            override fun onChildCancelled(dataModel: DataModel) {
                Log.d("onChildCancelled", "Error " + dataModel.databaseError?.message)
            }
        })
    }
}