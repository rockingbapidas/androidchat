package com.bapidas.chattingapp.data.core.callbacks

/**
 * Created by bapidas on 01/08/17.
 */
interface ResultInterface {
    fun onSuccess(t: String)
    fun onFail(e: String)
}