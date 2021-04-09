package com.nima.bluetoothchatapp.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.nima.bluetoothchatapp.devices.BLDevice
import com.nima.bluetoothchatapp.repository.ChatListRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatListViewModel @ViewModelInject constructor(
    private val repository: ChatListRepository
) :ViewModel(){

    fun getConnectedDevices() = repository.getConnectedDevices()



    fun insertConnectedDevice(blDevice: BLDevice) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.insertConnectedDevice(blDevice)
        }
    }
}