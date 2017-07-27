package com.vantagecircle.chatapp.core;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

/**
 * Created by bapidas on 26/07/17.
 */

public abstract class DataClass {
    private DatabaseReference databaseReference;

    protected DataClass(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference;
    }

    public void insertData(Object object){
        databaseReference.setValue(object).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                onSuccess(task.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onFail(e.getMessage());
            }
        });
    }

    public void updateData(HashMap<String, Object> hashMap){
        databaseReference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                onSuccess(task.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onFail(e.getMessage());
            }
        });
    }

    protected abstract void onSuccess(String t);

    protected abstract void onFail(String e);
}
