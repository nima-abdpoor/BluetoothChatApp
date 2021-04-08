package com.nima.bluetoothchatapp.viewmodel

import android.util.Log
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
import java.text.SimpleDateFormat
import java.util.*


class ChatViewModel @ViewModelInject constructor
    (private val repository: ChatRepository)
    : ViewModel(){

    fun insertMessage(
        writeMessage: String,
        chatId: String,
        uid: String,
        senderId: Int,
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
                MessageStatus.MessageStatusSend(
                    senderId
                )
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
}