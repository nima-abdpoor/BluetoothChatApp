package com.nima.bluetoothchatapp.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.nima.bluetoothchatapp.devices.BLDevice
import com.nima.bluetoothchatapp.repository.ChatListRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

class ChatListViewModel @ViewModelInject constructor(
    private val repository: ChatListRepository
) :ViewModel(){
    private var allDevices: List<BLDevice?>? = null

    fun getConnectedDevices(): List<BLDevice?>?{
        CoroutineScope(Dispatchers.IO).launch {
            allDevices =
                repository.getConnectedDevices()?.toList()
        }
        return allDevices
    }

    fun insertConnectedDevice(blDevice: BLDevice) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.insertConnectedDevice(blDevice)
        }
    }
}