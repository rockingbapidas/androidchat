package com.bapidas.chattingapp.data.core.callbacks

import com.bapidas.chattingapp.data.core.model.DataModel

/**
 * Created by bapidas on 01/08/17.
 */
interface ChildInterface {
    fun onChildNew(dataModel: DataModel)
    fun onChildModified(dataModel: DataModel)
    fun onChildDelete(dataModel: DataModel)
    fun onChildRelocate(dataModel: DataModel)
    fun onChildCancelled(dataModel: DataModel)
}