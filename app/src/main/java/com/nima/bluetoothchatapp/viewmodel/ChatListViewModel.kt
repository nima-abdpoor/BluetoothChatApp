package com.nima.bluetoothchatapp.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import com.nima.bluetoothchatapp.repository.ChatListRepository

class ChatListViewModel @ViewModelInject constructor(
    private val repository: ChatListRepository
) {

}