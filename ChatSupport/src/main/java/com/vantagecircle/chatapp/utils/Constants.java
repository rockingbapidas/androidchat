package com.vantagecircle.chatapp.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by bapidas on 10/07/17.
 */

public class Constants {
    public static final String APPNAME = "ChatApp";
    static final String SDCARD_PATH = Environment.getExternalStorageDirectory() + File.separator + APPNAME;
    public static final String DIR_SENT = "Sent";
    static final String DIR_RECEIVED = "Received";
    public static final String APP_PREFS = "preferences";

    public static final String DATABASE_USER_REF = "users";
    public static final String DATABASE_CHAT_REF = "chat_msg";
    public static final String DATABASE_GROUP_REF = "groups";
    public static final String STORAGE_CHAT_IMAGE_REF = "chat_img/";

    public static final String _ADMIN = "admin";
    public static final String _USER = "user";
    public static final String NO_ROOM = "noRoom";
    public static final String CONV_GR = "group";
    public static final String CONV_SN = "one";

    public static final String FIREBASE_TOKEN = "fcmToken";
    static final String ONLINE_STATUS = "online";
    static final String LAST_SEEN = "lastSeenTime";
    static final String SENT_STATUS = "sentSuccessfully";
    static final String READ_STATUS = "readSuccessfully";
    static final String FILE_URL = "fileUrl";

    public static final String IMAGE_CONTENT = "image/jpeg";
    public static final String TEXT_CONTENT = "text";

    public static final String IMAGE = "Image";
    public static final String VIDEO = "Video";
    public static final String FILE = "File";
    public static final String GALLERY = "Gallery";

    public static final int REQUEST_STORAGE_PERMISSION = 1;
    public static final int REQUEST_CAMERA_PERMISSION = 2;

    public static final int ACTIVITY_SELECT_FILE = 10;
    public static final int ACTIVITY_SELECT_GALLERY = 11;
    public static final int ACTIVITY_SELECT_PHOTO = 12;
    public static final int ACTIVITY_SELECT_VIDEO = 13;
}
