package com.nima.bluetoothchatapp.repository

import androidx.lifecycle.LiveData
import com.nima.bluetoothchatapp.database.MyDao
import com.nima.bluetoothchatapp.database.entities.ChatMessage
import kotlinx.coroutines.flow.Flow

class ChatRepository constructor(private val myDao: MyDao) {

    fun getNewMessage(id : Int) : LiveData<List<ChatMessage>>{
        return myDao.getLastMessage(id)
    }
    fun getAllMessages() : Flow<List<ChatMessage>> {
        return myDao.getMessages()
    }
    fun insert(){
        val text = ChatMessage(0,254,"LocalDateTime.now()","salam",true,"1","0",2)
        myDao.insertMessage(text)
    }
}