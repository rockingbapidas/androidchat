package com.vantagecircle.chatapp.core.interfacep;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.vantagecircle.chatapp.core.DataModel;

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
