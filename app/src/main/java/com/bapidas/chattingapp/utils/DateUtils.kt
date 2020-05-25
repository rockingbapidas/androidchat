package com.bapidas.chattingapp.utils

import android.text.format.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by bapidas on 13/07/17.
 */
object DateUtils {
    private const val SECOND_MILLIS = 1000
    private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
    private const val HOUR_MILLIS = 60 * MINUTE_MILLIS

    fun getDayOfMonthSuffix(no: String): String {
        val n = no.toInt()
        if (n in 1..31) if (n in 11..13) {
            return "th"
        }
        return when (n % 10) {
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> "th"
        }
    }

    fun getEEEE(date: Date?): String {
        //Thursday  dayOfTheWeek
        return DateFormat.format("EEEE", date) as String
    }

    fun getEEE(date: Date?): String {
        //Thu  dayOfTheWeek
        return DateFormat.format("EEE", date) as String
    }

    fun getEE(date: Date?): String {
        //Th  dayOfTheWeek
        return DateFormat.format("EE", date) as String
    }

    fun getMMMM(date: Date?): String {
        //June  stringMonth
        return DateFormat.format("MMMM", date) as String
    }

    fun getMMM(date: Date?): String {
        //Jun  stringMonth
        return DateFormat.format("MMM", date) as String
    }

    @JvmStatic
    fun getMM(date: Date?): String {
        //06 intMonth
        return DateFormat.format("MM", date) as String
    }

    @JvmStatic
    fun getDD(date: Date?): String {
        // 26 date
        return DateFormat.format("dd", date) as String
    }

    @JvmStatic
    fun getYYYY(date: Date?): String {
        // 2014 Year
        return DateFormat.format("yyyy", date) as String
    }

    fun getYY(date: Date?): String {
        // 14 Year
        return DateFormat.format("yy", date) as String
    }

    @JvmStatic
    fun getTimeAgo(times: Long): String? {
        var time = times
        val timestamp = time
        if (time < 1000000000000L) {
            time *= 1000
        }
        val now = System.currentTimeMillis()
        if (time > now || time <= 0) {
            return null
        }
        val diff = now - time
        return when {
            diff < MINUTE_MILLIS -> {
                "now"
            }
            diff < 2 * MINUTE_MILLIS -> {
                "a min ago"
            }
            diff < 50 * MINUTE_MILLIS -> {
                (diff / MINUTE_MILLIS).toString() + " min ago"
            }
            diff < 90 * MINUTE_MILLIS -> {
                "an hour ago"
            }
            diff < 24 * HOUR_MILLIS -> {
                (diff / HOUR_MILLIS).toString() + " hr ago"
            }
            diff < 48 * HOUR_MILLIS -> {
                "yesterday"
            }
            else -> {
                val sdf = SimpleDateFormat("dd MMM, yy", Locale.ENGLISH)
                val netDate = Date(timestamp * 1000)
                sdf.format(netDate)
            }
        }
    }

    @JvmStatic
    fun getTime(timestamp: Long): String {
        val newFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
        val date = Date(timestamp)
        return newFormat.format(date)
    }
}