package ru.nilsolk.gymapp.db

import android.app.Application

import androidx.room.Room

class App : Application() {
    companion object {
        lateinit var database: AppDatabase
    }

    override fun onCreate() {
        super.onCreate()
        database =
            Room.databaseBuilder(applicationContext, AppDatabase::class.java, "my-database").build()
    }
}