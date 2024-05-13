package ru.nilsolk.gymapp

import android.app.Application
import androidx.room.Room
import ru.nilsolk.gymapp.repository.db.AppDatabase
import ru.nilsolk.gymapp.utils.AppPreferences

class App : Application() {
    companion object {
        lateinit var database: AppDatabase
    }

    override fun onCreate() {
        super.onCreate()
        database =
            Room.databaseBuilder(applicationContext, AppDatabase::class.java, "my-database")
                .fallbackToDestructiveMigration().build()
    }

}