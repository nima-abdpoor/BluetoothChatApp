package com.nima.bluetoothchatapp.devices

data class BLDevice(
    val deviceName : String,
    val deviceAddress:String,
    val date:String? = null
) {
}