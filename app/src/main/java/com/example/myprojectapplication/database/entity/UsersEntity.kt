package com.example.myprojectapplication.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "USERS")
data class UsersEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "name") val name: String? = null ,
    @ColumnInfo(name = "email") val email: String? = null,
    @ColumnInfo(name = "username") val userName: String,
    @ColumnInfo(name = "password") val password: String
)

