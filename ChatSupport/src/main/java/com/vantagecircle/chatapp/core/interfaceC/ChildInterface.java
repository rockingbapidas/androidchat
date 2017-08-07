package com.vantagecircle.chatapp.core.interfaceC;

import com.vantagecircle.chatapp.core.model.DataModel;

/**
 * Created by bapidas on 01/08/17.
 */

public interface ChildInterface {
    void onChildNew(DataModel dataModel);

    void onChildModified(DataModel dataModel);

    void onChildDelete(DataModel dataModel);

    void onChildRelocate(DataModel dataModel);

    void onChildCancelled(DataModel dataModel);
}
