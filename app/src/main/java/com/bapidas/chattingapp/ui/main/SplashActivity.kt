package com.bapidas.chattingapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bapidas.chattingapp.ChatApplication
import com.bapidas.chattingapp.R
import com.bapidas.chattingapp.data.core.GetDataHandler
import com.bapidas.chattingapp.data.core.callbacks.ValueInterface
import com.bapidas.chattingapp.data.core.model.DataModel
import com.bapidas.chattingapp.data.model.UserM

/**
 * Created by bapidas on 10/07/17.
 */
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initApplicationMode();
    }

    private fun initApplicationMode() {
        if (ChatApplication.applicationContext().userInstance != null) {
            val getDataHandler = GetDataHandler()
            val ref = ChatApplication.applicationContext().userReference
                    .child(ChatApplication.applicationContext().userInstance?.uid.orEmpty())
            getDataHandler.setSingleValueEventListener(ref, object : ValueInterface {
                override fun onDataSuccess(dataModel: DataModel) {
                    val userM = dataModel.dataSnapshot?.getValue(UserM::class.java)
                    if (userM != null) {
                        ChatApplication.applicationContext().userM = userM
                        val intent = Intent(this@SplashActivity, UserActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        //logout from firebase and try again
                        ChatApplication.applicationContext().authInstance.signOut()
                        Toast.makeText(applicationContext, "User data not found please login",
                                Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }

                override fun onDataCancelled(dataModel: DataModel) {
                    ChatApplication.applicationContext().authInstance.signOut()
                    Toast.makeText(applicationContext, dataModel.databaseError?.message,
                            Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            })
        } else {
            val intent = Intent(this@SplashActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}