package com.vantagecircle.chatapp.holder;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
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
import com.vantagecircle.chatapp.Support;
import com.vantagecircle.chatapp.core.FileHandler;
import com.vantagecircle.chatapp.core.model.FileModel;
import com.vantagecircle.chatapp.core.interfaceC.FileInterface;
import com.vantagecircle.chatapp.services.SendNotification;
import com.vantagecircle.chatapp.utils.Constants;
import com.vantagecircle.chatapp.model.ChatM;
import com.vantagecircle.chatapp.utils.DateUtils;
import com.vantagecircle.chatapp.utils.FileUtils;
import com.vantagecircle.chatapp.utils.ToolsUtils;
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
                    if (chatM.getFileUrl().startsWith("https:") || chatM.getFileUrl().startsWith("gs:")) {
                        downloadFile(chatM);
                    } else {
                        if (chatM.getSenderUid().equals(Support.id)) {
                            ToolsUtils.loadPicasso(Support.getInstance(), fileImage, chatM.getFileUrl());
                            uploadFile(chatM);
                        } else {
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    fileImage.setImageResource(R.drawable.ic_warning_black_24dp);
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
        if (chatM.getSenderUid().equals(Support.id)) {
            userName.setTextColor(ContextCompat.getColor(context, R.color.colorOrange));
            lyt_parent.setPadding(100, 10, 15, 10);
            lyt_parent.setGravity(Gravity.END);
            statusImage.setVisibility(View.VISIBLE);
            lyt_thread.setCardBackgroundColor(ContextCompat.getColor(context, R.color.chat_background));
            if (chatM.isSentSuccessfully()) {
                if (chatM.isReadSuccessfully()) {
                    statusImage.setImageResource(R.drawable.double_tick);
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
                if (Support.getIsChatWindowActive()) {
                    UpdateKeyUtils.updateReadStatus(chatM.getChatRoom(), chatM.getTimeStamp());
                }
            }
        }
    }

    private void downloadFile(ChatM chatM) {
        FileHandler fileHandler = new FileHandler();
        final StorageReference storageReference = Support.getStorageInstance()
                .getReferenceFromUrl(chatM.getFileUrl());
        final String filepath;
        final String fileName = storageReference.getName() + ".jpg";
        if (chatM.getSenderUid().equals(Support.id)) {
            filepath = FileUtils.getSentPath();
        } else {
            filepath = FileUtils.getReceivedPath();
        }

        try {
            String file = FileUtils.isFilePresent(filepath, fileName);
            if (file != null) {
                Log.d(TAG, "File Exist");
                ToolsUtils.loadPicasso(Support.getInstance(), fileImage, file);
                progressBar.setVisibility(View.GONE);
            } else {
                Log.d(TAG, "File Not Exist");
                progressBar.setVisibility(View.VISIBLE);
                File destination = new File(filepath, fileName);
                fileHandler.setStorageRef(storageReference);
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
                    }

                    @Override
                    public void onFail(FileModel fileModel) {
                        fileImage.setImageResource(R.drawable.ic_warning_black_24dp);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onComplete(FileModel fileModel) {
                        try {
                            String file = FileUtils.isFilePresent(filepath, fileName);
                            if (file != null) {
                                ToolsUtils.loadPicasso(Support.getInstance(), fileImage, file);
                            } else {
                                fileImage.setImageResource(R.drawable.ic_warning_black_24dp);
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
        fileHandler.setStorageRef(Support.getChatImageReference()
                .child(String.valueOf(chatM.getTimeStamp())));
        progressBar.setVisibility(View.VISIBLE);

        fileHandler.uploadFile(Uri.parse(chatM.getFileUrl()), chatM.getChatType(), new FileInterface() {
            @Override
            public void onProgress(FileModel fileModel) {
                double progress = (100.0 * fileModel.getUploadTaskSnap().getBytesTransferred())
                        / fileModel.getUploadTaskSnap().getTotalByteCount();
                Log.d(TAG, "Upload is on progress  === " + progress + "%");
            }

            @Override
            public void onPause(FileModel fileModel) {
                Log.d(TAG, "Upload is paused  === ");
            }

            @Override
            public void onFail(FileModel fileModel) {
                progressBar.setVisibility(View.GONE);
                Log.d(TAG, "Upload is failed  === " + fileModel.getException().getMessage());
            }

            @Override
            public void onComplete(FileModel fileModel) {
                String downloadUrl = fileModel.getUploadTaskSnap().getDownloadUrl().toString();
                long timeStamp = Long.parseLong(fileModel.getUploadTaskSnap().getStorage().getName());

                //update file url to the server
                UpdateKeyUtils.updateFileUrl(chatM.getChatRoom(), timeStamp, downloadUrl);

                //push notification after file update is complete
                chatM.setFileUrl(downloadUrl);
                SendNotification sendNotification = new SendNotification();
                sendNotification.prepareNotification(chatM);
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
