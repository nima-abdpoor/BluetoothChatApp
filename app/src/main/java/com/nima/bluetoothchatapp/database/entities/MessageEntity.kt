package com.nima.bluetoothchatapp.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nima.bluetoothchatapp.chat.MessageStatus
import java.sql.Time

@Entity
data class ChatMessage constructor(
    @PrimaryKey
    val id: Int,
    val senderId: Int,
    val time : Time,
    val content: String,
    val isMe :Boolean,
    val status: MessageStatus,
    val fatherId:Int
)