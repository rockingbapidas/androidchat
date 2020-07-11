package com.bapidas.chattingapp.ui.main

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import com.bapidas.chattingapp.ChatApplication
import com.bapidas.chattingapp.R
import com.bapidas.chattingapp.data.core.AuthHandler
import com.bapidas.chattingapp.data.core.GetDataHandler
import com.bapidas.chattingapp.data.core.callbacks.ResultInterface
import com.bapidas.chattingapp.data.core.callbacks.ValueInterface
import com.bapidas.chattingapp.data.core.model.DataModel
import com.bapidas.chattingapp.data.model.User
import com.bapidas.chattingapp.data.pref.SharedPrefHelper
import com.bapidas.chattingapp.utils.Constants
import com.bapidas.chattingapp.utils.ToolsUtils.hideKeyboard
import com.bapidas.chattingapp.utils.ToolsUtils.isNetworkAvailable
import com.bapidas.chattingapp.utils.UpdateKeyUtils.updateTokenToServer

/**
 * Created by bapidas on 10/07/17.
 */
class LoginActivity : AppCompatActivity() {
    lateinit var usernameEdit: EditText
    lateinit var passwordEdit: EditText
    lateinit var btnLogin: Button
    lateinit var btnSignup: Button
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initView()
        initListener()
    }

    private fun initView() {
        usernameEdit = findViewById<View>(R.id.usernameEdit) as AppCompatEditText
        passwordEdit = findViewById<View>(R.id.passwordEdit) as AppCompatEditText
        btnLogin = findViewById<View>(R.id.btnLogin) as Button
        btnSignup = findViewById<View>(R.id.btnSignup) as Button
    }

    private fun initListener() {
        btnLogin.setOnClickListener {
            if (usernameEdit.text.toString().isEmpty() &&
                    passwordEdit.text.toString().isEmpty()) {
                Toast.makeText(this, "Username and password is cannot be blank",
                        Toast.LENGTH_SHORT).show()
            } else if (usernameEdit.text.toString().isEmpty()) {
                Toast.makeText(this, "Username cannot be blank",
                        Toast.LENGTH_SHORT).show()
            } else if (passwordEdit.text.toString().isEmpty()) {
                Toast.makeText(this, "Password cannot be blank",
                        Toast.LENGTH_SHORT).show()
            } else {
                if (isNetworkAvailable(this)) {
                    hideKeyboard(this)
                    progressDialog = ProgressDialog(this)
                    progressDialog?.setMessage("Authenticating user")
                    progressDialog?.show()
                    userLogin()
                } else {
                    Toast.makeText(this, "Please check your internet connection",
                            Toast.LENGTH_SHORT).show()
                }
            }
        }
        btnSignup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }

    private fun userLogin() {
        val username = usernameEdit.text.toString()
        val password = passwordEdit.text.toString()
        val authHandler = AuthHandler()
        authHandler.firebaseAuth = ChatApplication.applicationContext().authInstance
        authHandler.performLogin(username, password, object : ResultInterface {
            override fun onSuccess(t: String) {
                progressDialog?.setMessage("Authentication success")
                updateTokenToServer(SharedPrefHelper(this@LoginActivity)
                        .getString(Constants.FIREBASE_TOKEN))
                data
            }

            override fun onFail(e: String) {
                if (progressDialog != null && progressDialog?.isShowing == true) {
                    progressDialog?.dismiss()
                }
                Toast.makeText(this@LoginActivity, e, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private val data: Unit
        get() {
            val getDataHandler = GetDataHandler()
            val ref = ChatApplication.applicationContext().userReference
                    .child(ChatApplication.applicationContext().userInstance?.uid.orEmpty())
            getDataHandler.setSingleValueEventListener(ref, object : ValueInterface {
                override fun onDataSuccess(dataModel: DataModel) {
                    val userM = dataModel.dataSnapshot?.getValue(User::class.java)
                    if (userM != null) {
                        ChatApplication.applicationContext().user = userM
                        if (progressDialog != null && progressDialog?.isShowing == true) {
                            progressDialog?.dismiss()
                        }
                        if (userM.userType == Constants.ADMIN) {
                            ChatApplication.applicationContext().authInstance.signOut()
                            Toast.makeText(this@LoginActivity,
                                    "User invalid use another account",
                                    Toast.LENGTH_SHORT).show()
                        } else {
                            val intent = Intent(this@LoginActivity, UserActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        ChatApplication.applicationContext().authInstance.signOut()
                        if (progressDialog != null && progressDialog?.isShowing == true) {
                            progressDialog?.dismiss()
                        }
                        Toast.makeText(this@LoginActivity, "User data fetch error try again",
                                Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onDataCancelled(dataModel: DataModel) {
                    ChatApplication.applicationContext().authInstance.signOut()
                    if (progressDialog != null && progressDialog?.isShowing == true) {
                        progressDialog?.dismiss()
                    }
                    Toast.makeText(this@LoginActivity, dataModel.databaseError?.message,
                            Toast.LENGTH_SHORT).show()
                }
            })
        }
}