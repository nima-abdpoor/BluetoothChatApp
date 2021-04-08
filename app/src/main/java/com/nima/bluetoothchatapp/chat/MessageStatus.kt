package com.nima.bluetoothchatapp.chat

sealed class MessageStatus {
    data class MessageStatusSend(var id :Int):MessageStatus()
    data class MessageStatusSeen(var id :Int):MessageStatus()
    data class MessageStatusNone(var id :Int):MessageStatus()
}