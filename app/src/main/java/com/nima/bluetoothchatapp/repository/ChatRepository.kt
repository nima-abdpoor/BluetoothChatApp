package com.nima.bluetoothchatapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.nima.bluetoothchatapp.chat.Message
import com.nima.bluetoothchatapp.chat.MessageStatus
import com.nima.bluetoothchatapp.database.MyDao
import com.nima.bluetoothchatapp.database.entities.ChatMessage
import com.nima.bluetoothchatapp.mapper.ChatMessageMapper
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class ChatRepository @Inject constructor(private val myDao: MyDao) {

    @Inject
    lateinit var chatMessageMapper: ChatMessageMapper

    fun getNewMessage(id: Int): LiveData<List<ChatMessage>> {
        return myDao.getLastMessage(id)
    }

    //get All Messages with ChatID
    fun getAllMessages(chatId: String): Flow<List<Message?>>? {
        val messages = myDao.getMessages(chatId)
        return messages.map { chatMessageMapper.mapFromEntityList(it) }

    }

    fun insert(message: Message) {
        val chatMessage = chatMessageMapper.mapToEntity(message)
        chatMessage?.let { myDao.insertMessage(it) }
    }

    fun updateMyMessageStatus(status: MessageStatus, uId: String, message: String) {
        val state = when (status) {
            is MessageStatus.MessageStatusNone -> "0"
            is MessageStatus.MessageStatusSeen -> "2"
            is MessageStatus.MessageStatusSend -> "1"
        }
        myDao.updateMyMessageStatus(state, uId, message)
    }

    fun getMyFailedMessages(chatID: String): List<Message?> {
        val failedMessages = myDao.getMyFailedMessages(chatID)
        return failedMessages.map { chatMessageMapper.mapFromEntity(it) }
    }
}