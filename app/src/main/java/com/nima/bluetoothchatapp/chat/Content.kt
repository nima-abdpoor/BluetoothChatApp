package com.nima.bluetoothchatapp.chat

import java.sql.Time

data class Content<T : Message>(
    var id : Int,
    var time : Time,
    var content: T,
    var senderId : Int,
    var isMe : Boolean,
    var status :MessageStatus
)