package ru.nilsolk.gymapp.repository.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "exercises_done",
    foreignKeys = [
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class ExerciseDone(
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
    val reps: String,
    val exerciseId: String?
)
