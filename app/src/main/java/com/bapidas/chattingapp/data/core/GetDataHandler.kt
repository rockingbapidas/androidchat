package com.bapidas.chattingapp.data.core

import com.bapidas.chattingapp.data.core.callbacks.ChildInterface
import com.bapidas.chattingapp.data.core.callbacks.ValueInterface
import com.bapidas.chattingapp.data.core.model.DataModel
import com.google.firebase.database.*

/**
 * Created by bapidas on 26/07/17.
 */
class GetDataHandler {

    private val dataModel: DataModel = DataModel()

    fun setSingleValueEventListener(databaseReference: DatabaseReference, valueInterface: ValueInterface) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataModel.dataSnapshot = dataSnapshot
                valueInterface.onDataSuccess(dataModel)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                dataModel.databaseError = databaseError
                valueInterface.onDataCancelled(dataModel)
            }
        })
    }

    fun setSingleValueEventListener(queryReference: Query, valueInterface: ValueInterface) {
        queryReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataModel.dataSnapshot = dataSnapshot
                valueInterface.onDataSuccess(dataModel)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                dataModel.databaseError = databaseError
                valueInterface.onDataCancelled(dataModel)
            }
        })
    }

    fun setValueEventListener(databaseReference: DatabaseReference, valueInterface: ValueInterface) {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataModel.dataSnapshot = dataSnapshot
                valueInterface.onDataSuccess(dataModel)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                dataModel.databaseError = databaseError
                valueInterface.onDataCancelled(dataModel)
            }
        })
    }

    fun setValueEventListener(queryReference: Query, valueInterface: ValueInterface) {
        queryReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataModel.dataSnapshot = dataSnapshot
                valueInterface.onDataSuccess(dataModel)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                dataModel.databaseError = databaseError
                valueInterface.onDataCancelled(dataModel)
            }
        })
    }

    fun setChildValueListener(databaseReference: DatabaseReference, childInterface: ChildInterface) {
        databaseReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                dataModel.dataSnapshot = dataSnapshot
                dataModel.extraString = s.orEmpty()
                childInterface.onChildNew(dataModel)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                dataModel.dataSnapshot = dataSnapshot
                dataModel.extraString = s.orEmpty()
                childInterface.onChildModified(dataModel)
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                dataModel.dataSnapshot = dataSnapshot
                childInterface.onChildDelete(dataModel)
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {
                dataModel.dataSnapshot = dataSnapshot
                dataModel.extraString = s.orEmpty()
                childInterface.onChildRelocate(dataModel)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                dataModel.databaseError = databaseError
                childInterface.onChildCancelled(dataModel)
            }
        })
    }

    fun setChildValueListener(queryReference: Query, childInterface: ChildInterface) {
        queryReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                dataModel.dataSnapshot = dataSnapshot
                dataModel.extraString = s.orEmpty()
                childInterface.onChildNew(dataModel)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                dataModel.dataSnapshot = dataSnapshot
                dataModel.extraString = s.orEmpty()
                childInterface.onChildModified(dataModel)
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                dataModel.dataSnapshot = dataSnapshot
                childInterface.onChildDelete(dataModel)
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {
                dataModel.dataSnapshot = dataSnapshot
                dataModel.extraString = s.orEmpty()
                childInterface.onChildRelocate(dataModel)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                dataModel.databaseError = databaseError
                childInterface.onChildCancelled(dataModel)
            }
        })
    }
}