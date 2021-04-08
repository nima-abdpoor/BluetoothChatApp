package com.nima.bluetoothchatapp.repository

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import com.nima.bluetoothchatapp.database.MyDao
import com.nima.bluetoothchatapp.database.entities.ChatMessage

class ChatRepository constructor(private val myDao: MyDao) {

    fun saveMessage() : LiveData<List<ChatMessage>>{
        return myDao.getMessage()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun insert(){
        val text = ChatMessage(null,254,"LocalDateTime.now()","salam",true,"1",1)
        myDao.insertMessage(text)
    }
}