package com.nima.bluetoothchatapp.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ChatMessage constructor(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val chatId:String,
    val senderId: String,
    val time : String,
    val uId :String,
    val content: String,
    val isMe :Boolean,
    val status: String,
    val type:String,
    val fatherId:Int
)