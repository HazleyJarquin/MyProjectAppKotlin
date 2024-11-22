package com.example.myprojectapplication.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myprojectapplication.database.entity.UsersEntity

@Dao
interface UsersDao {
    @Query("SELECT * FROM USERS WHERE username = :username AND password = :password")
    fun getUser(username: String, password: String): UsersEntity?


    @Query("SELECT * FROM USERS")
    fun getAllUsers(): List<UsersEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(customerEntity: UsersEntity)
}