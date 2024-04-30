package ru.nilsolk.gymapp.model

data class ProfileUpdateModel(
    var username: String?,
    val profileImageURL: String?,
    var age: String?,
    var weight: Double?,
    var targetWeight: Double?
    )