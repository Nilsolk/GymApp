package ru.nilsolk.gymapp.model

import java.io.Serializable

data class PopularWorkoutsModel(val workoutName: String, val description: String, val imageURL: String) : Serializable