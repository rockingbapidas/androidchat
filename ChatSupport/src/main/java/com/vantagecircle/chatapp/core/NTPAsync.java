package com.vantagecircle.chatapp.core;

import android.os.AsyncTask;
import android.os.StrictMode;

import com.vantagecircle.chatapp.core.interfaceC.TimeInterface;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by bapidas on 18/08/17.
 */

public class NTPAsync extends AsyncTask<String, Void, String> {
    private TimeInterface timeInterface;
    private String TIME_SERVER = "time-a.nist.gov";

    @Override
    protected String doInBackground(String... params) {
        try {
            NTPUDPClient timeClient = new NTPUDPClient();
            InetAddress inetAddress = InetAddress.getByName(TIME_SERVER);
            TimeInfo timeInfo = timeClient.getTime(inetAddress);
            //long returnTime = timeInfo.getReturnTime();   //it return local device time
            return String.valueOf(timeInfo.getMessage().getTransmitTimeStamp().getTime()); //it return server time
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String l) {
        super.onPostExecute(l);
        if (timeInterface != null) {
            if (l != null) {
                timeInterface.onTimeSuccess(Long.parseLong(l));
                this.onCancelled();
            } else {
                timeInterface.onTimeError("Error occurred");
            }
        }
    }

    public void setCallBack(TimeInterface timeInterface){
        this.timeInterface = timeInterface;
    }
}
