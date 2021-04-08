package com.nima.bluetoothchatapp.chat

data class Content(
    var id : Int,
    var time : String,
    var content: String,
    var senderId : Int,
    var isMe : Boolean,
    var status :MessageStatus
)