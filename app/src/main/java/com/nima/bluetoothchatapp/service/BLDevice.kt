package com.nima.bluetoothchatapp.service

data class BLDevice(
    val deviceName : String,
    val deviceAddress:String,
    val date:String? = null
) {
}