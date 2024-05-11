package ru.nilsolk.gymapp.repository.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ExerciseDAO {
//    @Query("SELECT * FROM exercises")
//    suspend fun getAllExercises(): List<Exercise>

    @Query("SELECT * FROM exercises WHERE programName = :programName")
    suspend fun getAllExercises(programName: String): List<Exercise>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exercise: Exercise)

    @Delete
    suspend fun delete(exercise: Exercise)
}