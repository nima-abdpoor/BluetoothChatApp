package com.nima.bluetoothchatapp.repository

import com.nima.bluetoothchatapp.chat.Message
import com.nima.bluetoothchatapp.database.MyDao
import javax.inject.Inject

class ChatRepository() {
    @Inject
    lateinit var myDao : MyDao

    fun saveMessage(message: Message){

    }
}