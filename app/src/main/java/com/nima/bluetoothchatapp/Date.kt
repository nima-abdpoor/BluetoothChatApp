package com.nima.bluetoothchatapp

import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun String.toDate(): Date? {
    val TAG  = "String_Date"
    var date:Date? = null
    val format = SimpleDateFormat("MM-dd-yyyy HH:mm:ss")
    try {
        date = format.parse(this)
        Log.d(TAG, "StringToDate: $date")
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    finally {
        return date
    }
}