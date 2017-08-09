package com.vantagecircle.chatapp.utils;

import android.net.Uri;

import com.vantagecircle.chatapp.services.SupportService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by bapidas on 03/08/17.
 */

public class FileUtils {
    private final String TAG = FileUtils.class.getSimpleName();

    private static File getDirectoryPath() {
        return new File(Constants.SDCARD_PATH);
    }

    public static String getSentPath() {
        return getDirectoryPath().getPath() + File.separator + Constants.DIR_SENT;
    }

    public static String getReceivedPath() {
        return getDirectoryPath().getPath() + File.separator + Constants.DIR_RECEIVED;
    }

    public static String isFilePresent(String path, String name) throws Exception  {
        try {
            File fileR = new File(path + File.separator + name);
            if (fileR.exists()) {
                return Uri.fromFile(fileR).toString();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Boolean checkDirectory(String send) throws Exception {
        boolean status;
        File appFile = getDirectoryPath();
        if (appFile.isDirectory()) {
            File innerDir = new File(appFile.getPath() + File.separator + send);
            if (innerDir.isDirectory())
                status = true;
            else {
                innerDir.mkdir();
                status = true;
            }
        } else {
            SupportService.makeDir();
            status = true;
        }
        return status;
    }

    public static Uri copyFile(String path, Uri selectedUri, String fileName) throws Exception {
        if (selectedUri == null)
            return null;
        File source = new File(selectedUri.getPath());
        File destination = new File(path, fileName);
        InputStream in = new FileInputStream(source);
        OutputStream out = new FileOutputStream(destination);
        CopyStream(in, out);
        return Uri.fromFile(destination);
    }

    private static void CopyStream(InputStream in, OutputStream out) throws Exception {
        try {
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
