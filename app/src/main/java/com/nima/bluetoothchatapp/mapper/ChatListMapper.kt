package com.nima.bluetoothchatapp.mapper

import com.nima.bluetoothchatapp.database.entities.ConnectedDevices
import com.nima.bluetoothchatapp.devices.BLDevice
import javax.inject.Inject

class ChatListMapper @Inject constructor() :EntityMapper<ConnectedDevices,BLDevice> {
    override fun mapFromEntity(entity: ConnectedDevices): BLDevice {
        entity.apply {
            return BLDevice(
                deviceName = deviceName,
                deviceAddress = deviceAddress,
                date = date
            )
        }
    }

    override fun mapToEntity(domainModel: BLDevice): ConnectedDevices {
        domainModel.apply {
            return ConnectedDevices(
                id = 0,
                chatId = deviceAddress,
                deviceName = deviceName,
                deviceAddress = deviceAddress,
                date = date
            )
        }
    }

    fun mapFromEntityList(response: List<ConnectedDevices>): List<BLDevice?> =
        response.map { mapFromEntity(it) }

    fun mapToEntityList(response: List<BLDevice>): List<ConnectedDevices?> =
        response.map { mapToEntity(it) }
}