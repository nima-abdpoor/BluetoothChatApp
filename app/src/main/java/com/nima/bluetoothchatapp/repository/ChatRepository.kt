package com.nima.bluetoothchatapp.repository

import androidx.lifecycle.LiveData
import com.nima.bluetoothchatapp.chat.Message
import com.nima.bluetoothchatapp.database.MyDao
import com.nima.bluetoothchatapp.database.entities.ChatMessage
import com.nima.bluetoothchatapp.mapper.ChatMessageMapper
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatRepository @Inject constructor(private val myDao: MyDao) {

    @Inject
    lateinit var chatMessageMapper: ChatMessageMapper

    fun getNewMessage(id: Int): LiveData<List<ChatMessage>> {
        return myDao.getLastMessage(id)
    }

    //get All Messages with ChatID
    fun getAllMessages(chatId : String): Flow<List<ChatMessage>> {
        return myDao.getMessages(chatId)
    }

    fun insert(message : Message) {
        val chatMessage = chatMessageMapper.mapToEntity(message)
        chatMessage?.let { myDao.insertMessage(it) }
    }
}