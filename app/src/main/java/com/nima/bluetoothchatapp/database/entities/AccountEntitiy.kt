package com.nima.bluetoothchatapp.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Account constructor(
    @PrimaryKey
    val id: Int,
    val hash: String? = null,
    val iso6391: String? = null,
    val iso31661: String? = null,
    val name: String,
    val includeAdult: Boolean? = null,
    val username: String
)