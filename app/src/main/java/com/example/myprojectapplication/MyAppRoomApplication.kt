package com.example.myprojectapplication

import android.app.Application
import com.example.myprojectapplication.database.MyAppDatabase

class MyAppRoomApplication: Application() {
    val myAppDb by lazy { MyAppDatabase.getDatabase(this) }
}