package com.bapidas.chattingapp.data.core.callbacks

import com.bapidas.chattingapp.data.core.model.DataModel

/**
 * Created by bapidas on 01/08/17.
 */
interface ValueInterface {
    fun onDataSuccess(dataModel: DataModel)
    fun onDataCancelled(dataModel: DataModel)
}