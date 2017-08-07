package com.vantagecircle.chatapp.core.interfaceC;

import com.vantagecircle.chatapp.core.model.DataModel;

/**
 * Created by bapidas on 01/08/17.
 */

public interface ValueInterface{
    void onDataSuccess(DataModel dataModel);
    void onDataCancelled(DataModel dataModel);
}
