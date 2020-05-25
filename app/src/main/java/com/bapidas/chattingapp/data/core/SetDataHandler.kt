package com.bapidas.chattingapp.data.core

import com.bapidas.chattingapp.data.core.callbacks.ResultInterface
import com.google.firebase.database.DatabaseReference
import java.util.*

/**
 * Created by bapidas on 26/07/17.
 */
class SetDataHandler {
    lateinit var databaseReference: DatabaseReference

    fun insertData(`object`: Any, resultInterface: ResultInterface) {
        databaseReference.setValue(`object`)
                .addOnCompleteListener { task -> resultInterface.onSuccess(task.toString()) }
                .addOnFailureListener { e -> resultInterface.onFail(e.message.orEmpty()) }
    }

    fun updateData(hashMap: HashMap<String?, Any?>, resultInterface: ResultInterface) {
        databaseReference.updateChildren(hashMap)
                .addOnCompleteListener { task -> resultInterface.onSuccess(task.toString()) }
                .addOnFailureListener { e -> resultInterface.onFail(e.message.orEmpty()) }
    }
}