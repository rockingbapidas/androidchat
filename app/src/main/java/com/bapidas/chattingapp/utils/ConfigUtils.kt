package com.bapidas.chattingapp.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bapidas.chattingapp.ChatApplication
import com.bapidas.chattingapp.R
import com.bapidas.chattingapp.data.core.GetDataHandler
import com.bapidas.chattingapp.data.core.callbacks.ResultInterface
import com.bapidas.chattingapp.data.core.callbacks.ValueInterface
import com.bapidas.chattingapp.data.core.model.DataModel
import com.bapidas.chattingapp.data.model.Group
import com.bapidas.chattingapp.data.model.User
import com.squareup.picasso.Picasso

/**
 * Created by bapidas on 01/08/17.
 */
object ConfigUtils {
    private val TAG = ConfigUtils::class.java.simpleName

    fun checkRooms(user: User, resultInterface: ResultInterface) {
        val getDataHandler = GetDataHandler()
        getDataHandler.setValueEventListener(ChatApplication.applicationContext().chatReference, object : ValueInterface {
            override fun onDataSuccess(dataModel: DataModel) {
                val roomType1 = user.userId + "_" + ChatApplication.applicationContext().userInstance?.uid
                val roomType2 = ChatApplication.applicationContext().userInstance?.uid + "_" + user.userId
                when {
                    dataModel.dataSnapshot?.hasChild(roomType1) == true -> {
                        resultInterface.onSuccess(roomType1)
                    }
                    dataModel.dataSnapshot?.hasChild(roomType2) == true -> {
                        resultInterface.onSuccess(roomType2)
                    }
                    else -> {
                        resultInterface.onSuccess(Constants.NO_ROOM)
                    }
                }
            }

            override fun onDataCancelled(dataModel: DataModel) {
                resultInterface.onFail(dataModel.databaseError?.message.orEmpty())
            }
        })
    }

    fun checkRooms(group: Group, resultInterface: ResultInterface) {
        val getDataHandler = GetDataHandler()
        getDataHandler.setValueEventListener(ChatApplication.applicationContext().chatReference, object : ValueInterface {
            override fun onDataSuccess(dataModel: DataModel) {
                val roomType1 = group.name + "_" + group.id
                if (dataModel.dataSnapshot?.hasChild(roomType1) == true) {
                    resultInterface.onSuccess(roomType1)
                } else {
                    resultInterface.onSuccess(Constants.NO_ROOM)
                }
            }

            override fun onDataCancelled(dataModel: DataModel) {
                resultInterface.onFail(dataModel.databaseError?.message.orEmpty())
            }
        })
    }

    fun createRoom(group: Group): String {
        return group.name + "_" + group.id
    }

    fun createRoom(user: User): String {
        return user.userId + "_" + ChatApplication.applicationContext().userInstance?.uid
    }

    fun initializeApp(context: Context) {
        if (ChatApplication.applicationContext().userInstance != null) {
            val getDataHandler = GetDataHandler()
            val ref = ChatApplication.applicationContext().userReference
                    .child(ChatApplication.applicationContext().userInstance?.uid.orEmpty())
            getDataHandler.setSingleValueEventListener(ref, object : ValueInterface {
                override fun onDataSuccess(dataModel: DataModel) {
                    val userM = dataModel.dataSnapshot?.getValue(User::class.java)
                    if (userM != null) {
                        ChatApplication.applicationContext().user = userM
                    }
                }

                override fun onDataCancelled(dataModel: DataModel) {
                    Log.e(TAG, "initializeApp " + dataModel.databaseError?.message)
                }
            })
        }
    }

    fun loadPicasso(context: Context, file_img: ImageView, path: String) {
        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_insert_photo_black_24dp)
        drawable?.let {
            Picasso.get().load(path).noFade().noPlaceholder().error(it).into(file_img)
        }
    }

    fun getImage(imageUrl: String): Bitmap? {
        return Picasso.get().load(imageUrl).get()
    }

    fun isHasPermissions(context: Context, vararg permissions: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }

    @JvmStatic
    fun callIntent(type: String, act: Activity) {
        val intent: Intent
        when (type) {
            Constants.FILE -> {
                intent = Intent()
                if (Build.VERSION.SDK_INT >= 19) {
                    intent.action = Intent.ACTION_OPEN_DOCUMENT
                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
                    intent.type = "*/*"
                } else {
                    intent.action = Intent.ACTION_GET_CONTENT
                    intent.type = "*/*"
                }
                act.startActivityForResult(intent, Constants.ACTIVITY_SELECT_FILE)
            }
            Constants.IMAGE -> {
                intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                act.startActivityForResult(intent, Constants.ACTIVITY_SELECT_PHOTO)
            }
            Constants.VIDEO -> {
                intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
                act.startActivityForResult(intent, Constants.ACTIVITY_SELECT_VIDEO)
            }
            Constants.GALLERY -> {
                intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                //intent.setType("image/* video/");
                //set only for image
                intent.type = "image/*"
                act.startActivityForResult(intent, Constants.ACTIVITY_SELECT_GALLERY)
            }
        }
    }
}