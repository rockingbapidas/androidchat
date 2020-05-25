package com.bapidas.chattingapp.ui.main

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bapidas.chattingapp.ChatApplication
import com.bapidas.chattingapp.R
import com.bapidas.chattingapp.data.core.GetDataHandler
import com.bapidas.chattingapp.data.core.SetDataHandler
import com.bapidas.chattingapp.data.core.callbacks.ChildInterface
import com.bapidas.chattingapp.data.core.callbacks.ResultInterface
import com.bapidas.chattingapp.data.core.callbacks.ValueInterface
import com.bapidas.chattingapp.data.core.model.DataModel
import com.bapidas.chattingapp.data.model.ChatM
import com.bapidas.chattingapp.data.model.GroupM
import com.bapidas.chattingapp.data.model.UserM
import com.bapidas.chattingapp.notification.httpcall.SendNotification
import com.bapidas.chattingapp.ui.adapter.ChatMAdapter
import com.bapidas.chattingapp.ui.adapter.holder.ChatMViewHolder
import com.bapidas.chattingapp.utils.ConfigUtils.callIntent
import com.bapidas.chattingapp.utils.ConfigUtils.checkRooms
import com.bapidas.chattingapp.utils.ConfigUtils.createRoom
import com.bapidas.chattingapp.utils.ConfigUtils.initializeApp
import com.bapidas.chattingapp.utils.ConfigUtils.isHasPermissions
import com.bapidas.chattingapp.utils.Constants
import com.bapidas.chattingapp.utils.MainFileUtils
import com.bapidas.chattingapp.utils.MainFileUtils.compressImage
import com.bapidas.chattingapp.utils.MainFileUtils.createNewFile
import com.bapidas.chattingapp.utils.MainFileUtils.getFileName
import com.bapidas.chattingapp.utils.MainFileUtils.getMimeType
import com.bapidas.chattingapp.utils.MainFileUtils.getPath
import com.bapidas.chattingapp.utils.MainFileUtils.getUniqueFile
import com.bapidas.chattingapp.utils.UpdateKeyUtils.updateLastSeen
import com.bapidas.chattingapp.utils.UpdateKeyUtils.updateOnlineStatus
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_chat.*
import java.io.File
import java.util.*

/**
 * Created by bapidas on 27/07/17.
 */
class ChatActivity : AppCompatActivity() {
    private var mActionBar: ActionBar? = null
    private var userM: UserM? = null
    private var groupM: GroupM? = null

    private var currentRoom: String = ""
    private var isGroup = false
    private var isFromNotification = false
    private var tokens: ArrayList<String> = arrayListOf()

    private lateinit var chatMAdapter: ChatMAdapter

    //get all messages from firebase db and bind
    private val chatHistory: Unit
        get() {
            if (isGroup) {
                groupM?.let {
                    checkRooms(it, object : ResultInterface {
                        override fun onSuccess(t: String) {
                            if (t == Constants.NO_ROOM) {
                                Log.d(TAG, "Current Room created")
                                currentRoom = createRoom(it)
                            } else {
                                Log.d(TAG, "Current Room updated")
                                currentRoom = t
                                createAdapter()
                            }
                        }

                        override fun onFail(e: String) {
                            Log.d(TAG, "Current room Error $e")
                        }
                    })
                }
            } else {
                userM?.let {
                    checkRooms(it, object : ResultInterface {
                        override fun onSuccess(t: String) {
                            if (t == Constants.NO_ROOM) {
                                Log.d(TAG, "Current Room created")
                                createRoom(it)
                            } else {
                                Log.d(TAG, "Current Room updated")
                                currentRoom = t
                                createAdapter()
                            }
                        }

                        override fun onFail(e: String) {
                            Log.d(TAG, "Current room Error $e")
                        }
                    })
                }
            }
        }

    private val onlineStatus: Unit
        get() {
            val getDataHandler = GetDataHandler()
            val ref = ChatApplication.applicationContext().userReference.child(userM?.userId.orEmpty())
            getDataHandler.setValueEventListener(ref, object : ValueInterface {
                override fun onDataSuccess(dataModel: DataModel) {
                    val model = dataModel.dataSnapshot?.getValue(UserM::class.java)
                    if (model != null) {
                        val statusString = model.isOnlineString()
                        mActionBar?.subtitle = statusString
                    }
                }

                override fun onDataCancelled(dataModel: DataModel) {
                    Log.d(TAG, "Error " + dataModel.databaseError?.message)
                    mActionBar?.subtitle = "Offline"
                }
            })
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        initToolBar()
        initRecycler()
        initListener()
        initPermission()
    }

    //cast and bind view from layout
    private fun initToolBar() {
        mActionBar = supportActionBar
        mActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initRecycler() {
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        linearLayoutManager.stackFromEnd = true
        recycler_chat.layoutManager = linearLayoutManager
        recycler_chat.scrollToPosition(0)
        recycler_chat.setHasFixedSize(true)
        val animator = recycler_chat.itemAnimator
        if (animator is SimpleItemAnimator) {
            animator.supportsChangeAnimations = false
        }
    }

    private fun initListener() {
        btn_send_txt.setOnClickListener {
            if (et_message.text.toString().isNotEmpty()) {
                val chat = prepareChatModel(et_message.text.toString(),
                        Constants.TEXT_CONTENT, null)
                if (chat != null)
                    pushMessage(chat)
            } else {
                Toast.makeText(this, "Type some message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initPermission() {
        val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
        if (!isHasPermissions(this, *permissions)) {
            ActivityCompat.requestPermissions(this, permissions,
                    Constants.REQUEST_STORAGE_CAMERA_PERMISSION)
        } else {
            initialize()
        }
    }

    //initialize app for chat
    private fun initialize() {
        //initialize app data and set user online if user came from notification bar
        isFromNotification = intent.getBooleanExtra("isFromBar", false)
        if (isFromNotification) {
            initializeApp(this)
            updateOnlineStatus(true)
            updateLastSeen(Date().time)
        }

        //check intent is group chat or not
        isGroup = intent.getBooleanExtra("isGroup", false)
        if (isGroup) {
            groupM = Gson().fromJson(intent.getStringExtra("data"), GroupM::class.java)
            if (groupM != null) {
                mActionBar?.title = groupM?.name
                getTokens()
            }
        } else {
            userM = Gson().fromJson(intent.getStringExtra("data"), UserM::class.java)
            if (userM != null) {
                mActionBar?.title = userM?.fullName
                onlineStatus
            }
        }

        //get all chat history
        chatHistory
    }

    private fun createAdapter() {
        chatMAdapter = ChatMAdapter(ChatM::class.java, 0,
                ChatMViewHolder::class.java,
                ChatApplication.applicationContext().chatReference.child(currentRoom))
        recycler_chat.adapter = chatMAdapter
        chatMAdapter.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                val friendlyMessageCount = chatMAdapter.itemCount
                val lastVisiblePosition = (recycler_chat.layoutManager as LinearLayoutManager)
                        .findLastCompletelyVisibleItemPosition()
                if (lastVisiblePosition == -1 ||
                        positionStart >= friendlyMessageCount - 1 &&
                        lastVisiblePosition == positionStart - 1) {
                    recycler_chat.scrollToPosition(positionStart)
                    recycler_chat.smoothScrollToPosition(positionStart)
                }
            }
        })
    }

    private fun getTokens() {
        tokens = ArrayList()
        val getDataHandler = GetDataHandler()
        val ref = ChatApplication.applicationContext().groupReference
                .child(groupM?.id.orEmpty()).child("users")
        getDataHandler.setChildValueListener(ref, object : ChildInterface {
            override fun onChildNew(dataModel: DataModel) {
                tokens.add(dataModel.dataSnapshot
                        ?.child("fcmToken")
                        ?.value.toString())
            }

            override fun onChildModified(dataModel: DataModel) {

            }

            override fun onChildDelete(dataModel: DataModel) {

            }

            override fun onChildRelocate(dataModel: DataModel) {

            }

            override fun onChildCancelled(dataModel: DataModel) {

            }
        })
    }

    //push message to firebase db
    private fun prepareChatModel(text: String?, type: String?, uri: String?): ChatM? {
        Log.d(TAG, "Current Room === $currentRoom")
        var chatM: ChatM? = null
        try {
            val senderName = ChatApplication.applicationContext().userM?.fullName
            val senderUid = ChatApplication.applicationContext().id
            val senderFcmToken = ChatApplication.applicationContext().userM?.fcmToken
            val receiverName: String?
            val receiverUid: String?
            val conversationType: String
            if (isGroup && groupM != null) {
                receiverName = groupM?.name
                receiverUid = groupM?.id
                conversationType = Constants.CONVERSATION_GROUP
            } else {
                receiverName = userM?.fullName
                receiverUid = userM?.userId
                conversationType = Constants.CONVERSATION_SINGLE
            }
            val timeStamp = Date().time
            chatM = ChatM(senderName.orEmpty(), receiverName.orEmpty(),
                    senderUid.orEmpty(), receiverUid.orEmpty(),
                    type.orEmpty(), text.orEmpty(),
                    uri.orEmpty(), timeStamp, isSentSuccessfully = false,
                    isReadSuccessfully = false, chatRoom = currentRoom,
                    convType = conversationType, senderToken = senderFcmToken.orEmpty(),
                    receiverToken = (if (isGroup && groupM != null) ""
                    else
                        userM?.fcmToken.orEmpty()))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return chatM
    }

    private fun pushMessage(chatM: ChatM) {
        //clear edit text and scroll recycler view to bottom
        et_message.setText("")

        //config handler and push chat data to current room
        val setDataHandler = SetDataHandler()
        setDataHandler.databaseReference = ChatApplication.applicationContext().chatReference
                .child(currentRoom)
                .child(chatM.timeStamp.toString())
        setDataHandler.insertData(chatM, object : ResultInterface {
            override fun onSuccess(t: String) {
                //send push notification to the user if chat type is text type
                if (chatM.chatType == Constants.TEXT_CONTENT) {
                    val sendNotification = SendNotification(this@ChatActivity)
                    sendNotification.prepareNotification(chatM)
                } else {
                    Log.d(TAG, "Notification will be sent after file upload")
                }
            }

            override fun onFail(e: String) {
                Log.d(TAG, "Error $e")
            }
        })
    }

    //file choose config and function
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.attach_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val i = item.itemId
        if (i == android.R.id.home) {
            finish()
        } else if (i == R.id.action_attach) {
            if (currentFocus != null) {
                registerForContextMenu(currentFocus)
                openContextMenu(currentFocus)
                unregisterForContextMenu(currentFocus)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.attach_option_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        try {
            if (item.itemId == R.id.action_choose) {
                val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                if (!isHasPermissions(this, *permissions)) {
                    ActivityCompat.requestPermissions(this,
                            permissions, Constants.REQUEST_STORAGE_PERMISSION)
                } else {
                    callIntent(Constants.FILE, this)
                }
            } else {
                val permissions = arrayOf(Manifest.permission.CAMERA)
                if (!isHasPermissions(this, *permissions)) {
                    ActivityCompat.requestPermissions(this,
                            permissions, Constants.REQUEST_CAMERA_PERMISSION)
                } else {
                    callIntent(Constants.IMAGE, this)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return super.onContextItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Constants.REQUEST_STORAGE_PERMISSION -> try {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callIntent(Constants.FILE, this)
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                    permissions[0])) {
                        Toast.makeText(this,
                                "Gallery cannot be opened without this permission",
                                Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Constants.REQUEST_CAMERA_PERMISSION -> try {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callIntent(Constants.IMAGE, this)
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                    permissions[0])) {
                        Toast.makeText(this,
                                "Camera cannot be opened without this permission",
                                Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Constants.REQUEST_STORAGE_CAMERA_PERMISSION -> try {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    initialize()
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                    permissions[0])) {
                        Toast.makeText(this,
                                "Please give storage permissions from settings",
                                Toast.LENGTH_SHORT).show()
                    }
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                    permissions[1])) {
                        Toast.makeText(this,
                                "Please give camera permissions from settings",
                                Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null) return
        if (resultCode == Activity.RESULT_OK) {
            if (data.extras == null || data.data != null) {
                try {
                    val uri = data.data
                    if (uri != null) {
                        val mimeType = contentResolver.getType(uri)
                        if (mimeType == null) {
                            Toast.makeText(this, "File type is not supported",
                                    Toast.LENGTH_SHORT).show()
                            return
                        }
                        val path = getPath(this, uri)
                        var file: File? = null
                        if (path != null) {
                            file = File(path)
                        }
                        val fileName = getFileName(this, uri)
                        val newFileName = getUniqueFile(fileName)
                        if (file != null) {
                            val newFile = createNewFile(file, newFileName,
                                    Constants.DIR_SENT)
                            if (newFile != null) {
                                val selectedUri = Uri.fromFile(newFile)
                                if (mimeType.contains("image")) {
                                    selectedUri.path?.let { compressImage(it, this) }
                                    val chatM = prepareChatModel(null, Constants.IMAGE_CONTENT,
                                            selectedUri.toString())
                                    if (chatM != null)
                                        pushMessage(chatM)
                                }
                            } else {
                                Toast.makeText(this, "File error occurred",
                                        Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                try {
                    val uri = createNewFile(data, MainFileUtils.MIME_TYPE_IMAGE)
                    val mimeType = getMimeType(File(uri.path.orEmpty()))
                    if (mimeType == null) {
                        Toast.makeText(this, "File type is not supported",
                                Toast.LENGTH_SHORT).show()
                        return
                    }
                    val path = getPath(this, uri)
                    var file: File? = null
                    if (path != null) {
                        file = File(path)
                    }
                    val fileName = getFileName(this, uri)
                    val newFileName = getUniqueFile(fileName)
                    if (file != null) {
                        val newFile = createNewFile(file, newFileName,
                                Constants.DIR_SENT)
                        if (newFile != null) {
                            val selectedUri = Uri.fromFile(newFile)
                            if (mimeType.contains("image")) {
                                selectedUri.path?.let { compressImage(it, this) }
                                val chatM = prepareChatModel(null, Constants.IMAGE_CONTENT,
                                        selectedUri.toString())
                                if (chatM != null)
                                    pushMessage(chatM)
                            }
                        } else {
                            Toast.makeText(this, "File error occurred",
                                    Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    //handle activity life cycle
    override fun onResume() {
        Log.d(TAG, "onResume")
        super.onResume()
    }

    override fun onPause() {
        Log.d(TAG, "onPause")
        super.onPause()
    }

    override fun onStart() {
        Log.d(TAG, "onStart")
        super.onStart()
        ChatApplication.applicationContext().isChatWindowActive = true
    }

    override fun onStop() {
        Log.d(TAG, "onStop")
        super.onStop()
        ChatApplication.applicationContext().isChatWindowActive = false
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()
        if (isFromNotification) {
            updateOnlineStatus(false)
            updateLastSeen(Date().time)
        }
    }

    companion object {
        private val TAG = ChatActivity::class.java.simpleName
    }
}