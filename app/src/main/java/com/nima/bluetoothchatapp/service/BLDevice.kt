package com.nima.bluetoothchatapp.service

data class BLDevice(
    val deviceName : String,
    val deviceAddress:String,
) {
    override fun toString(): String {
        return "BluetoothDevice(deviceName='$deviceName', deviceAddress='$deviceAddress')"
    }
}