package ru.nilsolk.gymapp.repository.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.time.LocalDate

@Dao
interface ExerciseDAO {

    @Query("SELECT * FROM exercises WHERE programName = :programName")
    suspend fun getAllExercises(programName: String): List<Exercise>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exercise: Exercise)

    @Delete
    suspend fun delete(exercise: Exercise)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgramStatistic(programStatistic: ProgramStatistic)

    @Query("SELECT programName FROM program_statistics LIMIT 1")
    suspend fun getProgramName(): String

    @Query("UPDATE program_statistics SET exerciseDate = :nextExerciseDate WHERE programName = :programName")
    suspend fun updateExerciseDate(programName: String, nextExerciseDate: String)

    @Query("UPDATE program_statistics SET currentDay = :currentDay WHERE programName = :programName")
    suspend fun updateCurrentDay(programName: String, currentDay: Int)

    @Query("UPDATE program_statistics SET daysLeft = :daysLeft WHERE programName = :programName")
    suspend fun updateDaysLeft(programName: String, daysLeft: Int)

    @Query("UPDATE program_statistics SET progress = :progress WHERE programName = :programName")
    suspend fun updateProgress(programName: String, progress: Float)


    @Query("SELECT exerciseDate FROM program_statistics WHERE programName = :programName")
    suspend fun getExerciseDate(programName: String): String

    @Query("SELECT currentDay FROM program_statistics WHERE programName = :programName")
    suspend fun getCurrentDay(programName: String): Int?

    @Query("SELECT daysLeft FROM program_statistics WHERE programName = :programName")
    suspend fun getDaysLeft(programName: String): Int?

    @Query("SELECT progress FROM program_statistics WHERE programName = :programName")
    suspend fun getProgress(programName: String): Float?

    @Query("DELETE FROM program_statistics WHERE programName = :programName")
    suspend fun deleteProgramStatistics(programName: String)

    @Query("DELETE FROM program_statistics")
    suspend fun deleteProgramStatisticsTable()

}