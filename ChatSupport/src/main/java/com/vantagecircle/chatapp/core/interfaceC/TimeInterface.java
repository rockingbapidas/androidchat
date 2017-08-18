package com.vantagecircle.chatapp.core.interfaceC;

/**
 * Created by bapidas on 18/08/17.
 */

public interface TimeInterface {
    void onTimeSuccess(long timeStamp);
    void onTimeError(String error);
}
