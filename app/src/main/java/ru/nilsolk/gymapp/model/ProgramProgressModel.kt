package ru.nilsolk.gymapp.model

import android.provider.ContactsContract.CommonDataKinds.Email

data class ProgramProgressModel(
    var countOfDays: Int,
    var lastToDoDate: String,
    val email: Email,
    var programDayNow: Int,
    var programName: String
)
