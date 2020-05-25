package com.bapidas.chattingapp.ui.main

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bapidas.chattingapp.ChatApplication
import com.bapidas.chattingapp.R
import com.bapidas.chattingapp.data.core.SetDataHandler
import com.bapidas.chattingapp.data.core.callbacks.ResultInterface
import com.bapidas.chattingapp.data.model.GroupM
import com.bapidas.chattingapp.data.model.UserM
import com.bapidas.chattingapp.notification.httpcall.SendNotification
import com.bapidas.chattingapp.ui.adapter.GroupMAdapter
import com.bapidas.chattingapp.ui.adapter.UsersMAdapter
import com.bapidas.chattingapp.ui.adapter.callbacks.ClickGroup
import com.bapidas.chattingapp.ui.adapter.callbacks.ClickUser
import com.bapidas.chattingapp.ui.adapter.holder.GroupMViewHolder
import com.bapidas.chattingapp.ui.adapter.holder.UserMViewHolder
import com.bapidas.chattingapp.ui.adapter.decoration.DividerItemDecoration
import com.bapidas.chattingapp.utils.ConfigUtils.isHasPermissions
import com.bapidas.chattingapp.utils.Constants
import com.bapidas.chattingapp.utils.UpdateKeyUtils.updateLastSeen
import com.bapidas.chattingapp.utils.UpdateKeyUtils.updateOnlineStatus
import com.google.firebase.database.Query
import com.google.gson.Gson
import java.util.*

class UserActivity : AppCompatActivity() {
    private var mActionBar: ActionBar? = null
    lateinit var recyclerView: RecyclerView
    lateinit var recyclerView1: RecyclerView
    lateinit var groupTitle: LinearLayout

    private var linearLayoutManager: LinearLayoutManager = LinearLayoutManager(this)
    private var linearLayoutManager1: LinearLayoutManager = LinearLayoutManager(this)

    lateinit var usersMAdapter: UsersMAdapter
    lateinit var groupMAdapter: GroupMAdapter

    private var progressDialog: ProgressDialog? = null
    private var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        initPermission()
        initToolbar()
        initView()
        initRecycler()
        setData()
    }

    private fun initPermission() {
        val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (!isHasPermissions(this, *permissions)) {
            ActivityCompat.requestPermissions(this, permissions,
                    Constants.REQUEST_STORAGE_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission granted")
            } else {
                Log.d(TAG, "Permission not granted")
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun initToolbar() {
        mActionBar = supportActionBar
        mActionBar?.title = ChatApplication.applicationContext().userM?.fullName
    }

    private fun initView() {
        recyclerView = findViewById<View>(R.id.recyclerView) as RecyclerView
        recyclerView1 = findViewById<View>(R.id.recyclerView1) as RecyclerView
        groupTitle = findViewById<View>(R.id.groupTitle) as LinearLayout
    }

    private fun initRecycler() {
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.scrollToPosition(0)
        val animator = recyclerView.itemAnimator
        if (animator is SimpleItemAnimator) {
            animator.supportsChangeAnimations = false
        }
        recyclerView.addItemDecoration(DividerItemDecoration(ContextCompat
                .getDrawable(this, R.drawable.divider)))

        linearLayoutManager1.orientation = LinearLayoutManager.VERTICAL
        recyclerView1.layoutManager = linearLayoutManager1
        recyclerView1.scrollToPosition(0)
        val animator1 = recyclerView1.itemAnimator
        if (animator1 is SimpleItemAnimator) {
            animator1.supportsChangeAnimations = false
        }
        recyclerView1.addItemDecoration(DividerItemDecoration(ContextCompat
                .getDrawable(this, R.drawable.divider)))
    }

    private fun setData() {
        val myQuery: Query = ChatApplication.applicationContext().userReference
        usersMAdapter = UsersMAdapter(UserM::class.java, R.layout.row_users,
                UserMViewHolder::class.java, myQuery, object : ClickUser {
            override fun onUserClick(position: Int) {
                val intent = Intent(this@UserActivity, ChatActivity::class.java)
                intent.putExtra("isFormBar", false)
                intent.putExtra("isGroup", false)
                intent.putExtra("data", Gson().toJson(usersMAdapter.getItem(position)))
                startActivity(intent)
            }
        })
        recyclerView.adapter = usersMAdapter

        val myQuery1: Query = ChatApplication.applicationContext().groupReference
        groupMAdapter = GroupMAdapter(GroupM::class.java, R.layout.row_users,
                GroupMViewHolder::class.java, myQuery1, object : ClickGroup {
            override fun onGroupClick(position: Int) {
                val intent = Intent(this@UserActivity, ChatActivity::class.java)
                intent.putExtra("isFormBar", false)
                intent.putExtra("isGroup", true)
                intent.putExtra("data", Gson().toJson(groupMAdapter.getItem(position)))
                startActivity(intent)
            }
        })
        recyclerView1.adapter = groupMAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val i = item.itemId
        if (i == R.id.action_logout) {
            ChatApplication.applicationContext().authInstance.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        } else if (i == R.id.action_group) {
            showDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(this)
        val layoutInflater = LayoutInflater.from(this)
        val view = layoutInflater.inflate(R.layout.layout_create_group, null)
        val editText = view.findViewById<View>(R.id.groupnameEdit) as EditText
        val btnSubmit = view.findViewById<View>(R.id.btnSubmit) as Button
        btnSubmit.setOnClickListener {
            if (!TextUtils.isEmpty(editText.text.toString())) {
                progressDialog = ProgressDialog(this)
                progressDialog?.setMessage("Please Wait")
                progressDialog?.show()
                val id = ChatApplication.applicationContext().groupReference.push().key.orEmpty()
                val name = editText.text.toString()
                val groupM = GroupM(id, name)
                val setDataHandler = SetDataHandler()
                setDataHandler.databaseReference = ChatApplication.applicationContext().groupReference.child(id)
                setDataHandler.insertData(groupM, object : ResultInterface {
                    override fun onSuccess(t: String) {
                        alertDialog?.dismiss()
                        progressDialog?.dismiss()
                        addUsersToGroup(id)
                        val roomName = id + "_" + name
                        subscribeGroup(roomName)
                    }

                    override fun onFail(e: String) {
                        progressDialog?.dismiss()
                        Toast.makeText(this@UserActivity, e, Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(this, "Enter Group Name",
                        Toast.LENGTH_SHORT).show()
            }
        }
        builder.setCancelable(true)
        builder.setView(view)
        alertDialog = builder.create()
        alertDialog?.show()
    }

    private fun addUsersToGroup(id: String) {
        for (i in 0 until usersMAdapter.itemCount) {
            val (userId, _, _, fcmToken) = usersMAdapter.getItem(i)
            val setDataHandler = SetDataHandler()
            setDataHandler.databaseReference = ChatApplication.applicationContext().groupReference.child(id)
                    .child("users").child(userId).child("fcmToken")
            setDataHandler.insertData(fcmToken, object : ResultInterface {
                override fun onSuccess(t: String) {
                    Log.d(TAG, t)
                }

                override fun onFail(e: String) {
                    Log.e(TAG, e)
                }
            })
        }
        alertDialog?.dismiss()
        progressDialog?.dismiss()
        Toast.makeText(this, "Group created successfully",
                Toast.LENGTH_SHORT).show()
    }

    private fun subscribeGroup(room: String) {
        for (i in 0 until usersMAdapter.itemCount) {
            val (_, _, _, fcmToken) = usersMAdapter.getItem(i)
            SendNotification(this).subscribeTokenToTopic(fcmToken, room)
        }
    }

    override fun onStart() {
        Log.d(TAG, "onStart")
        super.onStart()
        updateOnlineStatus(true)
        updateLastSeen(Date().time)
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()
        updateOnlineStatus(false)
        updateLastSeen(Date().time)
    }

    companion object {
        private val TAG = UserActivity::class.java.simpleName
    }
}