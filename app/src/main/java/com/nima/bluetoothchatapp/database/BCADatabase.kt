package com.nima.bluetoothchatapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nima.bluetoothchatapp.database.entities.ChatMessage
import com.nima.bluetoothchatapp.database.entities.ChildMessageEntity
import com.nima.bluetoothchatapp.database.entities.ConnectedDevices

@Database(
    entities = [
        ChatMessage::class,
        ConnectedDevices::class,
        ChildMessageEntity::class
    ], version = 1
)
abstract class BCADatabase : RoomDatabase() {
    abstract fun myDao(): MyDao
}