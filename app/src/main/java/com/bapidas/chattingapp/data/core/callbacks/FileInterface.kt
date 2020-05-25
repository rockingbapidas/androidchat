package com.bapidas.chattingapp.data.core.callbacks

import com.bapidas.chattingapp.data.core.model.FileModel

/**
 * Created by bapidas on 04/08/17.
 */
interface FileInterface {
    fun onProgress(fileModel: FileModel)
    fun onPause(fileModel: FileModel)
    fun onFail(fileModel: FileModel)
    fun onComplete(fileModel: FileModel)
}