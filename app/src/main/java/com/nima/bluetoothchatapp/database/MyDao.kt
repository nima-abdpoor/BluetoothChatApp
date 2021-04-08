package com.nima.bluetoothchatapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nima.bluetoothchatapp.database.entities.Account
import com.nima.bluetoothchatapp.database.entities.UserInfo

@Dao
interface MyDao {

    //Account
    @Query("SELECT * FROM account")
    fun getAccount(): Account
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAccount(vararg account: Account)

    //UserInfo
    @Query("SELECT * FROM userInfo")
    fun getUserInfo() : LiveData<UserInfo?>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserInfo(userInfo: UserInfo)

}