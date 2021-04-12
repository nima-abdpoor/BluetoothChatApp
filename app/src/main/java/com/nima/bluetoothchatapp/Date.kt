package com.nima.bluetoothchatapp

import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun String.toDateAndTime(): Date? {
    val TAG  = "String_Date"
    var date:Date? = null
    val format = SimpleDateFormat("yyyy.MM.dd G 'at' h:mm a")
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
fun String.toTime():String{
    var time = ""
    try {
        time = this.substring(16)
    }catch (e:ParseException){
        e.printStackTrace()
    }
    finally {
        return time
    }
}