package com.example.passwordapp.data.password

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "password")
data class Password(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "website") val websiteName: String,
    @ColumnInfo(name = "user") val username: String,
    @ColumnInfo(name = "pass") val password: String,
)