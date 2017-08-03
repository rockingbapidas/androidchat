package com.vantagecircle.chatapp.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;

import com.vantagecircle.chatapp.Support;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by bapidas on 03/08/17.
 */

public class FileUtils {
    public static File getDirectoryPath() {
        return new File(Constant.SDCARD_PATH);
    }

    public static String getSentPath() {
        return getDirectoryPath().getPath() + File.separator + Constant.DIR_SENT;
    }

    public static String getReceivedPath() {
        return getDirectoryPath().getPath() + File.separator + Constant.DIR_RECEIVED;
    }

    public static Boolean checkDirectory(String send) {
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
            Support.makeDir();
            status = true;
        }
        return status;
    }

    public static Uri copyFile(String path, Uri selectedUri, String fileName) throws IOException {
        if (selectedUri == null)
            return null;
        File source = new File(selectedUri.getPath());
        File destination = new File(path, fileName);
        try {
            byte[] buf = new byte[1024];
            InputStream in = new FileInputStream(source);
            OutputStream out = new FileOutputStream(destination);
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new NullPointerException("Failed to copy");
        }
        return Uri.fromFile(destination);
    }

    public static File isFilePresent(String path, String name) {
        try {
            File fileR = new File(path + File.separator + name);
            if (fileR.exists()) {
                return fileR;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File downloadData(String path, String sourceURL, String filename) {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            if (filename.length() > 0) {
                File destination_file = new File(path, filename);
                if (!destination_file.exists()) {
                    URL source = new URL(sourceURL);
                    HttpURLConnection connection = (HttpURLConnection) source.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    OutputStream out = new FileOutputStream(destination_file);
                    CopyStream(input, out);
                }
                return destination_file;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
