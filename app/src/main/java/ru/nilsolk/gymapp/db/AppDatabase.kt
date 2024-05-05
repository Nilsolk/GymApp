package ru.nilsolk.gymapp.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Exercise::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDAO
}