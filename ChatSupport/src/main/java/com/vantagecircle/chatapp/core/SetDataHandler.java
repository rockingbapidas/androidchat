package com.vantagecircle.chatapp.core;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.vantagecircle.chatapp.core.interfaceC.ResultInterface;

import java.util.HashMap;

/**
 * Created by bapidas on 26/07/17.
 */

public class SetDataHandler {
    private DatabaseReference databaseReference;

    public void setDatabaseReference(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference;
    }

    public void insertData(Object object, final ResultInterface resultInterface) {
        databaseReference.setValue(object)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        resultInterface.onSuccess(task.toString());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        resultInterface.onFail(e.getMessage());
                    }
                });
    }

    public void updateData(HashMap<String, Object> hashMap, final ResultInterface resultInterface) {
        databaseReference.updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        resultInterface.onSuccess(task.toString());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        resultInterface.onFail(e.getMessage());
                    }
                });
    }
}
