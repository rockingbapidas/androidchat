package com.bapidas.chattingapp.utils

import android.os.Environment
import java.io.File

/**
 * Created by bapidas on 10/07/17.
 */
object Constants {
    const val APP_NAME = "ChatApp"
    @JvmField
    val SDCARD_PATH = Environment.getExternalStorageDirectory().toString() + File.separator + APP_NAME
    const val DIR_SENT = "Sent"
    const val DIR_RECEIVED = "Received"
    const val APP_PREFS = "preferences"
    const val DATABASE_USER_REF = "users"
    const val DATABASE_CHAT_REF = "chat_msg"
    const val DATABASE_GROUP_REF = "groups"
    const val STORAGE_CHAT_IMAGE_REF = "chat_img/"
    const val ADMIN = "admin"
    const val USER = "user"
    const val NO_ROOM = "noRoom"
    const val CONVERSATION_GROUP = "group"
    const val CONVERSATION_SINGLE = "one"
    const val FIREBASE_TOKEN = "fcmToken"
    const val ONLINE_STATUS = "online"
    const val LAST_SEEN = "lastSeenTime"
    const val SENT_STATUS = "sentSuccessfully"
    const val READ_STATUS = "readSuccessfully"
    const val FILE_URL = "fileUrl"
    const val IMAGE_CONTENT = "image/jpeg"
    const val TEXT_CONTENT = "text"
    const val IMAGE = "Image"
    const val VIDEO = "Video"
    const val FILE = "File"
    const val GALLERY = "Gallery"
    const val REQUEST_STORAGE_PERMISSION = 1
    const val REQUEST_CAMERA_PERMISSION = 2
    const val REQUEST_STORAGE_CAMERA_PERMISSION = 3
    const val ACTIVITY_SELECT_FILE = 10
    const val ACTIVITY_SELECT_GALLERY = 11
    const val ACTIVITY_SELECT_PHOTO = 12
    const val ACTIVITY_SELECT_VIDEO = 13
}