package ru.nilsolk.gymapp.model

data class ToDoModel(
    val selectedDay: String?,
    val selectedDate: String?,
    val todoText: String?,
    val todoId: String?,
    val createdAt: Long?,
    val muscleGroup: String,
    val programName: String
)

