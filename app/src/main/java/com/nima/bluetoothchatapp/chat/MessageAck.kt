package com.nima.bluetoothchatapp.chat

data class MessageAck(
    val isMe : Boolean,
    val status :MessageStatus,
    val UID : String,
    val content: String
)