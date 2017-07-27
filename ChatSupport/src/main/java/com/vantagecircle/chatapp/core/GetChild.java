package com.vantagecircle.chatapp.core;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by bapidas on 26/07/17.
 */

public abstract class GetChild {
    private DatabaseReference databaseReference;

    public GetChild(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference;
    }

    public void addChildListener(){
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                onChildNew(dataSnapshot, s);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                onChildModified(dataSnapshot, s);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                onChildDelete(dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                onChildRelocate(dataSnapshot, s);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onChildCancelled(databaseError);
            }
        });
    }

    protected abstract void onChildNew(DataSnapshot dataSnapshot, String s);

    protected abstract void onChildModified(DataSnapshot dataSnapshot, String s);

    protected abstract void onChildDelete(DataSnapshot dataSnapshot);

    protected abstract void onChildRelocate(DataSnapshot dataSnapshot, String s);

    protected abstract void onChildCancelled(DatabaseError databaseError);
}
