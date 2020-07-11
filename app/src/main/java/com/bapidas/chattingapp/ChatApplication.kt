package com.bapidas.chattingapp

import android.app.Application
import android.os.Environment
import com.bapidas.chattingapp.data.model.User
import com.bapidas.chattingapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class ChatApplication : Application() {
    @get:Synchronized
    @set:Synchronized
    var isChatWindowActive = false

    @get:Synchronized
    @set:Synchronized
    var id: String? = null

    @get:Synchronized
    @set:Synchronized
    var user: User? = null

    @get:Synchronized
    val authInstance: FirebaseAuth
        get() = FirebaseAuth.getInstance()

    @get:Synchronized
    val userInstance: FirebaseUser?
        get() = authInstance.currentUser

    @get:Synchronized
    val databaseInstance: FirebaseDatabase
        get() = FirebaseDatabase.getInstance()

    @get:Synchronized
    val userReference: DatabaseReference
        get() = databaseInstance.getReference(Constants.DATABASE_USER_REF)

    @get:Synchronized
    val chatReference: DatabaseReference
        get() = databaseInstance.getReference(Constants.DATABASE_CHAT_REF)

    @get:Synchronized
    val groupReference: DatabaseReference
        get() = databaseInstance.getReference(Constants.DATABASE_GROUP_REF)

    @get:Synchronized
    val storageInstance: FirebaseStorage
        get() = FirebaseStorage.getInstance()

    @get:Synchronized
    val chatImageReference: StorageReference
        get() = storageInstance.getReference(Constants.STORAGE_CHAT_IMAGE_REF)


    override fun onCreate() {
        super.onCreate()
        instance = this
        databaseInstance.setPersistenceEnabled(true)
        userReference.keepSynced(true)
        chatReference.keepSynced(true)
        makeDir()
    }

    fun makeDir() {
        val appFile = File(Environment.getExternalStorageDirectory().toString()
                + File.separator + Constants.APP_NAME)
        try {
            if (!appFile.isDirectory) {
                appFile.mkdir()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            makeInnerDir()
        }
    }

    private fun makeInnerDir() {
        val sFolder = instance?.resources?.getStringArray(R.array.folders_name)
        if (sFolder != null) {
            for (aS_folder in sFolder) {
                val appFile = File(Environment.getExternalStorageDirectory().toString()
                        + File.separator + Constants.APP_NAME + File.separator + aS_folder)
                try {
                    if (!appFile.isDirectory) {
                        appFile.mkdir()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    companion object {
        private var instance: ChatApplication? = null

        fun applicationContext(): ChatApplication {
            return instance as ChatApplication
        }
    }
}