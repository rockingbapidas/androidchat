package com.vantagecircle.chatapp.utils;

import android.util.Log;

import com.vantagecircle.chatapp.Support;
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
        getDataHandler.setDataReference(Support.getChatReference());
        getDataHandler.setValueEventListener(new ValueInterface() {
            @Override
            public void onDataSuccess(DataModel dataModel) {
                String room_type_1 = userM.getUserId() + "_" + Support.getUserInstance().getUid();
                String room_type_2 = Support.getUserInstance().getUid() + "_" + userM.getUserId();
                if (dataModel.getDataSnapshot().hasChild(room_type_1)) {
                    //Log.e(TAG, "Chat room available " + room_type_1);
                    resultInterface.onSuccess(room_type_1);
                } else if (dataModel.getDataSnapshot().hasChild(room_type_2)) {
                    //Log.e(TAG, "Chat room available " + room_type_2);
                    resultInterface.onSuccess(room_type_2);
                } else {
                    //Log.e(TAG, "No Chat room available yet");
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
        getDataHandler.setDataReference(Support.getChatReference());
        getDataHandler.setValueEventListener(new ValueInterface() {
            @Override
            public void onDataSuccess(DataModel dataModel) {
                String room_type_1 = groupM.getName() + "_" + groupM.getId();
                if (dataModel.getDataSnapshot().hasChild(room_type_1)) {
                    //Log.e(TAG, "Group room available " + room_type_1);
                    resultInterface.onSuccess(room_type_1);
                } else {
                    //Log.e(TAG, "No Group room available yet");
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
        return userM.getUserId() + "_" + Support.getUserInstance().getUid();
    }

    public static void initializeApp(){
        if (Support.getUserInstance() != null) {
            GetDataHandler getDataHandler = new GetDataHandler();
            getDataHandler.setDataReference(Support.getUserReference()
                    .child(Support.getUserInstance().getUid()));
            getDataHandler.setSingleValueEventListener(new ValueInterface() {
                @Override
                public void onDataSuccess(DataModel dataModel) {
                    UserM userM = dataModel.getDataSnapshot().getValue(UserM.class);
                    if (userM != null ) {
                        Support.id = Support.getUserInstance().getUid();
                        Support.userM = userM;
                    }
                }

                @Override
                public void onDataCancelled(DataModel dataModel) {
                    Log.e(TAG, "initializeApp " + dataModel.getDatabaseError().getMessage());
                }
            });
        }
    }
}
