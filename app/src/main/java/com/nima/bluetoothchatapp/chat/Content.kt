package com.nima.bluetoothchatapp.chat

import java.sql.Time

data class Content(
    var id : Int,
    var time : Time,
    var content: String,
    var senderId : Int,
    var isMe : Boolean,
    var status :MessageStatus
)