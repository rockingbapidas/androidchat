package com.vantagecircle.chatapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.vantagecircle.chatapp.R;
import com.vantagecircle.chatapp.services.SupportService;
import com.vantagecircle.chatapp.core.model.DataModel;
import com.vantagecircle.chatapp.core.GetDataHandler;
import com.vantagecircle.chatapp.core.interfaceC.ResultInterface;
import com.vantagecircle.chatapp.core.interfaceC.ValueInterface;
import com.vantagecircle.chatapp.model.GroupM;
import com.vantagecircle.chatapp.model.UserM;

/**
 * Created by bapidas on 01/08/17.
 */

public class ConfigUtils {
    private static final String TAG = ConfigUtils.class.getSimpleName();

    public static void checkRooms(final UserM userM, final ResultInterface resultInterface) {
        GetDataHandler getDataHandler = new GetDataHandler();
        getDataHandler.setDataReference(SupportService.getChatReference());
        getDataHandler.setValueEventListener(new ValueInterface() {
            @Override
            public void onDataSuccess(DataModel dataModel) {
                String room_type_1 = userM.getUserId() + "_" + SupportService.getUserInstance().getUid();
                String room_type_2 = SupportService.getUserInstance().getUid() + "_" + userM.getUserId();
                if (dataModel.getDataSnapshot().hasChild(room_type_1)) {
                    resultInterface.onSuccess(room_type_1);
                } else if (dataModel.getDataSnapshot().hasChild(room_type_2)) {
                    resultInterface.onSuccess(room_type_2);
                } else {
                    resultInterface.onSuccess(Constants.NO_ROOM);
                }
            }

            @Override
            public void onDataCancelled(DataModel dataModel) {
                resultInterface.onFail(dataModel.getDatabaseError().getMessage());
            }
        });
    }

    public static void checkRooms(final GroupM groupM, final ResultInterface resultInterface) {
        GetDataHandler getDataHandler = new GetDataHandler();
        getDataHandler.setDataReference(SupportService.getChatReference());
        getDataHandler.setValueEventListener(new ValueInterface() {
            @Override
            public void onDataSuccess(DataModel dataModel) {
                String room_type_1 = groupM.getName() + "_" + groupM.getId();
                if (dataModel.getDataSnapshot().hasChild(room_type_1)) {
                    resultInterface.onSuccess(room_type_1);
                } else {
                    resultInterface.onSuccess(Constants.NO_ROOM);
                }
            }

            @Override
            public void onDataCancelled(DataModel dataModel) {
                resultInterface.onFail(dataModel.getDatabaseError().getMessage());
            }
        });
    }

    public static String createRoom(GroupM groupM){
        return groupM.getName() + "_" + groupM.getId();
    }

    public static String createRoom(UserM userM){
        return userM.getUserId() + "_" + SupportService.getUserInstance().getUid();
    }

    public static void initializeApp(final Context context){
        if (SupportService.getUserInstance() != null) {
            GetDataHandler getDataHandler = new GetDataHandler();
            getDataHandler.setDataReference(SupportService.getUserReference()
                    .child(SupportService.getUserInstance().getUid()));
            getDataHandler.setSingleValueEventListener(new ValueInterface() {
                @Override
                public void onDataSuccess(DataModel dataModel) {
                    UserM userM = dataModel.getDataSnapshot().getValue(UserM.class);
                    if (userM != null ) {
                        SupportService.init(context, userM);
                    }
                }

                @Override
                public void onDataCancelled(DataModel dataModel) {
                    Log.e(TAG, "initializeApp " + dataModel.getDatabaseError().getMessage());
                }
            });
        }
    }

    public static void loadPicasso(Context context, ImageView file_img, String path) {
        Picasso.with(context)
                .load(path)
                .noFade()
                .noPlaceholder()
                .error(ContextCompat.getDrawable(context, R.drawable.ic_insert_photo_black_24dp))
                .into(file_img);
    }

    public static boolean isHasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void callIntent(String type, Activity act) {
        Intent intent;
        switch (type) {
            case Constants.FILE:
                intent = new Intent();
                if (Build.VERSION.SDK_INT >= 19) {
                    intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    intent.setType("*/*");
                } else {
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                }
                act.startActivityForResult(intent, Constants.ACTIVITY_SELECT_FILE);
                break;
            case Constants.IMAGE:
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                act.startActivityForResult(intent, Constants.ACTIVITY_SELECT_PHOTO);
                break;
            case Constants.VIDEO:
                intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                act.startActivityForResult(intent, Constants.ACTIVITY_SELECT_VIDEO);
                break;
            case Constants.GALLERY:
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //intent.setType("image/* video/");
                //set only for image
                intent.setType("image/*");
                act.startActivityForResult(intent, Constants.ACTIVITY_SELECT_GALLERY);
                break;
            default:
        }
    }
}
