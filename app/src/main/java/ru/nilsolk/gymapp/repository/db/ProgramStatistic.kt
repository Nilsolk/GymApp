package ru.nilsolk.gymapp.repository.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "program_statistics")
data class ProgramStatistic(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val programName: String,
//    val currentDate: LocalDate,
    val exerciseDate: String,
    val currentDay: Int,
    val daysLeft: Int,
    val progress: Float
)