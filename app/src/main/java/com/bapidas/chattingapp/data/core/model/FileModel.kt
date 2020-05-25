package com.bapidas.chattingapp.data.core.model

import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.UploadTask

/**
 * Created by bapidas on 04/08/17.
 */
data class FileModel(
        var fileDownloadSnap: FileDownloadTask.TaskSnapshot? = null,
        var uploadTaskSnap: UploadTask.TaskSnapshot? = null,
        var storageMetadata: StorageMetadata? = null,
        var exception: Exception? = null,
        var extraString: String = ""
)