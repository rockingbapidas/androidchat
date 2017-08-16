package com.vantagecircle.chatapp.holder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.storage.StorageReference;
import com.vantagecircle.chatapp.R;
import com.vantagecircle.chatapp.core.FileHandler;
import com.vantagecircle.chatapp.core.interfaceC.FileInterface;
import com.vantagecircle.chatapp.core.model.FileModel;
import com.vantagecircle.chatapp.model.ChatM;
import com.vantagecircle.chatapp.services.SupportService;
import com.vantagecircle.chatapp.utils.ConfigUtils;
import com.vantagecircle.chatapp.utils.Constants;
import com.vantagecircle.chatapp.utils.DateUtils;
import com.vantagecircle.chatapp.utils.MainFileUtils;
import com.vantagecircle.chatapp.utils.UpdateKeyUtils;

import java.io.File;

/**
 * Created by bapidas on 27/07/17.
 */

public class ChatMViewHolder extends RecyclerView.ViewHolder {
    private final String TAG = ChatMViewHolder.class.getSimpleName();
    private TextView userName, messageText, dateTime;
    private ImageView statusImage, fileImage;
    private CardView lyt_thread;
    private ProgressBar progressBar;
    private LinearLayout lyt_parent;
    private Context context;

    public ChatMViewHolder(View itemView) {
        super(itemView);
        context = itemView.getContext();
        initViews(itemView);
    }

    private void initViews(View itemView) {
        userName = (TextView) itemView.findViewById(R.id.sender);
        messageText = (TextView) itemView.findViewById(R.id.text_content);
        dateTime = (TextView) itemView.findViewById(R.id.text_time);
        statusImage = (ImageView) itemView.findViewById(R.id.chat_status);
        fileImage = (ImageView) itemView.findViewById(R.id.image_status);
        progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        lyt_parent = (LinearLayout) itemView.findViewById(R.id.lyt_parent);
        lyt_thread = (CardView) itemView.findViewById(R.id.lyt_thread);
    }

    public void setDataToViews(ChatM chatM, boolean isChatContinue) {
        switch (chatM.getChatType()) {
            case Constants.IMAGE_CONTENT:
                if (chatM.getFileUrl() != null) {
                    if (MainFileUtils.isExternalLocal(chatM.getFileUrl())) {
                        downloadFile(chatM);
                    } else {
                        if (chatM.getSenderUid().equals(SupportService.id)) {
                            final File file = new File(Uri.parse(chatM.getFileUrl()).getPath());
                            if (file.exists()) {
                                ConfigUtils.loadPicasso(context, fileImage, chatM.getFileUrl());
                                fileImage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        onFileClick(file);
                                    }
                                });
                                uploadFile(chatM);
                            } else {
                                fileImage.setImageResource(R.drawable.ic_insert_photo_black_24dp);
                                progressBar.setVisibility(View.GONE);
                            }
                        } else {
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    fileImage.setImageResource(R.drawable.ic_insert_photo_black_24dp);
                    progressBar.setVisibility(View.GONE);
                }
                break;
            default:
                messageText.setText(chatM.getMessageText());
                break;
        }

        userName.setText(chatM.getSenderName());
        dateTime.setText(DateUtils.getTimeAgo(chatM.getTimeStamp()));
        if (isChatContinue) {
            userName.setVisibility(View.GONE);
        } else {
            userName.setVisibility(View.VISIBLE);
        }

        //change row alignment on basis of user
        if (chatM.getSenderUid().equals(SupportService.id)) {
            userName.setTextColor(ContextCompat.getColor(context, R.color.colorOrange));
            lyt_parent.setPadding(100, 10, 15, 10);
            lyt_parent.setGravity(Gravity.END);
            statusImage.setVisibility(View.VISIBLE);
            lyt_thread.setCardBackgroundColor(ContextCompat.getColor(context, R.color.chat_background));
            if (chatM.isSentSuccessfully()) {
                if (chatM.isReadSuccessfully()) {
                    statusImage.setImageResource(R.drawable.tick_icon);
                } else {
                    statusImage.setImageResource(R.drawable.single_tick);
                }
            } else {
                statusImage.setImageResource(R.drawable.ic_msg_wait);
            }
        } else {
            userName.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            statusImage.setVisibility(View.GONE);
            lyt_parent.setPadding(15, 10, 100, 10);
            lyt_parent.setGravity(Gravity.START);
            lyt_thread.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white));
            if (!chatM.isReadSuccessfully()) {
                if (SupportService.getIsChatWindowActive()) {
                    UpdateKeyUtils.updateReadStatus(chatM.getChatRoom(), chatM.getTimeStamp());
                }
            }
        }
    }

    private void onFileClick(File file) {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(context,
                        context.getPackageName() + ".fileProvider", file);
                intent.setDataAndType(contentUri, "*/*");
            } else {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(file), "*/*");
            }
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void downloadFile(ChatM chatM) {
        FileHandler fileHandler = new FileHandler();
        final StorageReference storageReference = SupportService.getStorageInstance()
                .getReferenceFromUrl(chatM.getFileUrl());
        fileHandler.setStorageRef(storageReference);

        final String filepath;
        final String fileName;
        if (chatM.getChatType().contains("image")) {
            fileName = storageReference.getName();
        } else {
            fileName = "";
        }
        if (chatM.getSenderUid().equals(SupportService.id)) {
            filepath = MainFileUtils.getSentPath();
        } else {
            filepath = MainFileUtils.getReceivedPath();
        }

        try {
            final File file = MainFileUtils.isFilePresent(filepath, fileName);
            if (file != null) {
                ConfigUtils.loadPicasso(context, fileImage, Uri.fromFile(file).toString());
                fileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onFileClick(file);
                    }
                });
                progressBar.setVisibility(View.GONE);
            } else {
                File destination = new File(filepath, fileName);
                progressBar.setVisibility(View.VISIBLE);
                fileHandler.downloadFile(destination, new FileInterface() {
                    @Override
                    public void onProgress(FileModel fileModel) {
                        double progress = (100.0 * fileModel.getFileDownloadSnap().getBytesTransferred())
                                / fileModel.getFileDownloadSnap().getTotalByteCount();
                        Log.d(TAG, "Download is on progress  === " + progress + "%");
                    }

                    @Override
                    public void onPause(FileModel fileModel) {
                        Log.d(TAG, "Download is paused  === ");
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFail(FileModel fileModel) {
                        fileImage.setImageResource(R.drawable.ic_insert_photo_black_24dp);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onComplete(FileModel fileModel) {
                        try {
                            final File file = MainFileUtils.isFilePresent(filepath, fileName);
                            if (file != null) {
                                ConfigUtils.loadPicasso(context, fileImage, Uri.fromFile(file).toString());
                                fileImage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        onFileClick(file);
                                    }
                                });
                            } else {
                                fileImage.setImageResource(R.drawable.ic_insert_photo_black_24dp);
                            }
                            progressBar.setVisibility(View.GONE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadFile(final ChatM chatM) {
        FileHandler fileHandler = new FileHandler();
        Uri file = Uri.parse(chatM.getFileUrl());
        fileHandler.setStorageRef(SupportService.getChatImageReference()
                .child(MainFileUtils.getFileName(context, file)));

        progressBar.setVisibility(View.VISIBLE);
        fileHandler.uploadFile(file, chatM.getChatType(), new FileInterface() {
            @Override
            public void onProgress(FileModel fileModel) {
                double progress = (100.0 * fileModel.getUploadTaskSnap().getBytesTransferred())
                        / fileModel.getUploadTaskSnap().getTotalByteCount();
                Log.d(TAG, "Upload is on progress  === " + progress + "%");
            }

            @Override
            public void onPause(FileModel fileModel) {
                Log.d(TAG, "Upload is paused  === ");
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFail(FileModel fileModel) {
                Log.d(TAG, "Upload is failed  === " + fileModel.getException().getMessage());
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onComplete(FileModel fileModel) {
                //get file download url
                String downloadUrl = fileModel.getUploadTaskSnap().getDownloadUrl().toString();
                //update file url to the server
                UpdateKeyUtils.updateFileUrl(chatM.getChatRoom(), chatM.getTimeStamp(), downloadUrl);

                //push notification after file update is complete
                /*chatM.setFileUrl(downloadUrl);
                SendNotification sendNotification = new SendNotification();
                sendNotification.prepareNotification(chatM);*/
            }
        });
    }
}