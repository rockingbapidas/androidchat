package com.vantagecircle.chatapp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by bapidas on 13/07/17.
 */

public class DateUtils {
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;

    public static String getTimeAgo(long time) {
        long timestamp = time;
        if (time < 1000000000000L) {
            time *= 1000;
        }
        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a min ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " min ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hr ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yy", Locale.ENGLISH);
            Date netDate = new Date(timestamp * 1000);
            return sdf.format(netDate);
        }
    }

    public static String getTime(long timestamp){
        SimpleDateFormat newformat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        Date date = new Date(timestamp);
        return newformat.format(date);
    }
}
