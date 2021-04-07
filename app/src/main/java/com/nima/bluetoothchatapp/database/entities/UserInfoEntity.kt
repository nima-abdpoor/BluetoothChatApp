package com.nima.tmdb.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserInfo constructor(
    @PrimaryKey
    var id : Int,
    var userName : String,
    var password: String,
)