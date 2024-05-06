package ru.nilsolk.gymapp.db

import android.app.Application
import androidx.room.Room
import ru.nilsolk.gymapp.utils.AppPreferences

class App : Application() {
    companion object {
        lateinit var database: AppDatabase
    }

    override fun onCreate() {
        AppPreferences(this).saveBoolean("isDataLoaded", false)
        super.onCreate()
        database =
            Room.databaseBuilder(applicationContext, AppDatabase::class.java, "my-database")
                .fallbackToDestructiveMigration().build()
    }

}