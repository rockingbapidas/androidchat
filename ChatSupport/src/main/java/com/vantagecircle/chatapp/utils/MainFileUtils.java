package com.vantagecircle.chatapp.utils;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.webkit.MimeTypeMap;

import com.vantagecircle.chatapp.services.SupportService;

import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by bapidas on 03/08/17.
 */

public class MainFileUtils {
    private static final String TAG = MainFileUtils.class.getSimpleName();
    private static final boolean DEBUG = false;
    public static final String MIME_TYPE_IMAGE = "image";

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // LocalStorageProvider
            if (isLocalStorageDocument(uri)) {
                // The path is the id
                return DocumentsContract.getDocumentId(uri);
            }
            // ExternalStorageProvider
            else if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        } else if (isExternalLocal(uri.toString())) {
            return uri.toString();
        }
        return null;
    }

    private static boolean isLocalStorageDocument(Uri uri) {
        /*Log.e(TAG, "isLocalStorageDocument = " + LocalStorageProvider.AUTHORITY.equals(uri.getAuthority()));
        return LocalStorageProvider.AUTHORITY.equals(uri.getAuthority());*/
        return false;
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static boolean isExternalLocal(String url) {
        assert url != null;
        return url.startsWith("http://") || url.startsWith("https://");
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                if (DEBUG)
                    DatabaseUtils.dumpCursor(cursor);
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static File getDirectoryPath() {
        return new File(Constants.SDCARD_PATH);
    }

    public static String getSentPath() {
        return getDirectoryPath().getPath() + File.separator + Constants.DIR_SENT;
    }

    public static String getReceivedPath() {
        return getDirectoryPath().getPath() + File.separator + Constants.DIR_RECEIVED;
    }

    public static File isFilePresent(String path, String name) throws Exception {
        try {
            File fileR = new File(path + File.separator + name);
            if (fileR.exists()) {
                return fileR;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static File getFile(String filepath) {
        if (filepath != null) {
            return new File(filepath);
        }
        return null;
    }

    public static String getFileName(Context applicationContext, Uri path) {
        String filepath = null;
        if (path != null) {
            filepath = getPath(applicationContext, path);
        }
        if (getFile(filepath) == null)
            return "";
        return getFile(filepath).getName();
    }

    private static String getExtension(String uri) {
        if (uri == null) {
            return null;
        }
        int dot = uri.lastIndexOf(".");
        if (dot >= 0) {
            return uri.substring(dot);
        } else {
            // No extension.
            return "";
        }
    }

    public static String getMimeType(File file) {
        String extension = getExtension(file.getName());
        if (extension.length() > 0) {
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.substring(1));
        }
        return "application/octet-stream";
    }

    public static Uri createNewFile(Intent data, String mimeType) throws IOException {
        String fileName = MainFileUtils.getDynamicName(mimeType, mimeType);
        final File newFile = new File(getSentPath(), fileName);
        final Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            if (thumbnail != null) {
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            }
            newFile.createNewFile();
            FileOutputStream fo = new FileOutputStream(newFile);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new NullPointerException("File not create");
        }
        File f = new File(newFile.getAbsolutePath());
        return Uri.fromFile(f);
    }

    public static File createNewFile(File uri, String fileName, String dirName) {
        File destination = null;
        if (getDirectoryPath().isDirectory()) {
            destination = new File(getDirectoryPath() + File.separator + dirName, fileName);
            try {
                FileUtils.copyFile(uri, destination);
                return new File(getDirectoryPath() + File.separator + dirName, fileName);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            SupportService.makeDir();
            createNewFile(uri, fileName, dirName);
        }
        return null;
    }

    private static String getDynamicName(String mimeType, String uri) {
        String dynamicName;
        Date d = new Date();
        String FileSeperator = "_";
        String start = mimeType.equals("image") ? "IMG" : mimeType.equals("video") ? "VID" : "APP";
        String ext = MainFileUtils.getExtension(uri);
        if (ext.equals("")) {
            ext = mimeType.equals("image") ? ".jpg" : mimeType.equals("video") ? ".mp4" : ext;
        }
        String date = DateUtils.getYYYY(d) + DateUtils.getMM(d) + DateUtils.getDD(d) + FileSeperator;
        String time = String.valueOf(d.getTime() / 1000);

        dynamicName = start + FileSeperator + date + time + ext;
        return dynamicName;
    }

    public static String getUniqueFile(String filename) {
        String[] values = filename.split("\\.");
        return values[0] + "_" + String.valueOf(new Date().getTime()) + "." + values[1];
    }

    public static String compressImage(String imageUri, Context context) {
        String filename = null;
        try {
            Bitmap scaledBitmap = null;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeFile(imageUri, options);
            int actualHeight = options.outHeight;
            int actualWidth = options.outWidth;
            float maxHeight = 816.0f;
            float maxWidth = 612.0f;
            float imgRatio = actualWidth / actualHeight;
            float maxRatio = maxWidth / maxHeight;

            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight;
                    actualWidth = (int) (imgRatio * actualWidth);
                    actualHeight = (int) maxHeight;
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth;
                    actualHeight = (int) (imgRatio * actualHeight);
                    actualWidth = (int) maxWidth;
                } else {
                    actualHeight = (int) maxHeight;
                    actualWidth = (int) maxWidth;

                }
            }

            options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inTempStorage = new byte[16 * 1024];

            try {
                bmp = BitmapFactory.decodeFile(imageUri, options);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();

            }
            try {
                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();
            }
            float ratioX = actualWidth / (float) options.outWidth;
            float ratioY = actualHeight / (float) options.outHeight;
            float middleX = actualWidth / 2.0f;
            float middleY = actualHeight / 2.0f;
            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
            Canvas canvas = null;
            if (scaledBitmap != null) {
                canvas = new Canvas(scaledBitmap);
            }
            if (canvas != null) {
                canvas.setMatrix(scaleMatrix);
            }
            if (canvas != null) {
                canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2,
                        middleY - bmp.getHeight() / 2,
                        new Paint(Paint.FILTER_BITMAP_FLAG));
            }

            ExifInterface exif;
            try {
                exif = new ExifInterface(imageUri);

                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                } else if (orientation == 3) {
                    matrix.postRotate(180);
                } else if (orientation == 8) {
                    matrix.postRotate(270);
                }
                if (scaledBitmap != null) {
                    scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                            scaledBitmap.getWidth(),
                            scaledBitmap.getHeight(), matrix, true);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileOutputStream out = null;
            filename = imageUri;
            try {
                out = new FileOutputStream(filename);
                if (scaledBitmap != null) {
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filename;
    }

    private static int convertDipToPixels(float dips){
        Resources r = SupportService.getInstance().getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dips, r.getDisplayMetrics());
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public static Bitmap decodeBitmapFromPath(String filePath){
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        scaledBitmap = BitmapFactory.decodeFile(filePath,options);

        options.inSampleSize = calculateInSampleSize(options, convertDipToPixels(150), convertDipToPixels(200));
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inJustDecodeBounds = false;

        scaledBitmap = BitmapFactory.decodeFile(filePath, options);
        return scaledBitmap;
    }
}
