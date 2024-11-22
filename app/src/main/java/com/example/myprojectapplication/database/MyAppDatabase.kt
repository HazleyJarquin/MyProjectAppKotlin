package com.example.myprojectapplication.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myprojectapplication.MyAppRoomApplication
import com.example.myprojectapplication.database.dao.UsersDao
import com.example.myprojectapplication.database.entity.UsersEntity

@Database(entities = [UsersEntity::class], version = 5)
abstract class MyAppDatabase: RoomDatabase() {
    abstract fun usersDao(): UsersDao
    companion object {
        @Volatile
        private var INSTANCE: MyAppDatabase? = null

        fun getDatabase(context: MyAppRoomApplication): MyAppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyAppDatabase::class.java,
                    "my_app_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}