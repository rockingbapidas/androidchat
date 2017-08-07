package com.vantagecircle.chatapp.core;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vantagecircle.chatapp.core.interfaceC.FileInterface;
import com.vantagecircle.chatapp.core.model.FileModel;

import java.io.File;

/**
 * Created by bapidas on 04/08/17.
 */

public class FileHandler {
    private final String TAG = FileHandler.class.getSimpleName();
    private FileModel fileModel;
    private StorageReference storageReference;

    public FileHandler() {
        fileModel = new FileModel();
    }

    public void setStorageRef(StorageReference storageRef) {
        this.storageReference = storageRef;
    }

    public void uploadFile(Uri fileUri, String contentType, final FileInterface fileInterface) {
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType(contentType)
                .build();
        UploadTask uploadTask = storageReference.putFile(fileUri, metadata);

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                fileModel.setUploadTaskSnap(taskSnapshot);
                fileInterface.onProgress(fileModel);
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                fileModel.setUploadTaskSnap(taskSnapshot);
                fileInterface.onPause(fileModel);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                fileModel.setException(exception);
                fileInterface.onFail(fileModel);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileModel.setUploadTaskSnap(taskSnapshot);
                fileInterface.onComplete(fileModel);
            }
        });
    }

    public void downloadFile(File newFile, final FileInterface fileInterface) {
        FileDownloadTask fileDownloadTask = storageReference.getFile(newFile);

        fileDownloadTask.addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                fileModel.setFileDownloadSnap(taskSnapshot);
                fileInterface.onProgress(fileModel);
            }
        }).addOnPausedListener(new OnPausedListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onPaused(FileDownloadTask.TaskSnapshot taskSnapshot) {
                fileModel.setFileDownloadSnap(taskSnapshot);
                fileInterface.onPause(fileModel);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                fileModel.setException(e);
                fileInterface.onFail(fileModel);
            }
        }).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                fileModel.setFileDownloadSnap(taskSnapshot);
                fileInterface.onComplete(fileModel);
            }
        });
    }

    public void getMetaData(final FileInterface fileInterface) {
        storageReference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                fileModel.setStorageMetadata(storageMetadata);
                fileInterface.onComplete(fileModel);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                fileModel.setException(e);
                fileInterface.onFail(fileModel);
            }
        });
    }
}
