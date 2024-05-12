package ru.nilsolk.gymapp.repository.model

data class ExerciseProgramModel(
    val programName: String,
    val aboutProgram: String,
    val firstDayIds: String,
    val secondDayIds: String,
    val thirdDayIds: String,
    val instructionsToDo: String,
    val numDays: String,
    val numWeeks: String
)