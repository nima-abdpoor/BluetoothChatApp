package com.nima.bluetoothchatapp.repository

import com.nima.bluetoothchatapp.database.MyDao
import com.nima.bluetoothchatapp.devices.BLDevice
import com.nima.bluetoothchatapp.mapper.ChatListMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChatListRepository @Inject constructor(private val dao: MyDao) {

    @Inject
    lateinit var chatLisMapper: ChatListMapper

    suspend fun getConnectedDevices() : Flow<BLDevice>? {
        val connectedDevices = dao.getConnectedDevices()
        if (connectedDevices.count() != 0){
            return connectedDevices.map { chatLisMapper.mapFromEntity(it)  }
        }
        return null
    }
}