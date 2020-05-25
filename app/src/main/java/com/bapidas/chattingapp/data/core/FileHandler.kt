package com.bapidas.chattingapp.data.core

import android.net.Uri
import com.bapidas.chattingapp.data.core.callbacks.FileInterface
import com.bapidas.chattingapp.data.core.model.FileModel
import com.google.firebase.storage.*
import java.io.File

/**
 * Created by bapidas on 04/08/17.
 */
class FileHandler {
    private val TAG = FileHandler::class.java.simpleName
    private val fileModel: FileModel = FileModel()

    lateinit var storageReference: StorageReference

    fun uploadFile(fileUri: Uri, contentType: String, fileInterface: FileInterface) {
        val metadata = StorageMetadata.Builder()
                .setContentType(contentType)
                .build()
        val uploadTask = storageReference.putFile(fileUri, metadata)
        uploadTask.addOnProgressListener { p0 ->
            fileModel.uploadTaskSnap = p0
            fileInterface.onProgress(fileModel)
        }.addOnPausedListener { p0 ->
            fileModel.uploadTaskSnap = p0
            fileInterface.onPause(fileModel)
        }.addOnFailureListener { exception ->
            fileModel.exception = exception
            fileInterface.onFail(fileModel)
        }.addOnSuccessListener { taskSnapshot ->
            fileModel.uploadTaskSnap = taskSnapshot
            fileInterface.onComplete(fileModel)
        }
    }

    fun downloadFile(newFile: File, fileInterface: FileInterface) {
        val fileDownloadTask = storageReference.getFile(newFile)
        fileDownloadTask.addOnProgressListener { p0 ->
            fileModel.fileDownloadSnap = p0
            fileInterface.onProgress(fileModel)
        }.addOnPausedListener { p0 ->
            fileModel.fileDownloadSnap = p0
            fileInterface.onPause(fileModel)
        }.addOnFailureListener { e ->
            fileModel.exception = e
            fileInterface.onFail(fileModel)
        }.addOnSuccessListener { taskSnapshot ->
            fileModel.fileDownloadSnap = taskSnapshot
            fileInterface.onComplete(fileModel)
        }
    }

    fun getMetaData(fileInterface: FileInterface) {
        storageReference.metadata.addOnSuccessListener { storageMetadata ->
            fileModel.storageMetadata = storageMetadata
            fileInterface.onComplete(fileModel)
        }.addOnFailureListener { e ->
            fileModel.exception = e
            fileInterface.onFail(fileModel)
        }
    }

}