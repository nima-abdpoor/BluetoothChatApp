package com.nima.bluetoothchatapp.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ConnectedDevices constructor(
    @PrimaryKey(autoGenerate = true)
    var id : Int,
    var deviceName : String,
    var deviceAddress: String,
)