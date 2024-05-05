package ru.nilsolk.gymapp.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable

@Dao
interface ExerciseDAO {
    @Query("SELECT * FROM exercises")
    fun getAllExercises(): Flowable<List<Exercise>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(exercises: List<Exercise>)
}