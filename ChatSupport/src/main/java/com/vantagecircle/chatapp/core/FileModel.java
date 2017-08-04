package com.vantagecircle.chatapp.core;

import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StreamDownloadTask;
import com.google.firebase.storage.UploadTask;

/**
 * Created by bapidas on 04/08/17.
 */

public class FileModel {
    private FileDownloadTask.TaskSnapshot fileDownloadSnap;
    private UploadTask.TaskSnapshot uploadTaskSnap;
    private StorageMetadata storageMetadata;
    private Exception exception;
    private String extraString;

    public FileDownloadTask.TaskSnapshot getFileDownloadSnap() {
        return fileDownloadSnap;
    }

    public void setFileDownloadSnap(FileDownloadTask.TaskSnapshot fileDownloadSnap) {
        this.fileDownloadSnap = fileDownloadSnap;
    }

    public UploadTask.TaskSnapshot getUploadTaskSnap() {
        return uploadTaskSnap;
    }

    public void setUploadTaskSnap(UploadTask.TaskSnapshot uploadTaskSnap) {
        this.uploadTaskSnap = uploadTaskSnap;
    }

    public StorageMetadata getStorageMetadata() {
        return storageMetadata;
    }

    public void setStorageMetadata(StorageMetadata storageMetadata) {
        this.storageMetadata = storageMetadata;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public String getExtraString() {
        return extraString;
    }

    public void setExtraString(String extraString) {
        this.extraString = extraString;
    }
}
