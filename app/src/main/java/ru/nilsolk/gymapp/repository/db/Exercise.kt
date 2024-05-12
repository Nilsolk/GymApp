package ru.nilsolk.gymapp.repository.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey val id: String,
    val name: String,
    val gifUrl: String,
    val bodyPart: String,
    val equipment: String,
    val instructions: List<String>,
    val secondaryMuscles: List<String>,
    val target: String,
    val programName: String,
    val sets: String,
    val reps: String
)