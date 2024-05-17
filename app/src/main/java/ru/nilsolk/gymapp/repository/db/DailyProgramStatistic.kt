package ru.nilsolk.gymapp.repository.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_statistic",
    foreignKeys = [
        ForeignKey(
            entity = ExerciseDone::class,
            parentColumns = ["id"],
            childColumns = ["exerciseDoneId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProgramStatistic::class,
            parentColumns = ["id"],
            childColumns = ["programStatisticId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DailyProgramStatistic(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val exerciseDoneId: String,
    val programStatisticId: Int,
    val date: String,
    val weight: Int,
    val completedSets: Int,
    val completedReps: Int
)