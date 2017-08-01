package com.vantagecircle.chatapp.core;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.vantagecircle.chatapp.core.interfacep.ChildInterface;
import com.vantagecircle.chatapp.core.interfacep.ValueInterface;

/**
 * Created by bapidas on 26/07/17.
 */

public class GetDataHandler {
    private DatabaseReference databaseReference;
    private Query queryReference;
    private DataModel dataModel;

    public GetDataHandler() {
        dataModel = new DataModel();
    }

    public void setDataReference(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference;
    }

    public void setQueryReference(Query queryReference) {
        this.queryReference = queryReference;
    }

    public void setSingleValueEventListener(final ValueInterface valueInterface) {
        if (queryReference != null) {
            queryReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    dataModel.setDataSnapshot(dataSnapshot);
                    valueInterface.onDataSuccess(dataModel);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    dataModel.setDatabaseError(databaseError);
                    valueInterface.onDataCancelled(dataModel);
                }
            });
        } else {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    dataModel.setDataSnapshot(dataSnapshot);
                    valueInterface.onDataSuccess(dataModel);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    dataModel.setDatabaseError(databaseError);
                    valueInterface.onDataCancelled(dataModel);
                }
            });
        }
    }

    public void setValueEventListener(final ValueInterface valueInterface) {
        if (queryReference != null) {
            queryReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    dataModel.setDataSnapshot(dataSnapshot);
                    valueInterface.onDataSuccess(dataModel);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    dataModel.setDatabaseError(databaseError);
                    valueInterface.onDataCancelled(dataModel);
                }
            });
        } else {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    dataModel.setDataSnapshot(dataSnapshot);
                    valueInterface.onDataSuccess(dataModel);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    dataModel.setDatabaseError(databaseError);
                    valueInterface.onDataCancelled(dataModel);
                }
            });
        }
    }

    public void setChildValueListener(final ChildInterface childInterface) {
        if (queryReference != null) {
            queryReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    dataModel.setDataSnapshot(dataSnapshot);
                    dataModel.setExtraString(s);
                    childInterface.onChildNew(dataModel);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    dataModel.setDataSnapshot(dataSnapshot);
                    dataModel.setExtraString(s);
                    childInterface.onChildModified(dataModel);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    dataModel.setDataSnapshot(dataSnapshot);
                    childInterface.onChildDelete(dataModel);
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    dataModel.setDataSnapshot(dataSnapshot);
                    dataModel.setExtraString(s);
                    childInterface.onChildRelocate(dataModel);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    dataModel.setDatabaseError(databaseError);
                    childInterface.onChildCancelled(dataModel);
                }
            });
        } else {
            databaseReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    dataModel.setDataSnapshot(dataSnapshot);
                    dataModel.setExtraString(s);
                    childInterface.onChildNew(dataModel);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    dataModel.setDataSnapshot(dataSnapshot);
                    dataModel.setExtraString(s);
                    childInterface.onChildModified(dataModel);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    dataModel.setDataSnapshot(dataSnapshot);
                    childInterface.onChildDelete(dataModel);
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    dataModel.setDataSnapshot(dataSnapshot);
                    dataModel.setExtraString(s);
                    childInterface.onChildRelocate(dataModel);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    dataModel.setDatabaseError(databaseError);
                    childInterface.onChildCancelled(dataModel);
                }
            });
        }
    }
}
