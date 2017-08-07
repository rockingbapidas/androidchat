package com.vantagecircle.chatapp.core.interfaceC;

import com.vantagecircle.chatapp.core.model.FileModel;

/**
 * Created by bapidas on 04/08/17.
 */

public interface FileInterface {
    void onProgress(FileModel fileModel);
    void onPause(FileModel fileModel);
    void onFail(FileModel fileModel);
    void onComplete(FileModel fileModel);
}