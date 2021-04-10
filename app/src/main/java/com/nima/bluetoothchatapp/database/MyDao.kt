package com.nima.bluetoothchatapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nima.bluetoothchatapp.database.entities.ChatMessage
import com.nima.bluetoothchatapp.database.entities.ChildMessageEntity
import com.nima.bluetoothchatapp.database.entities.ConnectedDevices
import kotlinx.coroutines.flow.Flow

@Dao
interface MyDao {

    /* ------------------------------------------------------------*/


    //Messages
    @Query("SELECT * FROM ChatMessage WHERE id > (:ID)")
    fun getLastMessage(ID : Int): LiveData<List<ChatMessage>>

    @Query("SELECT * FROM ChatMessage WHERE chatId = (:chatID)")
    fun getMessages(chatID : String): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessage(vararg message: ChatMessage)

    @Query("UPDATE ChatMessage SET status = (:status) WHERE uId = (:uId) AND content = (:message) AND status != (:status)")
    fun updateMyMessageStatus(status : String,uId :String,message  :String)

    @Query("SELECT * FROM ChatMessage WHERE chatId = (:chatID) AND status != '2' AND isMe = 1")
    fun getMyFailedMessages(chatID: String) : List<ChatMessage>
    /* ------------------------------------------------------------*/


    //Devices
    @Query("SELECT * FROM connectedDevices")
    fun getConnectedDevices() : LiveData<List<ConnectedDevices>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertConnectedDevices(devices: ConnectedDevices)

    /* ------------------------------------------------------------*/


    //ChildMessages
    @Query("SELECT * FROM ChildMessageEntity WHERE id = (:ID)")
    fun getChildMessages(ID : Int) : Flow<ChildMessageEntity>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertChildMessage(child :ChildMessageEntity)

}