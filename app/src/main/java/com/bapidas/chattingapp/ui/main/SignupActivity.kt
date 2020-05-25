package com.bapidas.chattingapp.ui.main

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import com.bapidas.chattingapp.ChatApplication
import com.bapidas.chattingapp.R
import com.bapidas.chattingapp.data.core.AuthHandler
import com.bapidas.chattingapp.data.core.SetDataHandler
import com.bapidas.chattingapp.data.core.callbacks.ResultInterface
import com.bapidas.chattingapp.data.model.RoomM
import com.bapidas.chattingapp.data.model.UserM
import com.bapidas.chattingapp.data.pref.SharedPrefM
import com.bapidas.chattingapp.utils.Constants
import com.bapidas.chattingapp.utils.ToolsUtils.hideKeyboard
import com.bapidas.chattingapp.utils.ToolsUtils.isNetworkAvailable
import com.google.firebase.iid.FirebaseInstanceId
import java.util.*

/**
 * Created by bapidas on 10/07/17.
 */
class SignupActivity : AppCompatActivity() {
    lateinit var usernameEdit: EditText
    lateinit var passwordEdit: EditText
    lateinit var fullNameEdit: EditText
    lateinit var btnSignup: Button

    private var username: String = ""
    private var password: String = ""
    private var fullName: String = ""

    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        initView()
        initListener()
    }

    private fun initView() {
        usernameEdit = findViewById<View>(R.id.usernameEdit) as AppCompatEditText
        passwordEdit = findViewById<View>(R.id.passwordEdit) as AppCompatEditText
        fullNameEdit = findViewById<View>(R.id.fullNameEdit) as AppCompatEditText
        btnSignup = findViewById<View>(R.id.btnSignup) as Button
    }

    private fun initListener() {
        btnSignup.setOnClickListener {
            if (usernameEdit.text.toString().isEmpty() && passwordEdit.text.toString().isEmpty()
                    && fullNameEdit.text.toString().isEmpty()) {
                Toast.makeText(this, "All Details is cannot be blank",
                        Toast.LENGTH_SHORT).show()
            } else if (usernameEdit.text.toString().isEmpty()) {
                Toast.makeText(this, "Username cannot be blank",
                        Toast.LENGTH_SHORT).show()
            } else if (passwordEdit.text.toString().isEmpty()) {
                Toast.makeText(this, "Password cannot be blank",
                        Toast.LENGTH_SHORT).show()
            } else if (fullNameEdit.text.toString().isEmpty()) {
                Toast.makeText(this, "Full name cannot be blank",
                        Toast.LENGTH_SHORT).show()
            } else {
                if (isNetworkAvailable(this)) {
                    hideKeyboard(this)
                    progressDialog = ProgressDialog(this)
                    progressDialog?.setMessage("Validating fields and data")
                    progressDialog?.show()
                    userSignup()
                } else {
                    Toast.makeText(this, "Please check your internet connection",
                            Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun userSignup() {
        username = usernameEdit.text.toString()
        password = passwordEdit.text.toString()
        fullName = fullNameEdit.text.toString()
        val authHandler = AuthHandler()
        authHandler.firebaseAuth = ChatApplication.applicationContext().authInstance
        authHandler.performSignup(username, password, object : ResultInterface {
            override fun onSuccess(t: String) {
                progressDialog?.setMessage("Creating account")
                setupData()
            }

            override fun onFail(e: String) {
                if (progressDialog != null && progressDialog?.isShowing == true) {
                    progressDialog?.dismiss()
                }
                Toast.makeText(this@SignupActivity, e, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupData() {
        val userId = ChatApplication.applicationContext().userInstance?.uid.orEmpty()
        val username = username
        val fullName = fullName
        val fcmToken: String
        if (SharedPrefM(this).getString(Constants.FIREBASE_TOKEN) != null) {
            fcmToken = SharedPrefM(this).getString(Constants.FIREBASE_TOKEN).orEmpty()
        } else {
            fcmToken = FirebaseInstanceId.getInstance().token.orEmpty()
            SharedPrefM(this).saveString(Constants.FIREBASE_TOKEN, fcmToken)
        }
        val lastSeenTime = System.currentTimeMillis()
        val isOnline = true
        val userType = Constants.USER
        val roomMArrayList = staticRooms

        val userM = UserM(userId, username, fullName, fcmToken,
                lastSeenTime = lastSeenTime,
                isOnline = isOnline,
                userType = userType,
                roomMArrayList = roomMArrayList
        )
        val setDataHandler = SetDataHandler()
        setDataHandler.databaseReference = ChatApplication.applicationContext().userReference.child(userM.userId)
        setDataHandler.insertData(userM, object : ResultInterface {
            override fun onSuccess(t: String) {
                ChatApplication.applicationContext().userM = userM
                if (progressDialog != null && progressDialog?.isShowing == true) {
                    progressDialog?.dismiss()
                }
                Toast.makeText(this@SignupActivity, "Account is created successfully",
                        Toast.LENGTH_SHORT).show()
                val intent = Intent(this@SignupActivity, UserActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
            }

            override fun onFail(e: String) {
                ChatApplication.applicationContext().userInstance?.delete()
                if (progressDialog != null && progressDialog?.isShowing == true) {
                    progressDialog?.dismiss()
                }
                Toast.makeText(this@SignupActivity, e, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private val staticRooms: ArrayList<RoomM>
        get() {
            val arrayList = ArrayList<RoomM>()
            val roomId = "CAOL5K"
            val roomName = "CAOL5K_QWERTY"
            arrayList.add(RoomM(roomId, roomName))
            return arrayList
        }
}