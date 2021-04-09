package com.nima.bluetoothchatapp.repository

import com.nima.bluetoothchatapp.database.MyDao
import com.nima.bluetoothchatapp.mapper.ChatMessageMapper
import javax.inject.Inject

class ChatListRepository @Inject constructor(private val dao: MyDao) {

    @Inject
    lateinit var chatMessageMapper: ChatMessageMapper
}