package ru.nilsolk.gymapp.repository.model

data class ProfileUpdateModel(
    var username: String?,
    val profileImageURL: String?,
    var age: String?,
    var weight: Double?,
    var targetWeight: Double?
    )