package com.vantagecircle.chatapp.core;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by bapidas on 26/07/17.
 */

public abstract class GetParent {
    private DatabaseReference databaseReference;

    protected GetParent(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference;
    }

    public void addSingleListener() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                onDataSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onDataCancelled(databaseError);
            }
        });
    }

    public void addContinueListener() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                onDataSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onDataCancelled(databaseError);
            }
        });
    }

    protected abstract void onDataSuccess(DataSnapshot dataSnapshot);

    protected abstract void onDataCancelled(DatabaseError databaseError);
}
