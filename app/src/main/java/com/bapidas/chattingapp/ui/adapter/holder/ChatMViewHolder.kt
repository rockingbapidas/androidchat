package com.bapidas.chattingapp.ui.adapter.holder

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.bapidas.chattingapp.ChatApplication
import com.bapidas.chattingapp.R
import com.bapidas.chattingapp.data.core.FileHandler
import com.bapidas.chattingapp.data.core.callbacks.FileInterface
import com.bapidas.chattingapp.data.core.model.FileModel
import com.bapidas.chattingapp.data.model.ChatM
import com.bapidas.chattingapp.notification.httpcall.SendNotification
import com.bapidas.chattingapp.utils.ConfigUtils.loadPicasso
import com.bapidas.chattingapp.utils.Constants
import com.bapidas.chattingapp.utils.DateUtils.getTimeAgo
import com.bapidas.chattingapp.utils.MainFileUtils.getFileName
import com.bapidas.chattingapp.utils.MainFileUtils.isExternalLocal
import com.bapidas.chattingapp.utils.MainFileUtils.isFilePresent
import com.bapidas.chattingapp.utils.MainFileUtils.receivedPath
import com.bapidas.chattingapp.utils.MainFileUtils.sentPath
import com.bapidas.chattingapp.utils.UpdateKeyUtils.updateFileUrl
import com.bapidas.chattingapp.utils.UpdateKeyUtils.updateReadStatus
import java.io.File

/**
 * Created by bapidas on 27/07/17.
 */
class ChatMViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val TAG = ChatMViewHolder::class.java.simpleName
    private val context: Context = itemView.context

    private val lytParent: LinearLayout = itemView.findViewById<View>(R.id.lyt_parent) as LinearLayout
    private val lytThread: CardView = itemView.findViewById<View>(R.id.lyt_thread) as CardView
    private val userName: TextView = itemView.findViewById<View>(R.id.sender) as TextView
    private val messageText: TextView = itemView.findViewById<View>(R.id.text_content) as TextView
    private val dateTime: TextView = itemView.findViewById<View>(R.id.text_time) as TextView
    private val statusImage: ImageView = itemView.findViewById<View>(R.id.chat_status) as ImageView

    private var fileImage: ImageView? = null
    private var progressBar: ProgressBar? = null

    fun setDataToViews(chatM: ChatM, isChatContinue: Boolean) {
        when (chatM.chatType) {
            Constants.IMAGE_CONTENT ->  {
                progressBar = itemView.findViewById<View>(R.id.progressBar) as ProgressBar
                fileImage = itemView.findViewById<View>(R.id.image_status) as ImageView

                if (chatM.fileUrl.isNotEmpty()) {
                    if (isExternalLocal(chatM.fileUrl)) {
                        downloadFile(chatM)
                    } else {
                        if (chatM.senderUid == ChatApplication.applicationContext().id) {
                            val file = File(Uri.parse(chatM.fileUrl).path.orEmpty())
                            if (file.exists()) {
                                fileImage?.let { loadPicasso(context, it, chatM.fileUrl) }
                                fileImage?.setOnClickListener { onFileClick(file) }
                                uploadFile(chatM)
                            } else {
                                fileImage?.setImageResource(R.drawable.ic_insert_photo_black_24dp)
                                progressBar?.visibility = View.GONE
                            }
                        } else {
                            progressBar?.visibility = View.VISIBLE
                        }
                    }
                } else {
                    fileImage?.setImageResource(R.drawable.ic_insert_photo_black_24dp)
                    progressBar?.visibility = View.GONE
                }
            }
            else -> messageText.text = chatM.messageText
        }
        userName.text = chatM.senderName
        dateTime.text = getTimeAgo(chatM.timeStamp)
        if (isChatContinue) {
            userName.visibility = View.GONE
        } else {
            userName.visibility = View.VISIBLE
        }

        //change row alignment on basis of user
        if (chatM.senderUid == ChatApplication.applicationContext().id) {
            userName.setTextColor(ContextCompat.getColor(context, R.color.colorOrange))
            lytParent.setPadding(100, 10, 15, 10)
            lytParent.gravity = Gravity.END
            statusImage.visibility = View.VISIBLE
            lytThread.setCardBackgroundColor(ContextCompat.getColor(context, R.color.chat_background))
            if (chatM.isSentSuccessfully) {
                if (chatM.isReadSuccessfully) {
                    statusImage.setImageResource(R.drawable.tick_icon)
                } else {
                    statusImage.setImageResource(R.drawable.single_tick)
                }
            } else {
                statusImage.setImageResource(R.drawable.ic_msg_wait)
            }
        } else {
            userName.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
            statusImage.visibility = View.GONE
            lytParent.setPadding(15, 10, 100, 10)
            lytParent.gravity = Gravity.START
            lytThread.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
            if (!chatM.isReadSuccessfully) {
                if (ChatApplication.applicationContext().isChatWindowActive) {
                    updateReadStatus(chatM.chatRoom, chatM.timeStamp)
                }
            }
        }
    }

    private fun onFileClick(file: File) {
        try {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                val contentUri = FileProvider.getUriForFile(context,
                        context.packageName + ".utils.chatFileProvider", file)
                intent.setDataAndType(contentUri, "*/*")
            } else {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.setDataAndType(Uri.fromFile(file), "*/*")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun downloadFile(chatM: ChatM) {
        val fileHandler = FileHandler()
        val storageReference = ChatApplication.applicationContext().storageInstance
                .getReferenceFromUrl(chatM.fileUrl)
        fileHandler.storageReference = storageReference
        val fileName: String
        fileName = if (chatM.chatType.contains("image")) {
            storageReference.name
        } else {
            ""
        }
        val filepath: String = if (chatM.senderUid == ChatApplication.applicationContext().id) {
            sentPath
        } else {
            receivedPath
        }
        try {
            val file = isFilePresent(filepath, fileName)
            if (file != null) {
                fileImage?.let { loadPicasso(context, it, Uri.fromFile(file).toString()) }
                fileImage?.setOnClickListener { onFileClick(file) }
                progressBar?.visibility = View.GONE
            } else {
                val destination = File(filepath, fileName)
                progressBar?.visibility = View.VISIBLE
                fileHandler.downloadFile(destination, object : FileInterface {
                    override fun onProgress(fileModel: FileModel) {
                        val res = (fileModel.fileDownloadSnap?.bytesTransferred ?: 0) /
                                (fileModel.fileDownloadSnap?.totalByteCount ?: 0)
                        val progress = (100.0 * res)
                        Log.d(TAG, "Download is on progress  === $progress%")
                    }

                    override fun onPause(fileModel: FileModel) {
                        Log.d(TAG, "Download is paused  === ")
                        progressBar?.visibility = View.GONE
                    }

                    override fun onFail(fileModel: FileModel) {
                        fileImage?.setImageResource(R.drawable.ic_insert_photo_black_24dp)
                        progressBar?.visibility = View.GONE
                    }

                    override fun onComplete(fileModel: FileModel) {
                        try {
                            val newFile = isFilePresent(filepath, fileName)
                            if (newFile != null) {
                                fileImage?.let { loadPicasso(context, it, Uri.fromFile(newFile).toString()) }
                                fileImage?.setOnClickListener { onFileClick(newFile) }
                            } else {
                                fileImage?.setImageResource(R.drawable.ic_insert_photo_black_24dp)
                            }
                            progressBar?.visibility = View.GONE
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun uploadFile(chatM: ChatM) {
        val fileHandler = FileHandler()
        val file = Uri.parse(chatM.fileUrl)
        fileHandler.storageReference = ChatApplication.applicationContext()
                .chatImageReference.child(getFileName(context, file))
        progressBar?.visibility = View.VISIBLE
        fileHandler.uploadFile(file, chatM.chatType, object : FileInterface {
            override fun onProgress(fileModel: FileModel) {
                val res = (fileModel.fileDownloadSnap?.bytesTransferred ?: 0) /
                        (fileModel.fileDownloadSnap?.totalByteCount ?: 0)
                val progress = (100.0 * res)
                Log.d(TAG, "Upload is on progress  === $progress%")
            }

            override fun onPause(fileModel: FileModel) {
                Log.d(TAG, "Upload is paused  === ")
                progressBar?.visibility = View.GONE
            }

            override fun onFail(fileModel: FileModel) {
                Log.d(TAG, "Upload is failed  === " + fileModel.exception?.message)
                progressBar?.visibility = View.GONE
            }

            override fun onComplete(fileModel: FileModel) {
                //get file download url
                val downloadUrl = fileModel.uploadTaskSnap?.uploadSessionUri?.path
                if (!downloadUrl.isNullOrEmpty()) {
                    //update file url to the server
                    updateFileUrl(chatM.chatRoom, chatM.timeStamp, downloadUrl)

                    //push notification after file update is complete
                    chatM.fileUrl = downloadUrl
                    val sendNotification = SendNotification(context)
                    sendNotification.prepareNotification(chatM)
                }
            }
        })
    }
}