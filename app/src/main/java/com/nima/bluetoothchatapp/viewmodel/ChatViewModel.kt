package com.nima.bluetoothchatapp.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.nima.bluetoothchatapp.chat.*
import com.nima.bluetoothchatapp.repository.ChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class ChatViewModel @ViewModelInject constructor
    (private val repository: ChatRepository)
    : ViewModel(){

    private var failedMessages : List<Message?>? = null
    fun insertMessage(
        writeMessage: String,
        chatId: String,
        uid: String,
        senderId: String,
        isMe: Boolean,
        fatherId: Int
    ) {
        val message = Text(
            content = Content(
                0,
                chatId,
                getTimeCurrent(),
                uid,
                writeMessage,
                senderId,
                isMe,
                MessageStatus.MessageStatusSend()
            ),
            father = Father(fatherId),
            child = null
        )
        CoroutineScope(Dispatchers.IO).launch {
            repository.insert(message)
        }
    }
    private fun getTimeCurrent() : String{
        val currentDateTime = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        return dateFormat.format(currentDateTime.time).toString()
    }

    fun updateMyMessageStatus(status: MessageStatus, uId: String, message: String){
        CoroutineScope(Dispatchers.IO).launch {
            repository.updateMyMessageStatus(status, uId, message)
        }
    }

    fun getMyFailedMessages(chatID: String) : List<Message?>? {
        CoroutineScope(Dispatchers.IO).launch {
            failedMessages =
                repository.getMyFailedMessages(chatID).toList()
        }
        return failedMessages
    }
}