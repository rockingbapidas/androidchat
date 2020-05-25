package com.bapidas.chattingapp.utils

import android.annotation.TargetApi
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.DatabaseUtils
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.TypedValue
import android.webkit.MimeTypeMap
import com.bapidas.chattingapp.ChatApplication
import com.bapidas.chattingapp.utils.DateUtils.getDD
import com.bapidas.chattingapp.utils.DateUtils.getMM
import com.bapidas.chattingapp.utils.DateUtils.getYYYY
import org.apache.commons.io.FileUtils
import java.io.*
import java.util.*
import kotlin.math.roundToInt

/**
 * Created by bapidas on 03/08/17.
 */
object MainFileUtils {
    private val TAG = MainFileUtils::class.java.simpleName
    private const val DEBUG = false
    const val MIME_TYPE_IMAGE = "image"
    private val directoryPath: File
        get() = File(Constants.SDCARD_PATH)

    @JvmStatic
    val sentPath: String
        get() = directoryPath.path + File.separator + Constants.DIR_SENT

    @JvmStatic
    val receivedPath: String
        get() = directoryPath.path + File.separator + Constants.DIR_RECEIVED

    @JvmStatic
    @TargetApi(Build.VERSION_CODES.KITKAT)
    fun getPath(context: Context, uri: Uri): String? {
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // LocalStorageProvider
            if (isLocalStorageDocument(uri)) {
                // The path is the id
                return DocumentsContract.getDocumentId(uri)
            } else if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
                // TODO handle non-primary volumes
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        java.lang.Long.valueOf(id))
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val contentUri = when (split[0]) {
                    "image" -> {
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }
                    "video" -> {
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    }
                    "audio" -> {
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    else -> null
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                        split[1]
                )
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            // Return the remote address
            return if (isGooglePhotosUri(uri))
                uri.lastPathSegment
            else
                getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        } else if (isExternalLocal(uri.toString())) {
            return uri.toString()
        }
        return null
    }

    private fun isLocalStorageDocument(uri: Uri): Boolean {
        /*Log.e(TAG, "isLocalStorageDocument = " + LocalStorageProvider.AUTHORITY.equals(uri.getAuthority()));
        return LocalStorageProvider.AUTHORITY.equals(uri.getAuthority());*/
        return false
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    @JvmStatic
    fun isExternalLocal(url: String): Boolean {
        return url.startsWith("http://") || url.startsWith("https://")
    }

    private fun getDataColumn(context: Context, uri: Uri?, selection: String?,
                              selectionArgs: Array<String>?): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = uri?.let {
                context.contentResolver.query(it, projection,
                        selection, selectionArgs, null)
            }
            if (cursor != null && cursor.moveToFirst()) {
                if (DEBUG) DatabaseUtils.dumpCursor(cursor)
                val columnIndex = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(columnIndex)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    @JvmStatic
    @Throws(Exception::class)
    fun isFilePresent(path: String, name: String): File? {
        if (directoryPath.isDirectory) {
            val fileR = File(path + File.separator + name)
            return if (fileR.exists()) {
                fileR
            } else {
                null
            }
        } else {
            ChatApplication.applicationContext().makeDir()
            isFilePresent(path, name)
        }
        return null
    }

    private fun getFile(filepath: String?): File? {
        return filepath?.let { File(it) }
    }

    @JvmStatic
    fun getFileName(applicationContext: Context, path: Uri): String {
        val filepath: String? = getPath(applicationContext, path)
        return if (getFile(filepath) == null)
            ""
        else
            getFile(filepath)?.name.orEmpty()
    }

    private fun getExtension(uri: String): String? {
        val dot = uri.lastIndexOf(".")
        return if (dot >= 0) {
            uri.substring(dot)
        } else {
            // No extension.
            ""
        }
    }

    @JvmStatic
    fun getMimeType(file: File): String? {
        val extension = getExtension(file.name)
        return if (!extension.isNullOrEmpty() && extension.isNotEmpty())
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.substring(1))
        else
            "application/octet-stream"
    }

    @Throws(IOException::class)
    fun createNewFile(data: Intent, mimeType: String): Uri {
        val fileName = getDynamicName(mimeType, mimeType)
        val newFile = File(sentPath, fileName)
        val thumbnail = data.extras?.get("data") as Bitmap?
        try {
            val bytes = ByteArrayOutputStream()
            thumbnail?.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            newFile.createNewFile()
            val fo = FileOutputStream(newFile)
            fo.write(bytes.toByteArray())
            fo.close()
        } catch (e: IOException) {
            e.printStackTrace()
            throw NullPointerException("File not create")
        }
        val f = File(newFile.absolutePath)
        return Uri.fromFile(f)
    }

    @JvmStatic
    fun createNewFile(uri: File, fileName: String, dirName: String): File? {
        val destination: File?
        if (directoryPath.isDirectory) {
            destination = File(directoryPath.toString() + File.separator + dirName, fileName)
            return try {
                FileUtils.copyFile(uri, destination)
                File(directoryPath.toString() + File.separator + dirName, fileName)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else {
            ChatApplication.applicationContext().makeDir()
            createNewFile(uri, fileName, dirName)
        }
        return null
    }

    private fun getDynamicName(mimeType: String, uri: String): String {
        val dynamicName: String
        val d = Date()
        val fileSeperator = "_"
        val start = if (mimeType == "image") "IMG" else if (mimeType == "video") "VID" else "APP"
        var ext = getExtension(uri)
        if (ext == "") {
            ext = if (mimeType == "image") ".jpg" else if (mimeType == "video") ".mp4" else ext
        }
        val date = getYYYY(d) + getMM(d) + getDD(d) + fileSeperator
        val time = (d.time / 1000).toString()
        dynamicName = start + fileSeperator + date + time + ext
        return dynamicName
    }

    @JvmStatic
    fun getUniqueFile(filename: String): String {
        val values = filename.split("\\.").toTypedArray()
        return values[0] + "_" + Date().time.toString() + "." + values[1]
    }

    @JvmStatic
    fun compressImage(imageUri: String, context: Context): String? {
        var filename: String? = null
        try {
            var scaledBitmap: Bitmap? = null
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            var bmp = BitmapFactory.decodeFile(imageUri, options)
            var actualHeight = options.outHeight
            var actualWidth = options.outWidth
            val maxHeight = 816.0f
            val maxWidth = 612.0f
            var imgRatio = actualWidth / actualHeight.toFloat()
            val maxRatio = maxWidth / maxHeight
            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                when {
                    imgRatio < maxRatio -> {
                        imgRatio = maxHeight / actualHeight
                        actualWidth = (imgRatio * actualWidth).toInt()
                        actualHeight = maxHeight.toInt()
                    }
                    imgRatio > maxRatio -> {
                        imgRatio = maxWidth / actualWidth
                        actualHeight = (imgRatio * actualHeight).toInt()
                        actualWidth = maxWidth.toInt()
                    }
                    else -> {
                        actualHeight = maxHeight.toInt()
                        actualWidth = maxWidth.toInt()
                    }
                }
            }
            options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)
            options.inJustDecodeBounds = false
            options.inDither = false
            options.inPurgeable = true
            options.inInputShareable = true
            options.inTempStorage = ByteArray(16 * 1024)
            try {
                bmp = BitmapFactory.decodeFile(imageUri, options)
            } catch (exception: OutOfMemoryError) {
                exception.printStackTrace()
            }
            try {
                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
            } catch (exception: OutOfMemoryError) {
                exception.printStackTrace()
            }
            val ratioX = actualWidth / options.outWidth.toFloat()
            val ratioY = actualHeight / options.outHeight.toFloat()
            val middleX = actualWidth / 2.0f
            val middleY = actualHeight / 2.0f
            val scaleMatrix = Matrix()
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
            var canvas: Canvas? = null
            if (scaledBitmap != null) {
                canvas = Canvas(scaledBitmap)
            }
            canvas?.setMatrix(scaleMatrix)
            canvas?.drawBitmap(bmp, middleX - bmp.width / 2,
                    middleY - bmp.height / 2,
                    Paint(Paint.FILTER_BITMAP_FLAG))
            val exif: ExifInterface
            try {
                exif = ExifInterface(imageUri)
                val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
                val matrix = Matrix()
                if (orientation == 6) {
                    matrix.postRotate(90f)
                } else if (orientation == 3) {
                    matrix.postRotate(180f)
                } else if (orientation == 8) {
                    matrix.postRotate(270f)
                }
                if (scaledBitmap != null) {
                    scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                            scaledBitmap.width,
                            scaledBitmap.height, matrix, true)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            var out: FileOutputStream?
            filename = imageUri
            try {
                out = FileOutputStream(filename)
                scaledBitmap?.compress(Bitmap.CompressFormat.JPEG, 80, out)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return filename
    }

    private fun convertDipToPixels(dips: Float): Int {
        val r = ChatApplication.applicationContext().resources
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dips, r.displayMetrics).toInt()
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options,
                                      reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val heightRatio = (height.toFloat() / reqHeight.toFloat()).roundToInt()
            val widthRatio = (width.toFloat() / reqWidth.toFloat()).roundToInt()
            inSampleSize = heightRatio.coerceAtMost(widthRatio)
        }
        val totalPixels = width * height.toFloat()
        val totalReqPixelsCap = reqWidth * reqHeight * 2.toFloat()
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++
        }
        return inSampleSize
    }

    fun decodeBitmapFromPath(filePath: String): Bitmap? {
        var scaledBitmap: Bitmap?
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        scaledBitmap = BitmapFactory.decodeFile(filePath, options)
        options.inSampleSize = calculateInSampleSize(options,
                convertDipToPixels(150f),
                convertDipToPixels(200f))
        options.inDither = false
        options.inPurgeable = true
        options.inInputShareable = true
        options.inJustDecodeBounds = false
        scaledBitmap = BitmapFactory.decodeFile(filePath, options)
        return scaledBitmap
    }
}