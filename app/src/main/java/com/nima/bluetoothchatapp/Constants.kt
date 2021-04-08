package com.nima.bluetoothchatapp

interface Constants {
    companion object {
        // Message types sent from the BluetoothChatService Handler
        const val MESSAGE_STATE_CHANGE = 1
        const val MESSAGE_READ = 2
        const val MESSAGE_WRITE = 3
        const val MESSAGE_DEVICE_NAME = 4
        const val MESSAGE_TOAST = 5

        // Key names received from the BluetoothChatService Handler
        const val DEVICE_NAME = "device_name"
        const val DEVICE_ADDRESS ="device_address"
        const val TOAST = "toast"
        const val DATABASE_NAME = "BLUETOOTH_APPLICATION_CHAT"
        const val SHARED_PREFERENCES = "BCA_SHARED_PREFERENCES"

        //Message Status
        const val MessageStatusNone = "0"
        const val MessageStatusSend = "1"
        const val MessageStatusSeen = "2"

        //Message Type
        const val MessageTypeText = "0"
        const val MessageTypeFile = "1"
        const val MessageTypeVoice = "2"
        const val MessageTypeNone = "3"
    }
}