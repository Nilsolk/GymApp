package ru.nilsolk.gymapp.model

data class ExecutionModel(
    val exerciseName: String,
    val weight: Double,
    val type: String,
    val set: Int,
    val rep: Int,
    val date: String,
    val muscleGroup: String
)