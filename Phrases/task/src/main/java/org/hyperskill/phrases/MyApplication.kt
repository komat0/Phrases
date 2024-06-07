package org.hyperskill.phrases

import android.app.Application
import androidx.room.Room

class MyApplication : Application() {
    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(this, AppDatabase::class.java, "phrases.db").build()
    }
}