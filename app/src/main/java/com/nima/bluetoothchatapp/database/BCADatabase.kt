package com.nima.bluetoothchatapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nima.bluetoothchatapp.database.entities.Account
import com.nima.bluetoothchatapp.database.entities.UserInfo

@Database(
    entities = [
        Account::class,
        UserInfo::class
    ], version = 1
)
abstract class BCADatabase : RoomDatabase() {
    abstract fun myDao(): MyDao
}