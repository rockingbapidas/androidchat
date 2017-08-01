package com.vantagecircle.chatapp.utils;

import android.util.Log;

import com.vantagecircle.chatapp.Support;
import com.vantagecircle.chatapp.core.DataModel;
import com.vantagecircle.chatapp.core.GetDataHandler;
import com.vantagecircle.chatapp.core.interfacep.ResultInterface;
import com.vantagecircle.chatapp.core.interfacep.ValueInterface;
import com.vantagecircle.chatapp.model.GroupM;
import com.vantagecircle.chatapp.model.UserM;

/**
 * Created by bapidas on 01/08/17.
 */

public class ConfigUtils {
    private static final String TAG = ConfigUtils.class.getSimpleName();

    public static void getRoom(final GroupM groupM, final UserM userM, final ResultInterface resultInterface) {
        GetDataHandler getDataHandler = new GetDataHandler();
        getDataHandler.setDataReference(Support.getChatReference());
        getDataHandler.setSingleValueEventListener(new ValueInterface() {
            @Override
            public void onDataSuccess(DataModel dataModel) {
                String room_type_1;
                String room_type_2;
                if (groupM != null) {
                    room_type_1 = groupM.getName() + "_" + groupM.getId();
                    room_type_2 = groupM.getId() + "_" + groupM.getName();
                } else {
                    room_type_1 = userM.getUserId() + "_" + Support.id;
                    room_type_2 = Support.id + "_" + userM.getUserId();
                }

                if (dataModel.getDataSnapshot().hasChild(room_type_1)) {
                    Log.e(TAG, "Chat room available " + room_type_1);
                    resultInterface.onSuccess(room_type_1);
                } else if (dataModel.getDataSnapshot().hasChild(room_type_2)) {
                    Log.e(TAG, "Chat room available " + room_type_2);
                    resultInterface.onSuccess(room_type_2);
                } else {
                    Log.e(TAG, "No Chat room available yet");
                }
            }

            @Override
            public void onDataCancelled(DataModel dataModel) {
                Log.e(TAG, "Error " + dataModel.getDatabaseError().getMessage());
            }
        });
    }
}
