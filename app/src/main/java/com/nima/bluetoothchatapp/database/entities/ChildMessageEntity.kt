package com.nima.bluetoothchatapp.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ChildMessageEntity constructor(
    @PrimaryKey
    val id :Int,
    val senderId : Int,
    val ids : String
)