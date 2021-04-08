package com.nima.bluetoothchatapp.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ChatMessage constructor(
    @PrimaryKey
    val id: Int?,
    val senderId: Int,
    val time : String,
    val content: String,
    val isMe :Boolean,
    val status: String,
    val fatherId:Int
)