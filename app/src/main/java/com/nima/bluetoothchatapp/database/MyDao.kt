package com.nima.bluetoothchatapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nima.bluetoothchatapp.database.entities.ChatMessage
import com.nima.bluetoothchatapp.database.entities.ConnectedDevices
import kotlinx.coroutines.flow.Flow

@Dao
interface MyDao {

    //Messages
    @Query("SELECT * FROM ChatMessage WHERE id > (:ID)")
    fun getLastMessage(ID : Int): LiveData<List<ChatMessage>>
    @Query("SELECT * FROM ChatMessage")
    fun getMessages(): Flow<List<ChatMessage>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessage(vararg message: ChatMessage)

    //Devices
    @Query("SELECT * FROM connectedDevices")
    fun getConnectedDevices() : LiveData<ConnectedDevices?>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertConnectedDevices(devices: ConnectedDevices)

}