package com.nima.bluetoothchatapp.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.nima.bluetoothchatapp.chat.Content
import com.nima.bluetoothchatapp.chat.Father
import com.nima.bluetoothchatapp.chat.MessageStatus
import com.nima.bluetoothchatapp.chat.Text
import com.nima.bluetoothchatapp.repository.ChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ChatViewModel @ViewModelInject constructor
    (private val repository: ChatRepository)
    : ViewModel(){

    fun insertMessage(writeMessage: String, chatId: String,senderId:Int,isMe:Boolean,fatherId :Int) {
        val message = Text(
            content = Content(0,chatId,"123",writeMessage,senderId,isMe,MessageStatus.MessageStatusSend(senderId)),
            father = Father(fatherId),
            child = null
        )
        CoroutineScope(Dispatchers.IO).launch {
            repository.insert(message)
        }
    }
}